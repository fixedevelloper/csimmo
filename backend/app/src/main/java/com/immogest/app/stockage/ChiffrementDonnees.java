package com.immogest.app.stockage;

import com.immogest.app.stockage.infra.ChiffrementFichier;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Service;

/**
 * Chiffrement AES-256-GCM de petites données textuelles (ex. numéro de CNI) avec la clé d'installation, la même que
 * pour les fichiers sensibles. Sortie : base64 de {@code IV || chiffré+tag}.
 */
@Service
public class ChiffrementDonnees {

    private final ChiffrementFichier chiffrement;

    ChiffrementDonnees(ChiffrementFichier chiffrement) {
        this.chiffrement = chiffrement;
    }

    public String chiffrer(String clair) {
        return clair == null ? null : Base64.getEncoder().encodeToString(chiffrement.chiffrer(clair.getBytes(StandardCharsets.UTF_8)));
    }

    public String dechiffrer(String chiffre) {
        return chiffre == null ? null : new String(chiffrement.dechiffrer(Base64.getDecoder().decode(chiffre)), StandardCharsets.UTF_8);
    }
}
