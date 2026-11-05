package com.immogest.licensing;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Contenu d'une licence (CLAUDE.md §6). Les valeurs sont normalisées à la construction (texte en NFC, fonctionnalités
 * triées et sans doublon, empreinte en minuscules) : signature et vérification partent donc toujours de la même forme.
 *
 * @param licenseId        identifiant unique de la licence
 * @param client           nom du client
 * @param palierLots       nombre maximal de lots
 * @param dateDebut        premier jour de validité
 * @param dateExpiration   dernier jour de validité (inclus)
 * @param empreinteServeur empreinte du serveur auquel la licence est liée (voir {@link EmpreinteServeur})
 * @param fonctionnalites  fonctionnalités activées
 */
public record Licence(
        String licenseId,
        String client,
        int palierLots,
        LocalDate dateDebut,
        LocalDate dateExpiration,
        String empreinteServeur,
        List<String> fonctionnalites) {

    static final int LONGUEUR_MAX = 200;
    static final int FONCTIONNALITES_MAX = 100;

    public Licence {
        licenseId = texte(licenseId, "licenseId");
        client = texte(client, "client");
        empreinteServeur = texte(empreinteServeur, "empreinteServeur").toLowerCase(Locale.ROOT);
        if (palierLots <= 0) {
            throw new IllegalArgumentException("palierLots doit être positif");
        }
        if (dateDebut == null || dateExpiration == null) {
            throw new IllegalArgumentException("dateDebut et dateExpiration sont obligatoires");
        }
        if (dateExpiration.isBefore(dateDebut)) {
            throw new IllegalArgumentException("dateExpiration ne peut précéder dateDebut");
        }
        List<String> brutes = fonctionnalites == null ? List.of() : fonctionnalites;
        if (brutes.size() > FONCTIONNALITES_MAX) {
            throw new IllegalArgumentException("trop de fonctionnalités");
        }
        fonctionnalites = brutes.stream().map(f -> texte(f, "fonctionnalite")).distinct().sorted().toList();
    }

    private static String texte(String valeur, String champ) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(champ + " est obligatoire");
        }
        String normalise = Normalizer.normalize(valeur.strip(), Normalizer.Form.NFC);
        if (normalise.length() > LONGUEUR_MAX) {
            throw new IllegalArgumentException(champ + " est trop long");
        }
        return normalise;
    }

    public boolean estExpireeAu(LocalDate date) {
        return date.isAfter(dateExpiration);
    }

    public boolean estActiveAu(LocalDate date) {
        return !date.isBefore(dateDebut) && !date.isAfter(dateExpiration);
    }

    /** Jours restants avant l'expiration (0 le dernier jour, négatif une fois expirée). */
    public long joursRestantsAu(LocalDate date) {
        return java.time.temporal.ChronoUnit.DAYS.between(date, dateExpiration);
    }
}
