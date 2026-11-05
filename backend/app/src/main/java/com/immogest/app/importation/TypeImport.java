package com.immogest.app.importation;

import java.util.List;

/** Types de données importables, dans l'ordre d'import. {@code feuille} est le nom de l'onglet du modèle Excel. */
public enum TypeImport {
    IMMEUBLES("Immeubles", List.of(Colonne.req("nom"), Colonne.req("adresse"), Colonne.opt("quartier"), Colonne.req("ville"), Colonne.opt("description"))),
    LOTS("Lots", List.of(Colonne.req("immeuble"), Colonne.req("reference"), Colonne.req("type"), Colonne.opt("surface_m2"), Colonne.opt("pieces"), Colonne.opt("loyer_indicatif"))),
    LOCATAIRES("Locataires", List.of(Colonne.req("nom"), Colonne.req("telephone"), Colonne.opt("email"), Colonne.opt("profession"), Colonne.opt("adresse"),
            Colonne.opt("date_naissance"), Colonne.opt("numero_cni"))),
    BAUX("Baux", List.of(Colonne.req("immeuble"), Colonne.req("lot"), Colonne.req("locataire_telephone"), Colonne.req("date_debut"), Colonne.opt("date_fin"), Colonne.req("loyer"),
            Colonne.opt("charges"), Colonne.opt("periodicite"), Colonne.opt("jour_echeance"), Colonne.opt("caution"), Colonne.opt("preavis_mois"))),
    SOLDES("Soldes", List.of(Colonne.req("immeuble"), Colonne.req("lot"), Colonne.req("solde_du"), Colonne.opt("date_reprise")));

    private final String feuille;
    private final List<Colonne> colonnes;

    TypeImport(String feuille, List<Colonne> colonnes) {
        this.feuille = feuille;
        this.colonnes = colonnes;
    }

    public String feuille() {
        return feuille;
    }

    public List<Colonne> colonnes() {
        return colonnes;
    }

    /** @param requis colonne obligatoire (son en-tête doit exister ; chaque ligne doit la renseigner) */
    public record Colonne(String nom, boolean requis) {
        static Colonne req(String nom) {
            return new Colonne(nom, true);
        }

        static Colonne opt(String nom) {
            return new Colonne(nom, false);
        }
    }
}
