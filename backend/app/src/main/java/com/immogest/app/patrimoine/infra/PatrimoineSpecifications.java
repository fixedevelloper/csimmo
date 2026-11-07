package com.immogest.app.patrimoine.infra;

import com.immogest.app.patrimoine.FiltreImmeubles;
import com.immogest.app.patrimoine.FiltreLots;
import com.immogest.app.patrimoine.domain.Immeuble;
import com.immogest.app.patrimoine.domain.Lot;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/** Filtres de recherche : texte « contient », insensible à la casse ; les caractères % et _ saisis restent littéraux. */
public final class PatrimoineSpecifications {

    private PatrimoineSpecifications() {
    }

    /** Restriction au périmètre d'un employé : ensemble vide = aucun résultat. */
    public static Specification<Immeuble> immeublesDans(java.util.Set<java.util.UUID> ids) {
        return (racine, requete, cb) -> ids.isEmpty() ? cb.disjunction() : racine.get("id").in(ids);
    }

    public static Specification<Lot> lotsDansImmeubles(java.util.Set<java.util.UUID> immeubleIds) {
        return (racine, requete, cb) -> immeubleIds.isEmpty() ? cb.disjunction() : racine.get("immeuble").get("id").in(immeubleIds);
    }

    public static Specification<Immeuble> immeubles(FiltreImmeubles f) {
        return (racine, requete, cb) -> {
            List<Predicate> criteres = new ArrayList<>();
            if (!f.inclureArchives()) {
                criteres.add(cb.isFalse(racine.get("archive")));
            }
            if (f.proprietaireTiersId() != null) {
                criteres.add(cb.equal(racine.get("proprietaireTiersId"), f.proprietaireTiersId()));
            }
            contient(cb, racine.get("ville"), f.ville()).ifPresent(criteres::add);
            contient(cb, racine.get("quartier"), f.quartier()).ifPresent(criteres::add);
            if (present(f.q())) {
                criteres.add(cb.or(contient(cb, racine.get("nom"), f.q()).orElseThrow(),
                        contient(cb, racine.get("adresse"), f.q()).orElseThrow()));
            }
            return cb.and(criteres.toArray(Predicate[]::new));
        };
    }

    public static Specification<Lot> lots(FiltreLots f) {
        return (racine, requete, cb) -> {
            Join<Lot, Immeuble> immeuble = racine.join("immeuble");
            List<Predicate> criteres = new ArrayList<>();
            if (!f.inclureArchives()) {
                criteres.add(cb.isFalse(racine.get("archive")));
            }
            if (f.immeubleId() != null) {
                criteres.add(cb.equal(immeuble.get("id"), f.immeubleId()));
            }
            if (f.proprietaireTiersId() != null) {
                criteres.add(cb.equal(immeuble.get("proprietaireTiersId"), f.proprietaireTiersId()));
            }
            if (f.statut() != null) {
                criteres.add(cb.equal(racine.get("statut"), f.statut()));
            }
            if (f.type() != null) {
                criteres.add(cb.equal(racine.get("type"), f.type()));
            }
            contient(cb, immeuble.get("ville"), f.ville()).ifPresent(criteres::add);
            contient(cb, immeuble.get("quartier"), f.quartier()).ifPresent(criteres::add);
            if (present(f.q())) {
                criteres.add(cb.or(contient(cb, racine.get("reference"), f.q()).orElseThrow(),
                        contient(cb, immeuble.get("nom"), f.q()).orElseThrow()));
            }
            return cb.and(criteres.toArray(Predicate[]::new));
        };
    }

    private static boolean present(String valeur) {
        return valeur != null && !valeur.isBlank();
    }

    private static java.util.Optional<Predicate> contient(CriteriaBuilder cb, Expression<String> champ, String valeur) {
        if (!present(valeur)) {
            return java.util.Optional.empty();
        }
        String motif = "%" + valeur.strip().toLowerCase().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        return java.util.Optional.of(cb.like(cb.lower(champ), motif, '\\'));
    }
}
