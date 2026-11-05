package com.immogest.app.patrimoine;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.licence.LicenceService;
import com.immogest.app.licence.LicenceTest;
import com.immogest.app.licence.LicenceTestSupport;
import com.immogest.app.licence.MutableClock;
import com.immogest.app.stockage.StockageTestSupport;
import com.immogest.licensing.Licence;
import com.immogest.licensing.LicenceSignee;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

/** Palier de la licence et lecture seule, avec une licence de test à palier réduit. */
@LicenceTest
class PatrimoinePalierTest extends PatrimoineTestBase {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    LicenceService licences;
    @Autowired
    MutableClock horloge;

    @Override
    protected MockMvc mvc() {
        return mvc;
    }

    @Override
    protected ObjectMapper mapper() {
        return mapper;
    }

    private LocalDate aujourdhui() {
        return LocalDate.now(horloge.withZone(ZoneId.of("Africa/Douala")));
    }

    private void installerLicence(String id, int palier) {
        Licence licence = new Licence(id, "Client Palier", palier, aujourdhui().minusDays(1), aujourdhui().plusDays(365),
                LicenceTestSupport.EMPREINTE, List.of());
        licences.importer(LicenceSignee.signer(licence, LicenceTestSupport.EDITEUR.getPrivate()).versJson());
    }

    @BeforeEach
    void preparer() {
        horloge.reinitialiser();
        viderPatrimoine(jdbc);
        jdbc.execute("TRUNCATE lic_licence, sec_refresh_token, sec_otp_code, utilisateur CASCADE");
        licences.invalider();
    }

    @Test
    void lAvantDernierLotPasseLeDernierEstRefuseAuPalier() throws Exception {
        installerLicence("L-2", 2);
        UUID immeuble = creerImmeuble("Petit", "Douala", "Akwa");

        creerLotBrut(immeuble, "1", "STUDIO").andExpect(status().isCreated());
        creerLotBrut(immeuble, "2", "STUDIO").andExpect(status().isCreated());
        creerLotBrut(immeuble, "3", "STUDIO")
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("LICENCE_PALIER_ATTEINT"));

        assertThat(jdbc.queryForObject("SELECT count(*) FROM lot", Integer.class)).isEqualTo(2);
    }

    @Test
    void unLotArchiveLibereUnePlaceEtUneLicenceSuperieureAgrandiLePalier() throws Exception {
        installerLicence("L-2", 2);
        UUID immeuble = creerImmeuble("Petit", "Douala", "Akwa");
        UUID premier = creerLot(immeuble, "1", "STUDIO");
        creerLot(immeuble, "2", "STUDIO");
        creerLotBrut(immeuble, "3", "STUDIO").andExpect(status().isConflict());

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/lots/{id}", premier).with(ADMIN))
                .andExpect(status().isNoContent());
        creerLotBrut(immeuble, "3", "STUDIO").andExpect(status().isCreated());
        creerLotBrut(immeuble, "4", "STUDIO").andExpect(status().isConflict());

        installerLicence("L-10", 10);
        creerLotBrut(immeuble, "4", "STUDIO").andExpect(status().isCreated());
    }

    @Test
    void creationsSimultaneesAuDernierEmplacementUneSeuleReussit() throws Exception {
        installerLicence("L-3", 3);
        UUID immeuble = creerImmeuble("Concurrence", "Douala", "Akwa");
        creerLot(immeuble, "1", "STUDIO");
        creerLot(immeuble, "2", "STUDIO");

        int fils = 6;
        ExecutorService pool = Executors.newFixedThreadPool(fils);
        CountDownLatch depart = new CountDownLatch(1);
        try {
            List<Callable<Integer>> appels = new ArrayList<>();
            for (int i = 0; i < fils; i++) {
                String reference = "C" + i;
                appels.add(() -> {
                    depart.await();
                    return creerLotBrut(immeuble, reference, "STUDIO").andReturn().getResponse().getStatus();
                });
            }
            List<Future<Integer>> futurs = new ArrayList<>();
            for (Callable<Integer> appel : appels) {
                futurs.add(pool.submit(appel));
            }
            depart.countDown();
            List<Integer> statuts = new ArrayList<>();
            for (Future<Integer> f : futurs) {
                statuts.add(f.get());
            }

            assertThat(statuts.stream().filter(s -> s == 201)).as("un seul lot créé : %s", statuts).hasSize(1);
            assertThat(statuts.stream().filter(s -> s == 409)).hasSize(fils - 1);
            assertThat(jdbc.queryForObject("SELECT count(*) FROM lot", Integer.class)).as("jamais au-delà du palier").isEqualTo(3);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void licenceExpireeLesEcrituresDuPatrimoineSontRefuseesLesLecturesRestentPossibles() throws Exception {
        installerLicence("L-1", 50);
        UUID immeuble = creerImmeuble("Avant expiration", "Douala", "Akwa");
        UUID lot = creerLot(immeuble, "1", "STUDIO");
        horloge.avancerJours(400);

        creerImmeubleBrut("Après expiration", "Douala", "Akwa")
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"));
        creerLotBrut(immeuble, "2", "STUDIO").andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"));
        changerStatut(lot, "EN_TRAVAUX").andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"));
        mvc.perform(multipart("/api/v1/immeubles/{id}/photos", immeuble)
                        .file(new MockMultipartFile("fichier", "p.png", "image/png", StockageTestSupport.png(8, 8))).with(ADMIN))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"));

        mvc.perform(get("/api/v1/immeubles").with(ADMIN)).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get("/api/v1/lots/{id}", lot).with(ADMIN)).andExpect(status().isOk());
        assertThat(jdbc.queryForObject("SELECT count(*) FROM immeuble", Integer.class)).isEqualTo(1);
    }

    @Test
    void sansLicenceOnPeutLireMaisPasCreer() throws Exception {
        mvc.perform(get("/api/v1/immeubles").with(ADMIN)).andExpect(status().isOk());
        mvc.perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                        .content(json("nom", "X", "adresse", "Y", "ville", "Z")))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("LICENCE_EXPIREE"))
                .andExpect(jsonPath("$.etat").value("AUCUNE"));
    }
}
