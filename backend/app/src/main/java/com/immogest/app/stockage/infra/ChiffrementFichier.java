package com.immogest.app.stockage.infra;

import com.immogest.app.shared.ErreurMetier;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Chiffrement AES-256-GCM des fichiers sensibles. Format : {@code IV (12 octets) || chiffré+tag}. */
@Component
public class ChiffrementFichier {

    private static final int IV_OCTETS = 12;
    private static final int TAG_BITS = 128;

    private final SecureRandom aleatoire = new SecureRandom();
    private final SecretKey cle;

    ChiffrementFichier(StockageProperties proprietes) {
        String base64 = proprietes.cleChiffrement();
        if (base64 == null || base64.isBlank()) {
            this.cle = null;
        } else {
            byte[] octets = Base64.getDecoder().decode(base64.trim());
            if (octets.length != 32) {
                throw new IllegalStateException("immogest.stockage.cle-chiffrement doit contenir 32 octets (base64)");
            }
            this.cle = new SecretKeySpec(octets, "AES");
        }
    }

    public byte[] chiffrer(byte[] clair) {
        try {
            byte[] iv = new byte[IV_OCTETS];
            aleatoire.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, cleObligatoire(), new GCMParameterSpec(TAG_BITS, iv));
            byte[] chiffre = cipher.doFinal(clair);
            byte[] resultat = new byte[IV_OCTETS + chiffre.length];
            System.arraycopy(iv, 0, resultat, 0, IV_OCTETS);
            System.arraycopy(chiffre, 0, resultat, IV_OCTETS, chiffre.length);
            return resultat;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Chiffrement impossible", e);
        }
    }

    public byte[] dechiffrer(byte[] donnees) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, cleObligatoire(), new GCMParameterSpec(TAG_BITS, donnees, 0, IV_OCTETS));
            return cipher.doFinal(donnees, IV_OCTETS, donnees.length - IV_OCTETS);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Déchiffrement impossible (clé incorrecte ou fichier altéré)", e);
        }
    }

    private SecretKey cleObligatoire() {
        if (cle == null) {
            throw new ErreurMetier("STOCKAGE_CLE_CHIFFREMENT_ABSENTE",
                    "Aucune clé de chiffrement configurée pour les fichiers sensibles", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return cle;
    }
}
