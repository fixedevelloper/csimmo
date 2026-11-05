package com.immogest.licenseserver;

import static com.immogest.licenseserver.Modeles.*;

import com.immogest.licensing.Licence;
import com.immogest.licensing.LicenceSignee;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ServiceLicences {

    private static final String SEP = "\u001f";
    private static final String SELECT_LICENCE = """
            SELECT l.id, l.client_id, c.nom AS client_nom, l.statut, l.motif_revocation, l.transferts, l.derniere_verification, l.version_application,
                   e.palier_lots, e.date_debut, e.date_expiration, e.empreinte_serveur, e.fonctionnalites
              FROM ls_licence l
              JOIN ls_client c ON c.id = l.client_id
              JOIN LATERAL (SELECT * FROM ls_emission WHERE licence_id = l.id ORDER BY created_at DESC, id LIMIT 1) e ON true
            """;

    private final JdbcTemplate jdbc;
    private final Signataire signataire;
    private final Proprietes proprietes;
    private final Clock horloge;

    ServiceLicences(JdbcTemplate jdbc, Signataire signataire, Proprietes proprietes, Clock horloge) {
        this.jdbc = jdbc;
        this.signataire = signataire;
        this.proprietes = proprietes;
        this.horloge = horloge;
    }

    private LocalDate aujourdhui() {
        return LocalDate.now(horloge.withZone(ZoneId.of("Africa/Douala")));
    }

    private RowMapper<LicenceDto> licenceMapper() {
        LocalDate auj = aujourdhui();
        return (ResultSet rs, int i) -> mapper(rs, auj);
    }

    private static LicenceDto mapper(ResultSet rs, LocalDate auj) throws SQLException {
        LocalDate expiration = rs.getObject("date_expiration", LocalDate.class);
        String f = rs.getString("fonctionnalites");
        return new LicenceDto(rs.getObject("id", UUID.class), rs.getObject("client_id", UUID.class), rs.getString("client_nom"), rs.getString("statut"),
                rs.getInt("palier_lots"), rs.getObject("date_debut", LocalDate.class), expiration, rs.getString("empreinte_serveur"),
                f.isEmpty() ? List.of() : Arrays.asList(f.split(SEP)), ChronoUnit.DAYS.between(auj, expiration), rs.getInt("transferts"),
                rs.getObject("derniere_verification", java.time.OffsetDateTime.class), rs.getString("version_application"), rs.getString("motif_revocation"));
    }

    // ------------------------------------------------------------------ clients

    @Transactional
    ClientDto creerClient(ClientRequete r) {
        UUID id = UUID.randomUUID();
        jdbc.update("INSERT INTO ls_client (id, nom, email, telephone, pays, notes) VALUES (?,?,?,?,?,?)", id, r.nom().strip(), r.email(), r.telephone(), r.pays(), r.notes());
        return client(id);
    }

    @Transactional
    ClientDto modifierClient(UUID id, ClientRequete r) {
        client(id);
        jdbc.update("UPDATE ls_client SET nom=?, email=?, telephone=?, pays=?, notes=? WHERE id=?", r.nom().strip(), r.email(), r.telephone(), r.pays(), r.notes(), id);
        return client(id);
    }

    ClientDto client(UUID id) {
        return jdbc.query("SELECT * FROM ls_client WHERE id = ?", (rs, i) -> new ClientDto(rs.getObject("id", UUID.class), rs.getString("nom"), rs.getString("email"),
                rs.getString("telephone"), rs.getString("pays"), rs.getString("notes"), rs.getObject("created_at", java.time.OffsetDateTime.class)), id).stream().findFirst()
                .orElseThrow(() -> new ErreurMetier(HttpStatus.NOT_FOUND, "CLIENT_INTROUVABLE", "Client introuvable"));
    }

    PageReponse<ClientDto> clients(String q, int page, int size) {
        String filtre = q == null || q.isBlank() ? "%" : "%" + q.strip().toLowerCase() + "%";
        long total = jdbc.queryForObject("SELECT count(*) FROM ls_client WHERE lower(nom) LIKE ?", Long.class, filtre);
        List<ClientDto> l = jdbc.query("SELECT * FROM ls_client WHERE lower(nom) LIKE ? ORDER BY lower(nom), id LIMIT ? OFFSET ?",
                (rs, i) -> new ClientDto(rs.getObject("id", UUID.class), rs.getString("nom"), rs.getString("email"), rs.getString("telephone"), rs.getString("pays"),
                        rs.getString("notes"), rs.getObject("created_at", java.time.OffsetDateTime.class)), filtre, size, (long) page * size);
        return new PageReponse<>(l, page, size, total);
    }

    // ------------------------------------------------------------------ licences

    private Optional<LicenceDto> trouver(UUID id) {
        return jdbc.query(SELECT_LICENCE + " WHERE l.id = ?", licenceMapper(), id).stream().findFirst();
    }

    LicenceDto licence(UUID id) {
        return trouver(id).orElseThrow(() -> new ErreurMetier(HttpStatus.NOT_FOUND, "LICENCE_INTROUVABLE", "Licence introuvable"));
    }

    PageReponse<LicenceDto> licences(UUID clientId, String statut, Integer expirantSous, int page, int size) {
        StringBuilder where = new StringBuilder(" WHERE true");
        java.util.List<Object> p = new java.util.ArrayList<>();
        if (clientId != null) {
            where.append(" AND l.client_id = ?");
            p.add(clientId);
        }
        if (statut != null && !statut.isBlank()) {
            where.append(" AND l.statut = ?");
            p.add(statut);
        }
        if (expirantSous != null) {
            LocalDate auj = aujourdhui();
            // inclut les licences déjà expirées mais non révoquées : ce sont les plus urgentes à relancer
            where.append(" AND l.statut = 'ACTIVE' AND e.date_expiration <= ?");
            p.add(auj.plusDays(expirantSous));
        }
        long total = jdbc.queryForObject("SELECT count(*) FROM (" + SELECT_LICENCE + where + ") t", Long.class, p.toArray());
        p.add(size);
        p.add((long) page * size);
        List<LicenceDto> l = jdbc.query(SELECT_LICENCE + where + " ORDER BY e.date_expiration, l.id LIMIT ? OFFSET ?", licenceMapper(), p.toArray());
        return new PageReponse<>(l, page, size, total);
    }

    @Transactional
    EmissionDto emettre(EmissionRequete r, String par) {
        client(r.clientId());
        UUID id = UUID.randomUUID();
        Licence licence = new Licence(id.toString(), clientNom(r.clientId()), r.palierLots(), r.dateDebut(), r.dateExpiration(), r.empreinteServeur(), r.fonctionnalites());
        jdbc.update("INSERT INTO ls_licence (id, client_id) VALUES (?,?)", id, r.clientId());
        UUID emission = enregistrerEmission(id, "EMISSION", licence, par);
        jdbc.update("UPDATE ls_licence SET derniere_emission_livree = ? WHERE id = ?", emission, id); // le premier fichier est remis à la main
        return new EmissionDto(licence(id), fichierCourant(id));
    }

    private String clientNom(UUID clientId) {
        return client(clientId).nom();
    }

    private UUID enregistrerEmission(UUID licenceId, String motif, Licence l, String par) {
        UUID id = UUID.randomUUID();
        String contenu = signataire.signer(l);
        jdbc.update("INSERT INTO ls_emission (id, licence_id, motif, palier_lots, date_debut, date_expiration, empreinte_serveur, fonctionnalites, contenu, emise_par, created_at) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?, clock_timestamp())", id, licenceId, motif, l.palierLots(), l.dateDebut(), l.dateExpiration(), l.empreinteServeur(),
                String.join(SEP, l.fonctionnalites()), contenu, par);
        return id;
    }

    /** Verrou sur la licence : deux modifications simultanées se suivent au lieu de s'écraser. */
    private LicenceDto verrouiller(UUID id) {
        jdbc.query("SELECT id FROM ls_licence WHERE id = ? FOR UPDATE", (rs, i) -> 1, id);
        return licence(id);
    }

    private static void exigerActive(LicenceDto l) {
        if (!"ACTIVE".equals(l.statut())) {
            throw new ErreurMetier(HttpStatus.CONFLICT, "LICENCE_REVOQUEE", "Cette licence est révoquée");
        }
    }

    @Transactional
    EmissionDto renouveler(UUID id, RenouvellementRequete r, String par) {
        LicenceDto l = verrouiller(id);
        exigerActive(l);
        if (!r.dateExpiration().isAfter(l.dateExpiration())) {
            throw new ErreurMetier(HttpStatus.CONFLICT, "RENOUVELLEMENT_DATE_INVALIDE", "La nouvelle expiration doit suivre l'expiration actuelle");
        }
        Licence nouvelle = new Licence(id.toString(), l.clientNom(), r.palierLots() != null ? r.palierLots() : l.palierLots(), l.dateDebut(), r.dateExpiration(),
                l.empreinteServeur(), r.fonctionnalites() != null ? r.fonctionnalites() : l.fonctionnalites());
        enregistrerEmission(id, "RENOUVELLEMENT", nouvelle, par);
        return new EmissionDto(licence(id), fichierCourant(id));
    }

    @Transactional
    EmissionDto transferer(UUID id, TransfertRequete r, String par) {
        LicenceDto l = verrouiller(id);
        exigerActive(l);
        if (l.empreinteServeur().equalsIgnoreCase(r.empreinteServeur())) {
            throw new ErreurMetier(HttpStatus.CONFLICT, "TRANSFERT_MEME_EMPREINTE", "La nouvelle empreinte est identique à l'actuelle");
        }
        if (l.transferts() >= proprietes.transfertsMax()) {
            throw new ErreurMetier(HttpStatus.CONFLICT, "TRANSFERT_LIMITE_ATTEINTE", "Nombre maximal de transferts atteint");
        }
        Licence nouvelle = new Licence(id.toString(), l.clientNom(), l.palierLots(), l.dateDebut(), l.dateExpiration(), r.empreinteServeur(), l.fonctionnalites());
        enregistrerEmission(id, "TRANSFERT", nouvelle, par);
        jdbc.update("UPDATE ls_licence SET transferts = transferts + 1 WHERE id = ?", id);
        return new EmissionDto(licence(id), fichierCourant(id));
    }

    @Transactional
    LicenceDto revoquer(UUID id, String motif) {
        LicenceDto l = verrouiller(id);
        exigerActive(l);
        jdbc.update("UPDATE ls_licence SET statut='REVOQUEE', motif_revocation=?, revoquee_le=now() WHERE id=?", motif.strip(), id);
        return licence(id);
    }

    String fichierCourant(UUID id) {
        licence(id);
        return jdbc.queryForObject("SELECT contenu FROM ls_emission WHERE licence_id = ? ORDER BY created_at DESC, id LIMIT 1", String.class, id);
    }

    List<HistoriqueEmissionDto> historique(UUID id) {
        licence(id);
        return jdbc.query("SELECT id, motif, palier_lots, date_debut, date_expiration, empreinte_serveur, emise_par, created_at FROM ls_emission WHERE licence_id = ? ORDER BY created_at, id",
                (rs, i) -> new HistoriqueEmissionDto(rs.getObject("id", UUID.class), rs.getString("motif"), rs.getInt("palier_lots"), rs.getObject("date_debut", LocalDate.class),
                        rs.getObject("date_expiration", LocalDate.class), rs.getString("empreinte_serveur"), rs.getString("emise_par"),
                        rs.getObject("created_at", java.time.OffsetDateTime.class)), id);
    }

    // ------------------------------------------------------------------ vérification (appelée chaque semaine par les installations)

    /**
     * Statut d'une licence pour une installation. Une licence inconnue et un identifiant mal formé donnent la même réponse. Une empreinte qui n'est pas
     * celle de la licence (copie sur un autre serveur, ou licence transférée) est traitée comme révoquée pour CE serveur. Un fichier renouvelé est remis
     * une fois ; s'il n'est pas importé, le gestionnaire le télécharge depuis le portail.
     */
    @Transactional
    VerificationReponse verifier(VerificationRequete r) {
        UUID id;
        try {
            id = UUID.fromString(r.licenseId());
        } catch (IllegalArgumentException e) {
            return new VerificationReponse("INCONNUE", null);
        }
        Optional<LicenceDto> trouvee = jdbc.query(SELECT_LICENCE + " WHERE l.id = ?", licenceMapper(), id).stream().findFirst();
        if (trouvee.isEmpty()) {
            return new VerificationReponse("INCONNUE", null);
        }
        LicenceDto l = trouvee.get();
        jdbc.update("UPDATE ls_licence SET derniere_verification = now(), version_application = ? WHERE id = ?", r.versionApplication(), id);
        if (!"ACTIVE".equals(l.statut()) || !l.empreinteServeur().equalsIgnoreCase(r.empreinteServeur().strip())) {
            return new VerificationReponse("REVOQUEE", null);
        }
        List<String> aLivrer = jdbc.query("""
                SELECT e.contenu FROM ls_emission e JOIN ls_licence l ON l.id = e.licence_id
                 WHERE e.licence_id = ? AND e.id = (SELECT id FROM ls_emission WHERE licence_id = ? ORDER BY created_at DESC, id LIMIT 1)
                   AND l.derniere_emission_livree IS DISTINCT FROM e.id""", (rs, i) -> rs.getString(1), id, id);
        if (aLivrer.isEmpty()) {
            return new VerificationReponse("VALIDE", null);
        }
        jdbc.update("UPDATE ls_licence SET derniere_emission_livree = (SELECT id FROM ls_emission WHERE licence_id = ? ORDER BY created_at DESC, id LIMIT 1) WHERE id = ?", id, id);
        return new VerificationReponse("VALIDE", aLivrer.get(0));
    }

    // ------------------------------------------------------------------ paiements de licence

    @Transactional
    PaiementDto enregistrerPaiement(UUID licenceId, PaiementRequete r, String par) {
        licence(licenceId);
        UUID id = UUID.randomUUID();
        jdbc.update("INSERT INTO ls_paiement (id, licence_id, montant, mode, reference, date_paiement, note, enregistre_par) VALUES (?,?,?,?,?,?,?,?)", id, licenceId, r.montant(),
                r.mode().strip(), r.reference(), r.datePaiement(), r.note(), par);
        return paiements(licenceId, 0, 1000).content().stream().filter(p -> p.id().equals(id)).findFirst().orElseThrow();
    }

    PageReponse<PaiementDto> paiements(UUID licenceId, int page, int size) {
        licence(licenceId);
        long total = jdbc.queryForObject("SELECT count(*) FROM ls_paiement WHERE licence_id = ?", Long.class, licenceId);
        List<PaiementDto> l = jdbc.query("SELECT * FROM ls_paiement WHERE licence_id = ? ORDER BY date_paiement DESC, created_at DESC LIMIT ? OFFSET ?",
                (rs, i) -> new PaiementDto(rs.getObject("id", UUID.class), rs.getObject("licence_id", UUID.class), rs.getLong("montant"), rs.getString("mode"), rs.getString("reference"),
                        rs.getObject("date_paiement", LocalDate.class), rs.getString("note"), rs.getString("enregistre_par"), rs.getObject("created_at", java.time.OffsetDateTime.class)),
                licenceId, size, (long) page * size);
        return new PageReponse<>(l, page, size, total);
    }

    // ------------------------------------------------------------------ versions publiées

    private static VersionDto version(ResultSet rs, int i) throws SQLException {
        return new VersionDto(rs.getObject("id", UUID.class), rs.getString("numero"), rs.getString("notes"), rs.getString("url_telechargement"), rs.getString("sha256"),
                rs.getString("publiee_par"), rs.getObject("publiee_le", java.time.OffsetDateTime.class));
    }

    @Transactional
    VersionDto publier(VersionRequete r, String par) {
        UUID id = UUID.randomUUID();
        try {
            jdbc.update("INSERT INTO ls_version (id, numero, notes, url_telechargement, sha256, publiee_par) VALUES (?,?,?,?,?,?)", id, r.numero(), r.notes(), r.urlTelechargement(),
                    r.sha256().toLowerCase(), par);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new ErreurMetier(HttpStatus.CONFLICT, "VERSION_EXISTE", "Cette version est déjà publiée");
        }
        return jdbc.queryForObject("SELECT * FROM ls_version WHERE id = ?", ServiceLicences::version, id);
    }

    PageReponse<VersionDto> versions(int page, int size) {
        long total = jdbc.queryForObject("SELECT count(*) FROM ls_version", Long.class);
        return new PageReponse<>(jdbc.query("SELECT * FROM ls_version ORDER BY publiee_le DESC, id LIMIT ? OFFSET ?", ServiceLicences::version, size, (long) page * size), page, size, total);
    }

    Optional<VersionDto> derniereVersion() {
        return jdbc.query("SELECT * FROM ls_version ORDER BY publiee_le DESC, id LIMIT 1", ServiceLicences::version).stream().findFirst();
    }
}
