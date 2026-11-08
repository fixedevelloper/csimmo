package com.immogest.app.securite.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Limite de débit par adresse IP sur {@code POST /api/v1/auth/**} : le verrouillage par compte (5 échecs) n'arrête pas quelqu'un qui essaie un mot
 * de passe sur beaucoup de comptes, ni le bombardement de demandes d'OTP (qui coûtent des SMS). Fenêtre glissante d'une minute, en mémoire
 * (une installation = une instance). Dépassement : 429 {@code TROP_DE_REQUETES} avec {@code Retry-After}. L'adresse vient de
 * {@code getRemoteAddr()} : derrière Caddy, Tomcat y met l'adresse de X-Forwarded-For (voir application-prod.yml).
 */
final class LimiteDebitFilter extends OncePerRequestFilter {

    private static final long FENETRE_MS = 60_000;
    private static final int ENTREES_MAX = 50_000;

    private final SecuriteProperties proprietes;
    private final ObjectMapper mapper;
    private final Clock horloge;
    private final Map<String, long[]> compteurs = new ConcurrentHashMap<>(); // clé → {début de fenêtre, nombre}

    LimiteDebitFilter(SecuriteProperties proprietes, ObjectMapper mapper, Clock horloge) {
        this.proprietes = proprietes;
        this.mapper = mapper;
        this.horloge = horloge;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest requete) {
        boolean ecriture = "POST".equals(requete.getMethod()) || "PUT".equals(requete.getMethod());
        return !ecriture || !(requete.getRequestURI().startsWith("/api/v1/auth/") || estInstallation(requete));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest requete, HttpServletResponse reponse, FilterChain chaine) throws ServletException, IOException {
        boolean refresh = requete.getRequestURI().endsWith("/refresh");
        int limite = refresh ? proprietes.limiteRefreshParMinute() : proprietes.limiteAuthParMinute();
        // l'assistant d'installation est public tant qu'aucune installation n'existe : même limite par adresse que la connexion, compteur à part
        String cle = (estInstallation(requete) ? "installation:" : refresh ? "refresh:" : "auth:") + requete.getRemoteAddr();
        long maintenant = horloge.millis();
        if (compteurs.size() > ENTREES_MAX) {
            nettoyer(maintenant);
        }
        long[] etat = compteurs.compute(cle, (k, v) -> {
            if (v == null || maintenant - v[0] >= FENETRE_MS) {
                return new long[] {maintenant, 1};
            }
            v[1]++;
            return v;
        });
        if (etat[1] > limite) {
            long attente = Math.max(1, (etat[0] + FENETRE_MS - maintenant + 999) / 1000);
            reponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            reponse.setHeader("Retry-After", Long.toString(attente));
            reponse.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            mapper.writeValue(reponse.getOutputStream(), Map.<String, Object>of("type", "about:blank", "title", "Too Many Requests", "status", 429,
                    "detail", "Trop de requêtes : réessayez dans " + attente + " s", "code", "TROP_DE_REQUETES"));
            return;
        }
        chaine.doFilter(requete, reponse);
    }

    private static boolean estInstallation(HttpServletRequest requete) {
        return "/api/v1/installation".equals(requete.getRequestURI());
    }

    private void nettoyer(long maintenant) {
        for (Iterator<Map.Entry<String, long[]>> it = compteurs.entrySet().iterator(); it.hasNext();) {
            if (maintenant - it.next().getValue()[0] >= FENETRE_MS) {
                it.remove();
            }
        }
        if (compteurs.size() > ENTREES_MAX) {
            compteurs.clear(); // attaque massive depuis des milliers d'adresses : mieux vaut oublier que saturer la mémoire
        }
    }
}
