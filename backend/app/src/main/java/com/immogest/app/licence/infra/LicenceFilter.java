package com.immogest.app.licence.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.licence.EtatLicence;
import com.immogest.app.licence.LicenceService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Mode lecture seule (règle de licence n°4) : si la licence n'est pas valide, toute requête d'écriture est refusée
 * avec le code {@code LICENCE_EXPIREE}. Aucune donnée n'est supprimée ; les lectures restent possibles.
 *
 * <p>Exceptions (écritures toujours permises) : authentification, licence, installation, exports, sauvegardes.
 */
@Component
class LicenceFilter extends OncePerRequestFilter {

    static final String CODE = "LICENCE_EXPIREE";

    private static final Set<String> METHODES_SURES = Set.of("GET", "HEAD", "OPTIONS", "TRACE");
    private static final List<String> EXEMPTES = List.of(
            "/api/v1/auth", "/api/v1/licence", "/api/v1/installation", "/api/v1/exports", "/api/v1/sauvegardes");

    private final LicenceService licences;
    private final ObjectMapper mapper;

    LicenceFilter(LicenceService licences, ObjectMapper mapper) {
        this.licences = licences;
        this.mapper = mapper;
    }

    /** Vrai si la requête est une écriture soumise au contrôle de licence. */
    static boolean soumise(String methode, String chemin) {
        if (METHODES_SURES.contains(methode.toUpperCase()) || !chemin.startsWith("/api/")) {
            return false;
        }
        return EXEMPTES.stream().noneMatch(prefixe -> chemin.equals(prefixe) || chemin.startsWith(prefixe + "/"));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest requete) {
        return !soumise(requete.getMethod(), requete.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest requete, HttpServletResponse reponse, FilterChain chaine)
            throws ServletException, IOException {
        EtatLicence etat = licences.etatCourant();
        if (!etat.lectureSeule()) {
            chaine.doFilter(requete, reponse);
            return;
        }
        Map<String, Object> probleme = new LinkedHashMap<>();
        probleme.put("type", "about:blank");
        probleme.put("title", HttpStatus.FORBIDDEN.getReasonPhrase());
        probleme.put("status", HttpStatus.FORBIDDEN.value());
        probleme.put("detail", "Licence non valide : l'application est en lecture seule");
        probleme.put("code", CODE);
        probleme.put("etat", etat.name());
        reponse.setStatus(HttpStatus.FORBIDDEN.value());
        reponse.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        mapper.writeValue(reponse.getOutputStream(), probleme);
    }
}
