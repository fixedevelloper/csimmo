package com.immogest.app.securite;

import static org.assertj.core.api.Assertions.assertThat;

import com.immogest.app.IntegrationTest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Garde-fou de contrôle d'accès : CHAQUE endpoint de l'API doit déclarer explicitement qui peut l'appeler (@PreAuthorize sur la méthode ou la
 * classe), ou figurer ci-dessous avec sa justification. Un nouvel endpoint sans déclaration fait échouer ce test : par défaut, la configuration
 * HTTP n'exige qu'un jeton valide, c'est-à-dire que n'importe quel rôle (locataire, prestataire…) y accéderait.
 */
@IntegrationTest
class MatriceDroitsTest {

    /** Endpoints volontairement sans @PreAuthorize : « METHODE chemin » → pourquoi. */
    private static final Map<String, String> EXCEPTIONS = Map.ofEntries(
            Map.entry("POST /api/v1/auth/login", "public : connexion"),
            Map.entry("POST /api/v1/auth/refresh", "public : renouvellement par jeton opaque"),
            Map.entry("POST /api/v1/auth/logout", "public : révocation par jeton opaque"),
            Map.entry("POST /api/v1/auth/otp/demander", "public : connexion OTP"),
            Map.entry("POST /api/v1/auth/otp/verifier", "public : connexion OTP"),
            Map.entry("GET /api/v1/health", "public : supervision"),
            Map.entry("POST /api/v1/webhooks/{fournisseur}", "public : authentifié par signature"),
            Map.entry("GET /api/v1/auth/moi", "tout utilisateur connecté : lit SON propre profil"),
            Map.entry("POST /api/v1/auth/mot-de-passe", "tout utilisateur connecté : change SON mot de passe (ancien mot de passe exigé)"),
            Map.entry("GET /api/v1/fichiers/{id}", "tout utilisateur connecté : le droit d'accès est contrôlé fichier par fichier (ControleAccesFichier), refus par défaut"),
            Map.entry("GET /api/v1/installation", "public : assistant de premier lancement, 409 une fois installé"),
            Map.entry("POST /api/v1/installation", "public : assistant de premier lancement, 409 une fois installé"));

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("requestMappingHandlerMapping") // il y a aussi celui des endpoints Actuator
    RequestMappingHandlerMapping mapping;

    private static boolean declare(HandlerMethod h) {
        Method m = h.getMethod();
        return AnnotatedElementUtils.hasAnnotation(m, PreAuthorize.class) || AnnotatedElementUtils.hasAnnotation(h.getBeanType(), PreAuthorize.class);
    }

    @Test
    void chaqueEndpointDeclareSesDroits() {
        List<String> sansDroits = new ArrayList<>();
        Map<String, String> inventaire = new TreeMap<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> e : mapping.getHandlerMethods().entrySet()) {
            HandlerMethod h = e.getValue();
            if (!h.getBeanType().getPackageName().startsWith("com.immogest.app")) {
                continue; // contrôleurs de frameworks (erreur, springdoc, actuator)
            }
            var chemins = e.getKey().getPathPatternsCondition().getPatternValues();
            for (String chemin : chemins) {
                for (var methode : e.getKey().getMethodsCondition().getMethods()) {
                    String cle = methode + " " + chemin;
                    String regle = declare(h) ? "@PreAuthorize" : EXCEPTIONS.getOrDefault(cle, "AUCUNE");
                    inventaire.put(cle, regle);
                    if (regle.equals("AUCUNE")) {
                        sansDroits.add(cle);
                    }
                }
            }
        }
        assertThat(inventaire).as("endpoints inventoriés").hasSizeGreaterThan(100);
        assertThat(sansDroits).as("endpoints sans déclaration de droits (ajoutez @PreAuthorize ou justifiez l'exception)").isEmpty();
    }

    @Test
    void lesExceptionsDeclareesExistentToujours() {
        List<String> existants = new ArrayList<>();
        mapping.getHandlerMethods().keySet().forEach(i -> i.getPathPatternsCondition().getPatternValues()
                .forEach(p -> i.getMethodsCondition().getMethods().forEach(m -> existants.add(m + " " + p))));
        // une exception périmée masquerait un futur endpoint portant le même chemin
        assertThat(existants).containsAll(EXCEPTIONS.keySet());
    }
}
