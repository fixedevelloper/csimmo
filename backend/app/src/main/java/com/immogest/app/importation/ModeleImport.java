package com.immogest.app.importation;

import com.immogest.app.patrimoine.TypeLot;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Modèles téléchargeables : classeur Excel complet (instructions + un onglet par type, en-têtes figés) ou CSV d'un type (en-têtes seuls, UTF-8 avec BOM). */
final class ModeleImport {

    private static final List<String> INSTRUCTIONS = List.of(
            "ImmoGest — modèle d'import de données",
            "",
            "1. Remplissez les onglets dont vous avez besoin (les autres peuvent rester vides). Ne renommez ni les onglets ni les colonnes ; une * marque une colonne obligatoire dans la liste ci-dessous.",
            "2. Ordre logique : Immeubles → Lots → Locataires → Baux → Soldes. Un lot cite son immeuble par son nom ; un bail cite le lot (immeuble + référence) et le locataire par son téléphone.",
            "3. Dates : AAAA-MM-JJ ou JJ/MM/AAAA. Montants : FCFA entiers, sans décimales (150000 ou 150 000).",
            "4. Seuls les baux EN COURS sont importés (pas de date de fin passée). Les anciens baux et leurs quittances ne sont pas repris.",
            "5. Solde d'ouverture (onglet Soldes) : montant que le locataire doit à la date de reprise (aujourd'hui par défaut). Il est imputé aux échéances les plus récentes ; tout ce qui précède est considéré payé.",
            "   Un bail absent de l'onglet Soldes est considéré à jour au jour de l'import. Un locataire en avance (crédit) : indiquez les mois d'avance dans la fiche du bail après l'import.",
            "6. Un immeuble, un lot ou un locataire déjà enregistré ne peut pas être importé une seconde fois (les lignes sont refusées) : un fichier importé deux fois ne crée jamais de doublons.",
            "7. L'import est tout ou rien : à la moindre erreur, rien n'est enregistré. La prévisualisation liste les erreurs ligne par ligne.",
            "",
            "Valeurs possibles — type de lot : " + String.join(", ", Arrays.stream(TypeLot.values()).map(Enum::name).toList()) + " ; périodicité : MENSUELLE, TRIMESTRIELLE, ANNUELLE ; jour d'échéance : 1 à 28.",
            "");

    private ModeleImport() {
    }

    static byte[] xlsx() {
        try (XSSFWorkbook classeur = new XSSFWorkbook(); ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {
            Font gras = classeur.createFont();
            gras.setBold(true);
            CellStyle styleEntete = classeur.createCellStyle();
            styleEntete.setFont(gras);
            Sheet mode = classeur.createSheet("Instructions");
            int ligne = 0;
            for (String t : INSTRUCTIONS) {
                mode.createRow(ligne++).createCell(0).setCellValue(t);
            }
            for (TypeImport t : TypeImport.values()) {
                mode.createRow(ligne++).createCell(0).setCellValue("Onglet « " + t.feuille() + " » : " + String.join(", ", t.colonnes().stream().map(c -> c.nom() + (c.requis() ? "*" : "")).toList()));
            }
            mode.setColumnWidth(0, 160 * 256);
            for (TypeImport t : TypeImport.values()) {
                Sheet feuille = classeur.createSheet(t.feuille());
                Row entetes = feuille.createRow(0);
                for (int c = 0; c < t.colonnes().size(); c++) {
                    var cellule = entetes.createCell(c);
                    cellule.setCellValue(t.colonnes().get(c).nom());
                    cellule.setCellStyle(styleEntete);
                    feuille.setColumnWidth(c, 22 * 256);
                }
                feuille.createFreezePane(0, 1);
                if (t == TypeImport.LOTS) {
                    liste(feuille, 2, Arrays.stream(TypeLot.values()).map(Enum::name).toArray(String[]::new));
                }
                if (t == TypeImport.BAUX) {
                    liste(feuille, 7, new String[] {"MENSUELLE", "TRIMESTRIELLE", "ANNUELLE"});
                }
            }
            classeur.write(sortie);
            return sortie.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Liste déroulante (colonne {@code colonne}, lignes 2 à 2001) : aide à la saisie, la validation réelle reste côté serveur. */
    private static void liste(Sheet feuille, int colonne, String[] valeurs) {
        DataValidationHelper aide = feuille.getDataValidationHelper();
        DataValidationConstraint contrainte = aide.createExplicitListConstraint(valeurs);
        DataValidation validation = aide.createValidation(contrainte, new CellRangeAddressList(1, LecteurFichier.LIGNES_MAX, colonne, colonne));
        validation.setShowErrorBox(true);
        feuille.addValidationData(validation);
    }

    static byte[] csv(TypeImport type) {
        String entetes = String.join(";", type.colonnes().stream().map(TypeImport.Colonne::nom).toList()) + "\n";
        return ("﻿" + entetes).getBytes(StandardCharsets.UTF_8);
    }
}
