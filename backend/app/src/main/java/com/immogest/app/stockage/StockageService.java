package com.immogest.app.stockage;

import com.immogest.app.shared.Acteur;
import com.immogest.app.shared.ControleAccesFichier;
import com.immogest.app.shared.ErreurMetier;
import com.immogest.app.shared.FileStorage;
import com.immogest.app.stockage.domain.Fichier;
import com.immogest.app.stockage.infra.ChiffrementFichier;
import com.immogest.app.stockage.infra.FichierRepository;
import com.immogest.app.stockage.infra.StockageProperties;
import com.immogest.app.stockage.infra.ValidationFichier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * API publique du module : les autres modules envoient et suppriment leurs fichiers par ce service.
 * Clés d'objets : {@code {module}/{entite}/{id}/{uuid}.{ext}} ; le nom fourni par l'utilisateur reste en base.
 */
@Service
public class StockageService {

    private static final Duration DUREE_URL_PRESIGNEE = Duration.ofMinutes(5);

    private final FileStorage stockage;
    private final FichierRepository repository;
    private final ValidationFichier validation;
    private final ChiffrementFichier chiffrement;
    private final StockageProperties proprietes;
    private final Map<String, ControleAccesFichier> controles = new HashMap<>();

    StockageService(FileStorage stockage, FichierRepository repository, ValidationFichier validation,
            ChiffrementFichier chiffrement, StockageProperties proprietes, List<ControleAccesFichier> controles) {
        this.stockage = stockage;
        this.repository = repository;
        this.validation = validation;
        this.chiffrement = chiffrement;
        this.proprietes = proprietes;
        controles.forEach(c -> this.controles.put(c.module(), c));
    }

    /**
     * Valide, redimensionne (images), chiffre (si {@code sensible}) puis stocke un fichier.
     *
     * @param module  module propriétaire (ex. {@code patrimoine}), qui fournit le {@link ControleAccesFichier}
     * @param entite  type d'entité rattachée (ex. {@code immeuble})
     * @param sensible chiffrer avant envoi (pièces d'identité)
     */
    @Transactional
    public FichierDto enregistrer(String module, String entite, UUID entiteId, String nomOrigine,
            InputStream contenu, boolean sensible) {
        byte[] brut = lire(contenu);
        var valide = validation.valider(brut);
        byte[] aEnvoyer = sensible ? chiffrement.chiffrer(valide.contenu()) : valide.contenu();

        String cle = "%s/%s/%s/%s.%s".formatted(module, entite, entiteId, UUID.randomUUID(), valide.extension());
        stockage.put(cle, new ByteArrayInputStream(aEnvoyer), sensible ? "application/octet-stream" : valide.typeMime(),
                aEnvoyer.length);
        try {
            Fichier fichier = repository.saveAndFlush(new Fichier(cle, nettoyerNom(nomOrigine), valide.typeMime(),
                    valide.contenu().length, sha256(valide.contenu()), sensible, module, entite, entiteId));
            return dto(fichier);
        } catch (RuntimeException e) {
            stockage.delete(cle);
            throw e;
        }
    }

    /** Télécharge un fichier après vérification des droits par le module propriétaire. */
    @Transactional(readOnly = true)
    public FichierTelecharge telecharger(UUID id, Acteur acteur) {
        Fichier fichier = repository.findById(id).orElseThrow(() ->
                new ErreurMetier("FICHIER_INTROUVABLE", "Fichier introuvable", HttpStatus.NOT_FOUND));
        ControleAccesFichier controle = controles.get(fichier.getModuleProprietaire());
        if (controle == null || !controle.peutLire(fichier.getEntite(), fichier.getEntiteId(), acteur)) {
            throw new ErreurMetier("FICHIER_ACCES_REFUSE", "Accès au fichier refusé", HttpStatus.FORBIDDEN);
        }
        if (proprietes.urlPresignee() && !fichier.isChiffre()) {
            return new FichierTelecharge(dto(fichier), null,
                    Optional.of(stockage.urlTemporaire(fichier.getCle(), DUREE_URL_PRESIGNEE)));
        }
        InputStream flux = stockage.get(fichier.getCle());
        if (fichier.isChiffre()) {
            flux = new ByteArrayInputStream(chiffrement.dechiffrer(lire(flux)));
        }
        return new FichierTelecharge(dto(fichier), flux, Optional.empty());
    }

    @Transactional
    public void supprimer(UUID id) {
        repository.findById(id).ifPresent(f -> {
            repository.delete(f);
            stockage.delete(f.getCle());
        });
    }

    private static byte[] lire(InputStream flux) {
        try (flux) {
            byte[] donnees = flux.readNBytes((int) ValidationFichier.TAILLE_MAX + 1);
            return donnees;
        } catch (IOException e) {
            throw new ErreurMetier("FICHIER_LECTURE_IMPOSSIBLE", "Lecture du fichier impossible", HttpStatus.BAD_REQUEST);
        }
    }

    private static String nettoyerNom(String nom) {
        String n = nom == null ? "fichier" : nom.replaceAll("[\\\\/\\p{Cntrl}]", "_").strip();
        return n.isEmpty() ? "fichier" : n.substring(0, Math.min(n.length(), 255));
    }

    private static String sha256(byte[] donnees) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(donnees));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static FichierDto dto(Fichier f) {
        return new FichierDto(f.getId(), f.getNomOrigine(), f.getTypeMime(), f.getTaille());
    }
}
