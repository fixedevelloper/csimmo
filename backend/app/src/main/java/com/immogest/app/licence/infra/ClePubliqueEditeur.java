package com.immogest.app.licence.infra;

import com.immogest.licensing.Ed25519;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Clé publique de l'éditeur : {@code immogest.licence.cle-publique} si définie, sinon la ressource embarquée
 * {@code licence/cle-publique-editeur.txt}. Sans clé valide, AUCUNE licence n'est acceptée (échec sûr).
 */
@Component
public class ClePubliqueEditeur {

    private static final Logger LOG = LoggerFactory.getLogger(ClePubliqueEditeur.class);
    static final String RESSOURCE = "licence/cle-publique-editeur.txt";

    private final PublicKey cle;

    ClePubliqueEditeur(LicenceProperties proprietes) {
        this.cle = charger(proprietes.clePublique(), RESSOURCE);
        if (cle == null) {
            LOG.error("Aucune clé publique de l'éditeur valide : toute licence sera refusée "
                    + "(définir IMMOGEST_LICENCE_CLE_PUBLIQUE ou renseigner {})", RESSOURCE);
        }
    }

    public Optional<PublicKey> cle() {
        return Optional.ofNullable(cle);
    }

    static PublicKey charger(String configuree, String ressource) {
        String base64 = configuree != null && !configuree.isBlank() ? configuree : lireRessource(ressource);
        if (base64 == null || base64.isBlank()) {
            return null;
        }
        try {
            return Ed25519.decoderClePublique(base64);
        } catch (IllegalArgumentException e) {
            LOG.error("Clé publique de l'éditeur illisible : {}", e.getMessage());
            return null;
        }
    }

    /** Première ligne non vide qui n'est pas un commentaire ({@code #}). */
    private static String lireRessource(String chemin) {
        ClassPathResource ressource = new ClassPathResource(chemin);
        if (!ressource.exists()) {
            return null;
        }
        try (InputStream flux = ressource.getInputStream()) {
            for (String ligne : new String(flux.readAllBytes(), StandardCharsets.UTF_8).split("\\R")) {
                String l = ligne.strip();
                if (!l.isEmpty() && !l.startsWith("#")) {
                    return l;
                }
            }
        } catch (IOException e) {
            LOG.error("Lecture de {} impossible : {}", chemin, e.getMessage());
        }
        return null;
    }
}
