package com.immogest.app.importation;

import com.immogest.app.finance.CalculEcheances;
import com.immogest.app.patrimoine.TypeLot;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Validation d'un fichier lu, sans aucune écriture : format de chaque valeur, doublons dans le fichier, doublons avec l'existant, liens entre
 * feuilles (un lot connaît son immeuble, un bail son lot et son locataire), cohérence des soldes. Toutes les erreurs sont collectées (pas seulement la
 * première) pour que l'utilisateur corrige son fichier en une fois. Les règles qui ne se vérifient qu'à l'écriture (bail déjà en vigueur sur un lot
 * existant…) sont contrôlées par les services eux-mêmes à l'import, qui rapporte alors la ligne en cause.
 */
final class Analyseur {

    /** Ce qui existe déjà dans l'installation (lecture seule). */
    interface Existant {
        boolean immeubleExiste(String cleNom);

        /** Références de lots (clés normalisées) d'un immeuble existant, ou null si l'immeuble n'existe pas. */
        Set<String> lotsDeImmeuble(String cleNom);

        boolean locataireExiste(String telephone);
    }

    record Resultat(Plan plan, List<ErreurImportDto> erreurs) {
    }

    private final Existant existant;
    private final LocalDate aujourdhui;
    private final List<ErreurImportDto> erreurs = new ArrayList<>();
    private String feuille = "";

    Analyseur(Existant existant, LocalDate aujourdhui) {
        this.existant = existant;
        this.aujourdhui = aujourdhui;
    }

    private void erreur(int ligne, String colonne, String code, String message) {
        erreurs.add(new ErreurImportDto(feuille, ligne, colonne, code, message));
    }

    private <T> T lire(Ligne l, String colonne, Function<String, T> parseur) {
        String v = l.get(colonne);
        if (v == null) {
            return null;
        }
        try {
            return parseur.apply(v);
        } catch (IllegalArgumentException e) {
            erreur(l.numero(), colonne, "VALEUR_INVALIDE", e.getMessage());
            return null;
        }
    }

    private String requis(Ligne l, String colonne) {
        String v = l.get(colonne);
        if (v == null) {
            erreur(l.numero(), colonne, "VALEUR_REQUISE", "Valeur obligatoire");
        } else if (v.length() > 300) {
            erreur(l.numero(), colonne, "VALEUR_TROP_LONGUE", "Texte trop long (300 caractères maximum)");
            return null;
        }
        return v;
    }

    Resultat analyser(Map<TypeImport, LecteurFichier.Feuille> fichier) {
        for (TypeImport t : TypeImport.values()) {
            LecteurFichier.Feuille f = fichier.get(t);
            if (f != null) {
                feuille = t.feuille();
                for (TypeImport.Colonne c : t.colonnes()) {
                    if (c.requis() && !f.entetes().contains(c.nom())) {
                        erreur(1, c.nom(), "COLONNE_MANQUANTE", "Colonne obligatoire absente : « " + c.nom() + " »");
                    }
                }
            }
        }
        boolean structureOk = erreurs.isEmpty();
        List<Plan.Imm> immeubles = new ArrayList<>();
        List<Plan.Lot> lots = new ArrayList<>();
        List<Plan.Loc> locataires = new ArrayList<>();
        List<Plan.Bail> baux = new ArrayList<>();
        Map<String, Plan.Solde> soldes = new LinkedHashMap<>();
        if (structureOk) {
            analyserImmeubles(lignes(fichier, TypeImport.IMMEUBLES), immeubles);
            analyserLots(lignes(fichier, TypeImport.LOTS), immeubles, lots);
            analyserLocataires(lignes(fichier, TypeImport.LOCATAIRES), locataires);
            analyserBaux(lignes(fichier, TypeImport.BAUX), lots, locataires, baux);
            analyserSoldes(lignes(fichier, TypeImport.SOLDES), baux, soldes);
        }
        return new Resultat(new Plan(immeubles, lots, locataires, baux, soldes), erreurs);
    }

    private List<Ligne> lignes(Map<TypeImport, LecteurFichier.Feuille> fichier, TypeImport t) {
        feuille = t.feuille();
        LecteurFichier.Feuille f = fichier.get(t);
        return f == null ? List.of() : f.lignes();
    }

    // ------------------------------------------------------------------ feuilles

    private void analyserImmeubles(List<Ligne> lignes, List<Plan.Imm> sortie) {
        Set<String> vus = new HashSet<>();
        for (Ligne l : lignes) {
            String nom = requis(l, "nom");
            String adresse = requis(l, "adresse");
            String ville = requis(l, "ville");
            if (nom == null) {
                continue;
            }
            String cle = Valeurs.cle(nom);
            if (!vus.add(cle)) {
                erreur(l.numero(), "nom", "DOUBLON_DANS_FICHIER", "Immeuble déjà présent plus haut dans le fichier : « " + nom + " »");
            } else if (existant.immeubleExiste(cle)) {
                erreur(l.numero(), "nom", "DEJA_EXISTANT", "Un immeuble « " + nom + " » existe déjà : retirez cette ligne (ses lots peuvent le référencer directement)");
            }
            sortie.add(new Plan.Imm(l.numero(), nom, adresse, l.get("quartier"), ville, l.get("description")));
        }
    }

    private void analyserLots(List<Ligne> lignes, List<Plan.Imm> immeublesFichier, List<Plan.Lot> sortie) {
        Set<String> immeublesConnus = new HashSet<>();
        immeublesFichier.forEach(i -> immeublesConnus.add(Valeurs.cle(i.nom())));
        Set<String> vus = new HashSet<>();
        Map<String, Set<String>> existantsCache = new HashMap<>();
        for (Ligne l : lignes) {
            String immeuble = requis(l, "immeuble");
            String reference = requis(l, "reference");
            String typeTexte = requis(l, "type");
            TypeLot type = null;
            if (typeTexte != null) {
                try {
                    type = TypeLot.valueOf(Valeurs.enTete(typeTexte).toUpperCase());
                } catch (IllegalArgumentException e) {
                    erreur(l.numero(), "type", "VALEUR_INVALIDE", "Type inconnu « " + typeTexte + " » (valeurs : " + String.join(", ", java.util.Arrays.stream(TypeLot.values()).map(Enum::name).toList()) + ")");
                }
            }
            var surface = lire(l, "surface_m2", Valeurs::decimal);
            Integer pieces = lire(l, "pieces", v -> Valeurs.entier(v, 100));
            Long loyer = lire(l, "loyer_indicatif", Valeurs::montant);
            if (immeuble == null || reference == null) {
                continue;
            }
            String cleImm = Valeurs.cle(immeuble);
            boolean dansFichier = immeublesConnus.contains(cleImm);
            Set<String> existants = dansFichier ? null : existantsCache.computeIfAbsent(cleImm, existant::lotsDeImmeuble);
            if (!dansFichier && existants == null) {
                erreur(l.numero(), "immeuble", "IMMEUBLE_INCONNU", "Immeuble « " + immeuble + " » introuvable (ni dans l'onglet Immeubles, ni déjà enregistré)");
                continue;
            }
            String cleLot = cleImm + "|" + Valeurs.cle(reference);
            if (!vus.add(cleLot)) {
                erreur(l.numero(), "reference", "DOUBLON_DANS_FICHIER", "Lot « " + reference + " » déjà présent plus haut pour cet immeuble");
            } else if (existants != null && existants.contains(Valeurs.cle(reference))) {
                erreur(l.numero(), "reference", "DEJA_EXISTANT", "Le lot « " + reference + " » existe déjà dans l'immeuble « " + immeuble + " »");
            }
            sortie.add(new Plan.Lot(l.numero(), cleImm, immeuble, reference, type, surface, pieces, loyer));
        }
    }

    private void analyserLocataires(List<Ligne> lignes, List<Plan.Loc> sortie) {
        Set<String> vus = new HashSet<>();
        for (Ligne l : lignes) {
            String nom = requis(l, "nom");
            String brut = requis(l, "telephone");
            String tel = brut == null ? null : lire(l, "telephone", Valeurs::telephone);
            String email = lire(l, "email", Valeurs::email);
            LocalDate naissance = lire(l, "date_naissance", Valeurs::date);
            if (naissance != null && !naissance.isBefore(aujourdhui)) {
                erreur(l.numero(), "date_naissance", "VALEUR_INVALIDE", "La date de naissance doit être passée");
            }
            if (nom == null || tel == null) {
                continue;
            }
            if (!vus.add(tel)) {
                erreur(l.numero(), "telephone", "DOUBLON_DANS_FICHIER", "Ce numéro apparaît déjà plus haut dans le fichier");
            } else if (existant.locataireExiste(tel)) {
                erreur(l.numero(), "telephone", "DEJA_EXISTANT", "Un locataire avec ce numéro existe déjà : retirez cette ligne (ses baux peuvent le référencer par son numéro)");
            }
            sortie.add(new Plan.Loc(l.numero(), nom, tel, email, l.get("profession"), l.get("adresse"), naissance, l.get("numero_cni")));
        }
    }

    private void analyserBaux(List<Ligne> lignes, List<Plan.Lot> lotsFichier, List<Plan.Loc> locatairesFichier, List<Plan.Bail> sortie) {
        Set<String> lotsConnus = new HashSet<>();
        lotsFichier.forEach(x -> lotsConnus.add(x.immeubleCle() + "|" + Valeurs.cle(x.reference())));
        Set<String> telephonesFichier = new HashSet<>();
        locatairesFichier.forEach(x -> telephonesFichier.add(x.telephone()));
        Map<String, Set<String>> existantsCache = new HashMap<>();
        Map<String, List<Plan.Bail>> parLot = new HashMap<>();
        for (Ligne l : lignes) {
            String immeuble = requis(l, "immeuble");
            String lot = requis(l, "lot");
            String brutTel = requis(l, "locataire_telephone");
            String tel = brutTel == null ? null : lire(l, "locataire_telephone", Valeurs::telephone);
            String debutTexte = requis(l, "date_debut");
            LocalDate debut = debutTexte == null ? null : lire(l, "date_debut", Valeurs::date);
            LocalDate fin = lire(l, "date_fin", Valeurs::date);
            String loyerTexte = requis(l, "loyer");
            Long loyer = loyerTexte == null ? null : lire(l, "loyer", Valeurs::montant);
            Long charges = lire(l, "charges", Valeurs::montant);
            Long caution = lire(l, "caution", Valeurs::montant);
            Integer jour = lire(l, "jour_echeance", v -> Valeurs.entier(v, 28));
            Integer preavis = lire(l, "preavis_mois", v -> Valeurs.entier(v, 24));
            String periodicite = lire(l, "periodicite", v -> {
                String p = Valeurs.enTete(v).toUpperCase();
                if (!List.of("MENSUELLE", "TRIMESTRIELLE", "ANNUELLE").contains(p)) {
                    throw new IllegalArgumentException("périodicité inconnue (MENSUELLE, TRIMESTRIELLE ou ANNUELLE)");
                }
                return p;
            });
            if (jour != null && jour < 1) {
                erreur(l.numero(), "jour_echeance", "VALEUR_INVALIDE", "Le jour d'échéance va de 1 à 28");
            }
            if (loyer != null && loyer <= 0) {
                erreur(l.numero(), "loyer", "VALEUR_INVALIDE", "Le loyer doit être positif");
            }
            if (debut != null && fin != null && fin.isBefore(debut)) {
                erreur(l.numero(), "date_fin", "VALEUR_INVALIDE", "La fin du bail précède son début");
            }
            if (fin != null && fin.isBefore(aujourdhui)) {
                erreur(l.numero(), "date_fin", "BAIL_TERMINE", "Ce bail est déjà terminé : seuls les baux en cours sont importés");
            }
            if (debut != null && debut.isAfter(aujourdhui.plusYears(1))) {
                erreur(l.numero(), "date_debut", "VALEUR_INVALIDE", "La date de début est trop lointaine");
            }
            if (immeuble == null || lot == null || tel == null || debut == null || loyer == null) {
                continue;
            }
            String cleImm = Valeurs.cle(immeuble);
            String cleLot = cleImm + "|" + Valeurs.cle(lot);
            if (!lotsConnus.contains(cleLot)) {
                Set<String> existants = existantsCache.computeIfAbsent(cleImm, existant::lotsDeImmeuble);
                if (existants == null || !existants.contains(Valeurs.cle(lot))) {
                    erreur(l.numero(), "lot", "LOT_INCONNU", "Lot « " + lot + " » introuvable dans l'immeuble « " + immeuble + " » (ni dans l'onglet Lots, ni déjà enregistré)");
                    continue;
                }
            }
            if (!telephonesFichier.contains(tel) && !existant.locataireExiste(tel)) {
                erreur(l.numero(), "locataire_telephone", "LOCATAIRE_INCONNU", "Aucun locataire avec ce numéro (ni dans l'onglet Locataires, ni déjà enregistré)");
                continue;
            }
            Plan.Bail bail = new Plan.Bail(l.numero(), cleImm, lot, tel, debut, fin, loyer, charges == null ? 0 : charges, periodicite == null ? "MENSUELLE" : periodicite,
                    jour == null ? 1 : jour, caution == null ? 0 : caution, preavis == null ? 3 : preavis);
            for (Plan.Bail autre : parLot.getOrDefault(cleLot, List.of())) {
                if (chevauchent(autre, bail)) {
                    erreur(l.numero(), "date_debut", "CHEVAUCHEMENT", "Ce bail chevauche celui de la ligne " + autre.ligne() + " sur le même lot");
                }
            }
            parLot.computeIfAbsent(cleLot, k -> new ArrayList<>()).add(bail);
            sortie.add(bail);
        }
    }

    private static boolean chevauchent(Plan.Bail a, Plan.Bail b) {
        LocalDate finA = a.fin() == null ? LocalDate.MAX : a.fin();
        LocalDate finB = b.fin() == null ? LocalDate.MAX : b.fin();
        return !a.debut().isAfter(finB) && !b.debut().isAfter(finA);
    }

    private void analyserSoldes(List<Ligne> lignes, List<Plan.Bail> baux, Map<String, Plan.Solde> sortie) {
        Map<String, Plan.Bail> bauxParLot = new HashMap<>();
        baux.forEach(b -> bauxParLot.put(b.cleLot(), b));
        for (Ligne l : lignes) {
            String immeuble = requis(l, "immeuble");
            String lot = requis(l, "lot");
            String soldeTexte = requis(l, "solde_du");
            Long solde = soldeTexte == null ? null : lire(l, "solde_du", Valeurs::montant);
            LocalDate dateReprise = lire(l, "date_reprise", Valeurs::date);
            if (immeuble == null || lot == null || solde == null) {
                continue;
            }
            String cle = Valeurs.cle(immeuble) + "|" + Valeurs.cle(lot);
            Plan.Bail bail = bauxParLot.get(cle);
            if (bail == null) {
                erreur(l.numero(), "lot", "BAIL_INCONNU", "Aucun bail de l'onglet Baux pour ce lot : un solde se rattache à un bail du fichier");
                continue;
            }
            if (sortie.containsKey(cle)) {
                erreur(l.numero(), "lot", "DOUBLON_DANS_FICHIER", "Ce bail a déjà un solde plus haut dans le fichier");
                continue;
            }
            LocalDate reprise = dateReprise == null ? aujourdhui : dateReprise;
            if (reprise.isAfter(aujourdhui)) {
                erreur(l.numero(), "date_reprise", "VALEUR_INVALIDE", "La date de reprise ne peut pas être dans le futur");
                continue;
            }
            long du = cumuleDu(bail, reprise);
            if (solde > du) {
                erreur(l.numero(), "solde_du", "SOLDE_SUPERIEUR_AU_LOYER", "Le solde (" + solde + " FCFA) dépasse les loyers dus depuis le début du bail jusqu'au " + reprise + " (" + du + " FCFA)");
                continue;
            }
            sortie.put(cle, new Plan.Solde(l.numero(), solde, reprise));
        }
    }

    /** Loyers et charges échus avant la date de reprise : plafond d'un solde d'ouverture. */
    static long cumuleDu(Plan.Bail b, LocalDate reprise) {
        int mois = switch (b.periodicite()) {
            case "TRIMESTRIELLE" -> 3;
            case "ANNUELLE" -> 12;
            default -> 1;
        };
        return CalculEcheances.periodes(b.debut(), b.fin(), mois, b.loyer() + b.charges(), b.jourEcheance(), reprise.minusDays(1)).stream()
                .mapToLong(CalculEcheances.Periode::montant).sum();
    }
}
