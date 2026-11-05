package com.immogest.licensing;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/** Ed25519 avec l'API native du JDK (aucune bibliothèque tierce). */
public final class Ed25519 {

    private static final String ALGORITHME = "Ed25519";

    private Ed25519() {
    }

    public static KeyPair genererPaire() {
        try {
            return KeyPairGenerator.getInstance(ALGORITHME).generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Ed25519 indisponible", e);
        }
    }

    /** Clé publique au format X.509 (DER) en base64 : c'est la forme embarquée dans l'application. */
    public static String encoder(PublicKey cle) {
        return Base64.getEncoder().encodeToString(cle.getEncoded());
    }

    /** Clé privée au format PKCS#8 (DER) en base64. À garder uniquement chez l'éditeur. */
    public static String encoder(PrivateKey cle) {
        return Base64.getEncoder().encodeToString(cle.getEncoded());
    }

    public static PublicKey decoderClePublique(String base64) {
        try {
            PublicKey cle = KeyFactory.getInstance(ALGORITHME)
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64.strip())));
            exigerEd25519(cle.getAlgorithm());
            return cle;
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Clé publique Ed25519 invalide", e);
        }
    }

    public static PrivateKey decoderClePrivee(String base64) {
        try {
            PrivateKey cle = KeyFactory.getInstance(ALGORITHME)
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64.strip())));
            exigerEd25519(cle.getAlgorithm());
            return cle;
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Clé privée Ed25519 invalide", e);
        }
    }

    public static byte[] signer(byte[] donnees, PrivateKey clePrivee) {
        try {
            Signature signature = Signature.getInstance(ALGORITHME);
            signature.initSign(clePrivee);
            signature.update(donnees);
            return signature.sign();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Signature impossible", e);
        }
    }

    /** Vrai seulement si la signature est valide ; toute anomalie (taille, format) donne faux. */
    public static boolean verifier(byte[] donnees, byte[] signature, PublicKey clePublique) {
        try {
            Signature verification = Signature.getInstance(ALGORITHME);
            verification.initVerify(clePublique);
            verification.update(donnees);
            return verification.verify(signature);
        } catch (GeneralSecurityException e) {
            return false;
        }
    }

    private static void exigerEd25519(String algorithme) {
        if (!"Ed25519".equalsIgnoreCase(algorithme) && !"EdDSA".equalsIgnoreCase(algorithme)) {
            throw new IllegalArgumentException("La clé n'est pas une clé Ed25519");
        }
    }
}
