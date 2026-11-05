package com.immogest.app.charge;

import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.Role;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Charge (étape 11.2) : une installation de 5 000 lots, 4 000 baux actifs et 48 000 échéances. Mesure le temps de réponse des écrans de liste et du tableau
 * de bord (MockMvc : hors réseau) et le <b>poids</b> des réponses, qui décide de l'expérience en 3G. Les seuils sont larges (la CI est lente) : ils attrapent
 * une requête N+1 ou un index manquant, pas une dérive de quelques pour cent. Les mesures sont imprimées.
 */
@IntegrationTest
@org.springframework.test.context.TestPropertySource(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class ChargeTest {

    @Autowired
    jakarta.persistence.EntityManagerFactory emf;

    private static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    /** 3G « réaliste » : ~400 kbit/s utiles, soit ~50 Ko/s. */
    private static final int OCTETS_PAR_SECONDE_3G = 50_000;

    @Autowired
    MockMvc mvc;
    @Autowired
    JdbcTemplate jdbc;

    private void semer() {
        jdbc.execute("DROP TABLE IF EXISTS tmp_imm, tmp_lot, tmp_loc"); // une connexion du pool est réutilisée d'un test à l'autre
        jdbc.execute("TRUNCATE ol_webhook_recu, ol_transaction, mnt_signalement, notif_message, fin_document, fin_compteur_numero, fin_imputation, fin_paiement, fin_echeance, bail_document, bail_garant, bail, patrimoine_photo, compteur, lot_equipement, lot, immeuble, garant, tiers_piece, locataire CASCADE");
        jdbc.execute("INSERT INTO immeuble (id, nom, adresse, ville, quartier, created_at, updated_at, version) SELECT gen_random_uuid(), 'Immeuble ' || g, g || ' rue des Tests', 'Douala', 'Quartier ' || (g % 20), now(), now(), 0 FROM generate_series(1, 100) g");
        jdbc.execute("CREATE TEMP TABLE tmp_imm AS SELECT id, row_number() OVER (ORDER BY nom) AS n FROM immeuble");
        jdbc.execute("INSERT INTO lot (id, immeuble_id, reference, type, surface_m2, pieces, loyer_indicatif, statut, created_at, updated_at, version) "
                + "SELECT gen_random_uuid(), i.id, 'L' || g, (ARRAY['APPARTEMENT','STUDIO','BOUTIQUE','BUREAU'])[1 + g % 4], 30 + g % 60, 1 + g % 5, 60000 + (g % 10) * 10000, CASE WHEN g <= 4000 THEN 'OCCUPE' ELSE 'LIBRE' END, now(), now(), 0 "
                + "FROM generate_series(1, 5000) g JOIN tmp_imm i ON i.n = 1 + (g % 100)");
        jdbc.execute("INSERT INTO locataire (id, nom, telephone, created_at, updated_at, version) SELECT gen_random_uuid(), 'Locataire ' || g, '+2376' || lpad(g::text, 8, '0'), now(), now(), 0 FROM generate_series(1, 4000) g");
        jdbc.execute("CREATE TEMP TABLE tmp_lot AS SELECT id, row_number() OVER (ORDER BY reference) AS n FROM (SELECT id, reference FROM lot ORDER BY reference LIMIT 4000) x");
        jdbc.execute("CREATE TEMP TABLE tmp_loc AS SELECT id, row_number() OVER (ORDER BY nom) AS n FROM locataire");
        jdbc.execute("INSERT INTO bail (id, numero, lot_id, locataire_id, statut, date_debut, loyer, charges, periodicite, jour_echeance, caution, mois_avance, preavis_mois, created_at, updated_at, version) "
                + "SELECT gen_random_uuid(), 'BAIL-' || l.n, l.id, c.id, 'ACTIF', (current_date - interval '12 months')::date, 80000 + (l.n % 8) * 10000, 5000, 'MENSUELLE', 1, 160000, 0, 3, now(), now(), 0 "
                + "FROM tmp_lot l JOIN tmp_loc c ON c.n = l.n");
        // 12 échéances par bail ; la moitié des baux ne paient plus les 3 dernières (arriérés)
        jdbc.execute("INSERT INTO fin_echeance (id, bail_id, periode_debut, periode_fin, date_echeance, montant, montant_paye, statut, created_at, updated_at, version) "
                + "SELECT gen_random_uuid(), b.id, (b.date_debut + (m || ' months')::interval)::date, (b.date_debut + ((m + 1) || ' months')::interval - interval '1 day')::date, (b.date_debut + (m || ' months')::interval)::date, "
                + "b.loyer + b.charges, CASE WHEN m >= 9 AND (row_number() OVER (ORDER BY b.id) % 2 = 0) THEN 0 ELSE b.loyer + b.charges END, "
                + "CASE WHEN m >= 9 AND (row_number() OVER (ORDER BY b.id) % 2 = 0) THEN 'EN_RETARD' ELSE 'PAYEE' END, now(), now(), 0 FROM bail b CROSS JOIN generate_series(0, 11) m");
        jdbc.execute("ANALYZE");
    }

    private final Map<String, String> mesures = new LinkedHashMap<>();

    private int appeler(String url, long seuilMs, int poidsMaxOctets) throws Exception {
        mvc.perform(get(url).with(ADMIN)); // chauffe (compilation JIT, cache de plans)
        long debut = System.nanoTime();
        var reponse = mvc.perform(get(url).with(ADMIN)).andReturn().getResponse();
        long ms = Duration.ofNanos(System.nanoTime() - debut).toMillis();
        int octets = reponse.getContentAsByteArray().length;
        mesures.put(url, "%d ms, %d octets (≈ %.1f s en 3G)".formatted(ms, octets, octets / (double) OCTETS_PAR_SECONDE_3G));
        assertThat(reponse.getStatus()).as(url).isEqualTo(200);
        assertThat(ms).as("temps de réponse de %s", url).isLessThan(seuilMs);
        assertThat(octets).as("poids de %s (3G : 50 Ko/s)", url).isLessThan(poidsMaxOctets);
        return octets;
    }

    @Test
    void cinqMilleLotsLesEcransRestentReactifsEtLegers() throws Exception {
        semer();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM lot", Long.class)).isEqualTo(5000);
        assertThat(jdbc.queryForObject("SELECT count(*) FROM fin_echeance", Long.class)).isEqualTo(48000);

        List<String> urls = new ArrayList<>();
        appeler("/api/v1/lots?size=20", 1500, 40_000);
        appeler("/api/v1/lots?size=100&page=25", 2000, 120_000);
        appeler("/api/v1/lots?q=L4999", 1500, 40_000);
        appeler("/api/v1/lots?statut=LIBRE&size=20", 1500, 40_000);
        appeler("/api/v1/immeubles?size=100", 2000, 120_000);
        appeler("/api/v1/baux?size=20", 1500, 60_000);
        appeler("/api/v1/baux?size=100&page=10", 2500, 250_000);
        appeler("/api/v1/echeances?statut=EN_RETARD&size=20", 1500, 40_000);
        appeler("/api/v1/locataires?size=20", 1500, 40_000);
        appeler("/api/v1/locataires?q=Locataire+1234", 1500, 40_000);
        appeler("/api/v1/tableau-de-bord/synthese", 3000, 20_000);
        appeler("/api/v1/tableau-de-bord/attendu-encaisse", 3000, 20_000);
        appeler("/api/v1/tableau-de-bord/arrieres", 2000, 60_000); // 1,4 s avant la lecture groupée ; le nombre de requêtes est garanti par un test dédié
        appeler("/api/v1/tableau-de-bord/baux-a-echeance", 3000, 60_000);
        appeler("/api/v1/arrieres?size=20", 3000, 60_000);
        System.out.println("=== MESURES DE CHARGE (5 000 lots, 4 000 baux, 48 000 échéances) ===");
        mesures.forEach((u, m) -> System.out.println(u + " → " + m));
        assertThat(urls).isEmpty();
    }

    /**
     * Garde déterministe contre le N+1 (un temps de réponse varie avec la machine, un nombre de requêtes non) : avec 2 000 baux en retard, le tableau
     * de bord des arriérés ne doit émettre qu'une poignée de requêtes JPA. Avant la lecture groupée : ~4 000 (un bail et un lot par ligne).
     */
    @Test
    void lesArrieresDuTableauDeBordNeFontPasUneRequeteParBail() throws Exception {
        semer();
        var stats = emf.unwrap(org.hibernate.SessionFactory.class).getStatistics();
        mvc.perform(get("/api/v1/tableau-de-bord/arrieres").with(ADMIN)); // chauffe
        stats.clear();
        mvc.perform(get("/api/v1/tableau-de-bord/arrieres").with(ADMIN)).andReturn();
        long requetes = stats.getPrepareStatementCount();
        System.out.println("requêtes JPA pour les arriérés de 2 000 baux : " + requetes);
        assertThat(requetes).as("requêtes JPA émises").isLessThan(400);
    }
}
