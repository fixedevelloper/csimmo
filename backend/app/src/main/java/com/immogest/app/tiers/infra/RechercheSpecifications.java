package com.immogest.app.tiers.infra;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/** Recherche rapide par nom (contient, sans casse) ou par téléphone (chiffres saisis contenus dans le numéro). */
public final class RechercheSpecifications {

    private RechercheSpecifications() {
    }

    /** Entités possédant les attributs {@code nom}, {@code telephone} et {@code archive}. */
    public static <T> Specification<T> parNomOuTelephone(String q, boolean inclureArchives) {
        return (racine, requete, cb) -> {
            List<Predicate> criteres = new ArrayList<>();
            if (!inclureArchives) {
                criteres.add(cb.isFalse(racine.get("archive")));
            }
            if (q != null && !q.isBlank()) {
                String texte = echapper(q.strip().toLowerCase());
                List<Predicate> ou = new ArrayList<>();
                ou.add(cb.like(cb.lower(racine.get("nom")), "%" + texte + "%", '\\'));
                String chiffres = q.replaceAll("[^0-9]", "");
                if (chiffres.length() >= 3) {
                    ou.add(cb.like(racine.get("telephone"), "%" + chiffres + "%"));
                }
                criteres.add(cb.or(ou.toArray(Predicate[]::new)));
            }
            return cb.and(criteres.toArray(Predicate[]::new));
        };
    }

    private static String echapper(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
