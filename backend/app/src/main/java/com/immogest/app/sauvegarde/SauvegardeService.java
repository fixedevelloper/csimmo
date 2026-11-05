package com.immogest.app.sauvegarde;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.licence.EtatLicence;
import com.immogest.app.licence.EtatLicenceDto;
import com.immogest.app.licence.LicenceService;
import com.immogest.app.shared.ErreurMetier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lecture des sauvegardes (manifestes écrits par le conteneur de sauvegarde), demandes de sauvegarde / restauration et diagnostic.
 * L'identifiant d'une sauvegarde est validé par expression régulière avant tout accès au disque (pas de parcours de dossiers).
 */
@Service
@EnableConfigurationProperties(SauvegardeProperties.class)
public class SauvegardeService {

    private static final Logger LOG = LoggerFactory.getLogger(SauvegardeService.class);
    static final Pattern ID = Pattern.compile("^[0-9]{8}-[0-9]{6}(-[a-z0-9-]{1,40})?$");
    /** Sans sauvegarde depuis ce délai, l'installation est considérée comme non protégée. */
    static final Duration SAUVEGARDE_TROP_ANCIENNE = Duration.ofHours(48);
    static final long DISQUE_FAIBLE_OCTETS = 5L * 1024 * 1024 * 1024;

    private final SauvegardeProperties proprietes;
    private final LicenceService licence;
    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher evenements;
    private final Clock horloge;
    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).findAndRegisterModules();

    SauvegardeService(SauvegardeProperties proprietes, LicenceService licence, JdbcTemplate jdbc, ApplicationEventPublisher evenements, Clock horloge) {
        this.proprietes = proprietes;
        this.licence = licence;
        this.jdbc = jdbc;
        this.evenements = evenements;
        this.horloge = horloge;
    }

    // ------------------------------------------------------------------ lecture

    public EtatSauvegardesDto etat() {
        return new EtatSauvegardesDto(lister(), operation(), proprietes.heure(), proprietes.version());
    }

    public List<SauvegardeDto> lister() {
        Path dossier = Path.of(proprietes.repertoire());
        if (!Files.isDirectory(dossier)) {
            return List.of();
        }
        try (Stream<Path> s = Files.list(dossier)) {
            return s.filter(Files::isDirectory).filter(p -> ID.matcher(p.getFileName().toString()).matches()).map(this::lire).filter(java.util.Objects::nonNull)
                    .sorted(Comparator.comparing(SauvegardeDto::date).reversed()).toList();
        } catch (IOException e) {
            LOG.warn("Lecture du dossier des sauvegardes impossible : {}", e.getMessage());
            return List.of();
        }
    }

    /** Une sauvegarde sans manifeste (ou au manifeste illisible) est incomplète : elle n'est pas listée. */
    private SauvegardeDto lire(Path dossier) {
        Path manifeste = dossier.resolve("MANIFEST.json");
        if (!Files.isRegularFile(manifeste)) {
            return null;
        }
        try {
            JsonNode n = mapper.readTree(manifeste.toFile());
            if (!"COMPLETE".equals(n.path("statut").asText())) {
                return null;
            }
            String version = n.path("version").asText("");
            return new SauvegardeDto(n.path("id").asText(dossier.getFileName().toString()), OffsetDateTime.parse(n.path("date").asText()), version, n.path("etiquette").asText(""),
                    n.path("tailleOctets").asLong(), n.path("objets").asLong(), n.path("externe").asText("NON_CONFIGURE"), version.equals(proprietes.version()));
        } catch (IOException | RuntimeException e) {
            LOG.warn("Manifeste illisible ({}) : {}", dossier.getFileName(), e.getMessage());
            return null;
        }
    }

    /** Dernière opération du conteneur de sauvegarde, ou null. */
    public OperationSauvegardeDto operation() {
        Path fichier = Path.of(proprietes.demandes(), "etat.json");
        if (!Files.isRegularFile(fichier)) {
            return null;
        }
        try {
            JsonNode n = mapper.readTree(fichier.toFile());
            String fin = n.path("fin").asText(null);
            return new OperationSauvegardeDto(n.path("operation").asText(), n.path("statut").asText(), n.path("id").asText(), OffsetDateTime.parse(n.path("debut").asText()),
                    fin == null ? null : OffsetDateTime.parse(fin), n.path("message").asText(""));
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    // ------------------------------------------------------------------ demandes

    private void exigerAucuneOperationEnCours() {
        OperationSauvegardeDto op = operation();
        if (op != null && "EN_COURS".equals(op.statut()) && op.debut().isAfter(OffsetDateTime.now(horloge).minusHours(3))) {
            throw new ErreurMetier("SAUVEGARDE_OPERATION_EN_COURS", "Une sauvegarde ou une restauration est déjà en cours", HttpStatus.CONFLICT);
        }
    }

    private void deposer(String nom) {
        Path dossier = Path.of(proprietes.demandes());
        try {
            Files.createDirectories(dossier);
            // CREATE_NEW : une demande identique déjà en attente n'est pas dupliquée
            Files.writeString(dossier.resolve(nom + ".demande"), OffsetDateTime.now(horloge).toString(), StandardOpenOption.CREATE_NEW);
        } catch (java.nio.file.FileAlreadyExistsException e) {
            throw new ErreurMetier("SAUVEGARDE_DEMANDE_EN_ATTENTE", "Une demande est déjà en attente de traitement", HttpStatus.CONFLICT);
        } catch (IOException e) {
            LOG.error("Dépôt de la demande impossible : {}", e.getMessage());
            throw new ErreurMetier("SAUVEGARDE_SERVICE_INDISPONIBLE", "Le service de sauvegarde n'est pas joignable", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @Transactional // sans transaction, l'événement d'audit publié n'est jamais livré (écoute après commit)
    public void demanderSauvegarde(String par) {
        exigerAucuneOperationEnCours();
        deposer("sauvegarde");
        evenements.publishEvent(new EvenementsSauvegarde.SauvegardeDemandee(par));
    }

    /**
     * @param confirmation doit être exactement {@code RESTAURER}
     */
    @Transactional
    public void demanderRestauration(String id, String confirmation, String par) {
        if (!"RESTAURER".equals(confirmation)) {
            throw new ErreurMetier("SAUVEGARDE_CONFIRMATION_INVALIDE", "Tapez RESTAURER pour confirmer", HttpStatus.BAD_REQUEST);
        }
        if (id == null || !ID.matcher(id).matches()) {
            throw new ErreurMetier("SAUVEGARDE_INTROUVABLE", "Sauvegarde introuvable", HttpStatus.NOT_FOUND);
        }
        SauvegardeDto s = lister().stream().filter(x -> x.id().equals(id)).findFirst()
                .orElseThrow(() -> new ErreurMetier("SAUVEGARDE_INTROUVABLE", "Sauvegarde introuvable", HttpStatus.NOT_FOUND));
        if (!s.restaurable()) {
            throw new ErreurMetier("SAUVEGARDE_VERSION_DIFFERENTE", "Cette sauvegarde vient de la version " + s.version() + " (installée : " + proprietes.version()
                    + ") : utilisez deploy/restore.sh", HttpStatus.CONFLICT);
        }
        exigerAucuneOperationEnCours();
        deposer("restauration-" + id);
        evenements.publishEvent(new EvenementsSauvegarde.RestaurationDemandee(id, par));
    }

    // ------------------------------------------------------------------ diagnostic

    public DiagnosticDto diagnostic() {
        List<String> alertes = new ArrayList<>();
        OffsetDateTime maintenant = OffsetDateTime.now(horloge);
        List<SauvegardeDto> toutes = lister();
        SauvegardeDto derniere = toutes.isEmpty() ? null : toutes.get(0);
        Long age = derniere == null ? null : Duration.between(derniere.date(), maintenant).toHours();
        if (derniere == null) {
            alertes.add("SAUVEGARDE_ABSENTE");
        } else if (Duration.between(derniere.date(), maintenant).compareTo(SAUVEGARDE_TROP_ANCIENNE) > 0) {
            alertes.add("SAUVEGARDE_ANCIENNE");
        }
        String externe = derniere == null ? "NON_CONFIGURE" : derniere.externe();
        if ("NON_CONFIGURE".equals(externe)) {
            alertes.add("COPIE_EXTERNE_ABSENTE");
        } else if ("ECHEC".equals(externe)) {
            alertes.add("COPIE_EXTERNE_ECHEC");
        }
        OperationSauvegardeDto op = operation();
        if (op != null && "ECHEC".equals(op.statut())) {
            alertes.add("DERNIERE_OPERATION_ECHEC");
        }

        long total = 0;
        long libre = 0;
        try {
            Path dossier = Files.isDirectory(Path.of(proprietes.repertoire())) ? Path.of(proprietes.repertoire()) : Path.of(".");
            var magasin = Files.getFileStore(dossier);
            total = magasin.getTotalSpace();
            libre = magasin.getUsableSpace();
        } catch (IOException e) {
            LOG.warn("Espace disque illisible : {}", e.getMessage());
        }
        if (total > 0 && (libre < DISQUE_FAIBLE_OCTETS || libre * 10 < total)) {
            alertes.add("DISQUE_FAIBLE");
        }

        EtatLicenceDto l = licence.etat();
        if (l.etat() != EtatLicence.VALIDE) {
            alertes.add("LICENCE_INVALIDE");
        } else if (l.alerteJours() != null) {
            alertes.add("LICENCE_PROCHE");
        }
        String versionBase = jdbc.queryForObject("SHOW server_version", String.class);
        Long tailleBase = jdbc.queryForObject("SELECT pg_database_size(current_database())", Long.class);
        return new DiagnosticDto(proprietes.version(), Runtime.version().toString(), versionBase, tailleBase == null ? 0 : tailleBase, total, libre, l.etat().name(), l.joursRestants(),
                derniere == null ? null : derniere.date(), age, externe, alertes);
    }
}
