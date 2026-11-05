package com.immogest.app.bail;

import com.immogest.app.bail.domain.ModeleBail;
import com.immogest.app.bail.infra.ModeleBailRepository;
import com.immogest.app.shared.Montants;
import com.immogest.app.shared.ErreurMetier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Textes modifiables du bail (bailleur, préambule, clauses, pied de page) avec des variables {@code {{NOM}}}.
 * Le gabarit de mise en page reste fixe : un texte saisi ne peut jamais exécuter de code, seulement être affiché (échappé).
 */
@Service
public class ModeleBailService {

    public static final List<String> VARIABLES = List.of("NUMERO", "LOCATAIRE", "LOT", "IMMEUBLE", "ADRESSE", "LOYER", "CHARGES",
            "TOTAL_PERIODE", "CAUTION", "DATE_DEBUT", "DATE_FIN", "JOUR_ECHEANCE", "PERIODICITE", "PREAVIS_MOIS", "MOIS_AVANCE");
    private static final Pattern VARIABLE = Pattern.compile("\\{\\{([A-Za-z_]+)}}");

    static final String PREAMBULE_DEFAUT = """
            Il a été convenu et arrêté ce qui suit entre le bailleur et le locataire désignés ci-dessous, au sujet du bien {{LOT}} situé dans l'immeuble {{IMMEUBLE}}, {{ADRESSE}}.""";
    static final String CLAUSES_DEFAUT = """
            Article 1 - Objet. Le bailleur donne en location à {{LOCATAIRE}}, qui l'accepte, le bien {{LOT}} de l'immeuble {{IMMEUBLE}}, à usage conforme à sa destination.

            Article 2 - Durée. Le bail prend effet le {{DATE_DEBUT}}. Date de fin : {{DATE_FIN}}.

            Article 3 - Loyer et charges. Le loyer mensuel est de {{LOYER}} et les charges mensuelles de {{CHARGES}}, soit {{TOTAL_PERIODE}} par mois, payables par période {{PERIODICITE}} au plus tard le {{JOUR_ECHEANCE}} du mois d'échéance.

            Article 4 - Dépôt de garantie et avance. Le locataire verse à la signature un dépôt de garantie de {{CAUTION}} et une avance de {{MOIS_AVANCE}} mois de loyer. Le dépôt est restitué à la sortie, déduction faite des sommes dues et des réparations locatives justifiées.

            Article 5 - Obligations du locataire. Le locataire paie le loyer et les charges aux échéances, use paisiblement des lieux, les entretient, ne les sous-loue pas sans accord écrit du bailleur et répond des dégradations survenues pendant la location.

            Article 6 - Résiliation et préavis. Chaque partie peut résilier le bail par écrit en respectant un préavis de {{PREAVIS_MOIS}} mois. Le défaut de paiement du loyer après mise en demeure reste cause de résiliation.

            Article 7 - État des lieux. Un état des lieux contradictoire est dressé à l'entrée et à la sortie du locataire.

            Article 8 - Litiges. À défaut d'accord amiable, tout litige relève des juridictions compétentes du lieu de situation du bien.""";

    private final ModeleBailRepository modeles;

    ModeleBailService(ModeleBailRepository modeles) {
        this.modeles = modeles;
    }

    @Transactional(readOnly = true)
    public ModeleBailDto obtenir() {
        return versDto(charger());
    }

    @Transactional
    public ModeleBailDto modifier(ModeleBailSaisie saisie) {
        String nom = exiger(saisie.bailleurNom(), "bailleurNom", 200);
        String preambule = exiger(saisie.preambule(), "preambule", 4000);
        String clauses = exiger(saisie.clauses(), "clauses", 20000);
        verifierVariables(preambule);
        verifierVariables(clauses);
        verifierVariables(saisie.piedDePage());
        verifierVariables(saisie.bailleurAdresse());
        ModeleBail modele = chargerPourEcriture();
        modele.modifier(Montants.pourPdf(nom), optionnel(saisie.bailleurAdresse(), 300), preambule, clauses, optionnel(saisie.piedDePage(), 500));
        return versDto(modele);
    }

    /** Revient aux textes par défaut. */
    @Transactional
    public ModeleBailDto reinitialiser() {
        ModeleBail modele = chargerPourEcriture();
        modele.modifier("Le bailleur", null, PREAMBULE_DEFAUT, CLAUSES_DEFAUT, null);
        return versDto(modele);
    }

    /** Lecture seule : sans modèle enregistré, les textes par défaut (non persistés) sont renvoyés. */
    ModeleBail charger() {
        return modeles.findBySingletonTrue().orElseGet(ModeleBailService::parDefaut);
    }

    private ModeleBail chargerPourEcriture() {
        return modeles.findBySingletonTrue().orElseGet(() -> modeles.saveAndFlush(parDefaut()));
    }

    private static ModeleBail parDefaut() {
        return new ModeleBail("Le bailleur", null, PREAMBULE_DEFAUT, CLAUSES_DEFAUT, null);
    }

    /** Remplace les variables par leurs valeurs (texte brut : l'échappement est fait à l'affichage). Variable inconnue : laissée telle quelle. */
    public static String appliquer(String texte, Map<String, String> valeurs) {
        if (texte == null) {
            return null;
        }
        Matcher m = VARIABLE.matcher(texte);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String valeur = valeurs.get(m.group(1).toUpperCase());
            m.appendReplacement(sb, Matcher.quoteReplacement(valeur != null ? valeur : m.group()));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /** Découpe un texte en paragraphes (lignes vides) ; les sauts de ligne simples deviennent des espaces. */
    public static List<String> paragraphes(String texte) {
        List<String> resultat = new ArrayList<>();
        if (texte == null) {
            return resultat;
        }
        for (String bloc : texte.strip().split("\\R\\s*\\R")) {
            String ligne = bloc.replaceAll("\\s*\\R\\s*", " ").strip();
            if (!ligne.isEmpty()) {
                resultat.add(ligne);
            }
        }
        return resultat;
    }

    private static void verifierVariables(String texte) {
        if (texte == null) {
            return;
        }
        Matcher m = VARIABLE.matcher(texte);
        while (m.find()) {
            if (!VARIABLES.contains(m.group(1).toUpperCase())) {
                throw new ErreurMetier("BAIL_MODELE_VARIABLE_INCONNUE", "Variable inconnue : {{" + m.group(1) + "}}", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private static String exiger(String valeur, String champ, int max) {
        String t = optionnel(valeur, max);
        if (t == null) {
            throw new ErreurMetier("CHAMP_REQUIS", "Le champ " + champ + " est obligatoire", HttpStatus.BAD_REQUEST);
        }
        return t;
    }

    private static String optionnel(String valeur, int max) {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        String t = valeur.strip();
        if (t.length() > max) {
            throw new ErreurMetier("CHAMP_TROP_LONG", "Texte trop long (max %d caractères)".formatted(max), HttpStatus.BAD_REQUEST);
        }
        return t;
    }

    private static ModeleBailDto versDto(ModeleBail m) {
        return new ModeleBailDto(m.getBailleurNom(), m.getBailleurAdresse(), m.getPreambule(), m.getClauses(), m.getPiedDePage(), VARIABLES);
    }
}
