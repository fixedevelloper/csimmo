package com.immogest.app.importation;

import com.immogest.app.shared.ErreurMetier;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;

/** Lit un fichier .xlsx (un onglet par type) ou .csv (un seul type) en lignes de texte ; la validation métier est faite ailleurs. */
final class LecteurFichier {

    static final int LIGNES_MAX = 2000;
    private static final int COLONNES_MAX = 40;

    private LecteurFichier() {
    }

    record Feuille(TypeImport type, List<String> entetes, List<Ligne> lignes) {
    }

    static Map<TypeImport, Feuille> lire(byte[] contenu, TypeImport typeCsv) {
        if (contenu.length == 0) {
            throw new ErreurMetier("IMPORT_FICHIER_VIDE", "Le fichier est vide", HttpStatus.BAD_REQUEST);
        }
        if (contenu.length >= 2 && contenu[0] == 'P' && contenu[1] == 'K') {
            return xlsx(contenu);
        }
        if (contenu.length >= 4 && (contenu[0] & 0xff) == 0xD0 && (contenu[1] & 0xff) == 0xCF) {
            throw new ErreurMetier("IMPORT_FORMAT_NON_SUPPORTE", "Ancien format Excel (.xls) : enregistrez le fichier en .xlsx", HttpStatus.BAD_REQUEST);
        }
        for (byte b : contenu) {
            if (b == 0) {
                throw new ErreurMetier("IMPORT_FORMAT_NON_SUPPORTE", "Fichier binaire non reconnu : utilisez un .xlsx ou un .csv", HttpStatus.BAD_REQUEST);
            }
        }
        if (typeCsv == null) {
            throw new ErreurMetier("IMPORT_TYPE_REQUIS", "Un fichier CSV ne contient qu'un type de données : précisez lequel", HttpStatus.BAD_REQUEST);
        }
        Map<TypeImport, Feuille> r = new EnumMap<>(TypeImport.class);
        r.put(typeCsv, csv(texte(contenu), typeCsv));
        return r;
    }

    // ------------------------------------------------------------------ xlsx

    private static Map<TypeImport, Feuille> xlsx(byte[] contenu) {
        Map<TypeImport, Feuille> r = new EnumMap<>(TypeImport.class);
        try (XSSFWorkbook classeur = new XSSFWorkbook(new ByteArrayInputStream(contenu))) {
            DataFormatter formateur = new DataFormatter(java.util.Locale.FRANCE);
            FormulaEvaluator evaluateur = classeur.getCreationHelper().createFormulaEvaluator();
            for (Sheet feuille : classeur) {
                String nom = Valeurs.enTete(feuille.getSheetName());
                for (TypeImport t : TypeImport.values()) {
                    if (Valeurs.enTete(t.feuille()).equals(nom)) {
                        r.put(t, feuilleXlsx(t, feuille, formateur, evaluateur));
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
            if (e instanceof ErreurMetier em) {
                throw em;
            }
            throw new ErreurMetier("IMPORT_FICHIER_ILLISIBLE", "Fichier Excel illisible ou corrompu", HttpStatus.BAD_REQUEST);
        }
        return r;
    }

    private static Feuille feuilleXlsx(TypeImport type, Sheet feuille, DataFormatter formateur, FormulaEvaluator evaluateur) {
        List<String> entetes = new ArrayList<>();
        List<Ligne> lignes = new ArrayList<>();
        int premiere = feuille.getFirstRowNum();
        Row ligneEntetes = feuille.getRow(premiere);
        if (ligneEntetes == null) {
            return new Feuille(type, entetes, lignes);
        }
        int nbColonnes = Math.min(ligneEntetes.getLastCellNum(), COLONNES_MAX);
        for (int c = 0; c < nbColonnes; c++) {
            entetes.add(Valeurs.enTete(formateur.formatCellValue(ligneEntetes.getCell(c))));
        }
        for (int i = premiere + 1; i <= feuille.getLastRowNum(); i++) {
            Row ligne = feuille.getRow(i);
            if (ligne == null) {
                continue;
            }
            Map<String, String> valeurs = new LinkedHashMap<>();
            boolean vide = true;
            for (int c = 0; c < entetes.size(); c++) {
                String v = valeurCellule(ligne.getCell(c), formateur, evaluateur);
                if (!v.isBlank()) {
                    vide = false;
                }
                if (!entetes.get(c).isEmpty()) {
                    valeurs.put(entetes.get(c), v);
                }
            }
            if (vide) {
                continue;
            }
            if (lignes.size() >= LIGNES_MAX) {
                throw new ErreurMetier("IMPORT_TROP_DE_LIGNES", "Plus de %d lignes dans l'onglet « %s » : découpez le fichier".formatted(LIGNES_MAX, type.feuille()), HttpStatus.BAD_REQUEST);
            }
            lignes.add(new Ligne(i + 1, valeurs));
        }
        return new Feuille(type, entetes, lignes);
    }

    private static String valeurCellule(Cell cellule, DataFormatter formateur, FormulaEvaluator evaluateur) {
        if (cellule == null) {
            return "";
        }
        CellType type = cellule.getCellType() == CellType.FORMULA ? cellule.getCachedFormulaResultType() : cellule.getCellType();
        if (type == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cellule)) {
                return cellule.getLocalDateTimeCellValue().toLocalDate().toString();
            }
            return new java.math.BigDecimal(Double.toString(cellule.getNumericCellValue())).stripTrailingZeros().toPlainString(); // 150000 et non 1,5E5
        }
        return formateur.formatCellValue(cellule, evaluateur).strip();
    }

    // ------------------------------------------------------------------ csv

    private static String texte(byte[] contenu) {
        int debut = contenu.length >= 3 && (contenu[0] & 0xff) == 0xEF && (contenu[1] & 0xff) == 0xBB && (contenu[2] & 0xff) == 0xBF ? 3 : 0;
        try {
            return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(contenu, debut, contenu.length - debut)).toString();
        } catch (CharacterCodingException e) {
            return new String(contenu, debut, contenu.length - debut, java.nio.charset.Charset.forName("windows-1252")); // export Excel « CSV » classique
        }
    }

    static Feuille csv(String texte, TypeImport type) {
        List<List<String>> table = analyserCsv(texte);
        List<String> entetes = new ArrayList<>();
        List<Ligne> lignes = new ArrayList<>();
        if (table.isEmpty()) {
            return new Feuille(type, entetes, lignes);
        }
        table.get(0).forEach(e -> entetes.add(Valeurs.enTete(e)));
        for (int i = 1; i < table.size(); i++) {
            List<String> cellules = table.get(i);
            Map<String, String> valeurs = new LinkedHashMap<>();
            boolean vide = true;
            for (int c = 0; c < entetes.size(); c++) {
                String v = c < cellules.size() ? cellules.get(c).strip() : "";
                if (!v.isEmpty()) {
                    vide = false;
                }
                if (!entetes.get(c).isEmpty()) {
                    valeurs.put(entetes.get(c), v);
                }
            }
            if (vide) {
                continue;
            }
            if (lignes.size() >= LIGNES_MAX) {
                throw new ErreurMetier("IMPORT_TROP_DE_LIGNES", "Plus de %d lignes : découpez le fichier".formatted(LIGNES_MAX), HttpStatus.BAD_REQUEST);
            }
            lignes.add(new Ligne(i + 1, valeurs));
        }
        return new Feuille(type, entetes, lignes);
    }

    /** CSV RFC 4180 : séparateur détecté sur la première ligne (; , ou tabulation), guillemets doublés, retours à la ligne dans les champs entre guillemets. */
    static List<List<String>> analyserCsv(String texte) {
        int finPremiere = texte.indexOf('\n') < 0 ? texte.length() : texte.indexOf('\n');
        String premiere = texte.substring(0, finPremiere);
        char sep = ';';
        int meilleur = count(premiere, ';');
        for (char c : new char[] {',', '\t'}) {
            if (count(premiere, c) > meilleur) {
                meilleur = count(premiere, c);
                sep = c;
            }
        }
        List<List<String>> table = new ArrayList<>();
        List<String> ligne = new ArrayList<>();
        StringBuilder champ = new StringBuilder();
        boolean guillemets = false;
        for (int i = 0; i < texte.length(); i++) {
            char c = texte.charAt(i);
            if (guillemets) {
                if (c == '"') {
                    if (i + 1 < texte.length() && texte.charAt(i + 1) == '"') {
                        champ.append('"');
                        i++;
                    } else {
                        guillemets = false;
                    }
                } else {
                    champ.append(c);
                }
            } else if (c == '"') {
                guillemets = true;
            } else if (c == sep) {
                ligne.add(champ.toString());
                champ.setLength(0);
            } else if (c == '\n' || c == '\r') {
                if (c == '\r' && i + 1 < texte.length() && texte.charAt(i + 1) == '\n') {
                    i++;
                }
                ligne.add(champ.toString());
                champ.setLength(0);
                table.add(ligne);
                ligne = new ArrayList<>();
            } else {
                champ.append(c);
            }
        }
        if (champ.length() > 0 || !ligne.isEmpty()) {
            ligne.add(champ.toString());
            table.add(ligne);
        }
        return table;
    }

    private static int count(String s, char c) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                n++;
            }
        }
        return n;
    }
}
