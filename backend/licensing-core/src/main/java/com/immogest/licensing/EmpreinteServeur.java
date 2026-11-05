package com.immogest.licensing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Empreinte du serveur : SHA-256 (hexadécimal, 64 caractères) de l'identifiant de la machine et de l'identifiant
 * d'installation. Ce dernier est généré au premier démarrage puis conservé en base : il distingue deux installations
 * sur une même machine, et change quand on réinstalle (d'où le transfert de licence, règle de licence n°7).
 * L'empreinte n'est pas un secret : elle lie la licence à un serveur, elle ne l'authentifie pas.
 */
public final class EmpreinteServeur {

    private static final String PREFIXE = "immogest-empreinte-v1";

    private EmpreinteServeur() {
    }

    /** À appeler une seule fois, au premier démarrage ; le résultat est ensuite persisté. */
    public static String nouvelIdentifiantInstallation() {
        return UUID.randomUUID().toString();
    }

    public static String calculer(String identifiantMachine, String identifiantInstallation) {
        String machine = exiger(identifiantMachine, "identifiantMachine");
        String installation = exiger(identifiantInstallation, "identifiantInstallation");
        try {
            byte[] entree = (PREFIXE + "\n" + machine + "\n" + installation).getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(entree));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Empreinte de la machine courante, ou une {@link IllegalStateException} si aucun identifiant machine n'existe. */
    public static String calculerLocale(String identifiantInstallation) {
        String machine = IdentifiantMachine.detecter().orElseThrow(() -> new IllegalStateException(
                "Identifiant machine introuvable : définir la variable " + IdentifiantMachine.VARIABLE));
        return calculer(machine, identifiantInstallation);
    }

    private static String exiger(String valeur, String nom) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(nom + " est obligatoire");
        }
        return valeur.strip();
    }
}
