package com.immogest.app.stockage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(StockageTestSupport.class)
class FichierControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    StockageService service;

    private UUID fichier() {
        return service.enregistrer("test", "dossier", UUID.randomUUID(), "contrat é.pdf",
                new ByteArrayInputStream(StockageTestSupport.pdf()), false).id();
    }

    @Test
    void nonAuthentifie401() throws Exception {
        mvc.perform(get("/api/v1/fichiers/{id}", fichier()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("NON_AUTHENTIFIE"));
    }

    @Test
    void sansDroit403() throws Exception {
        mvc.perform(get("/api/v1/fichiers/{id}", fichier()).with(StockageTestSupport.jwtIntrus()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FICHIER_ACCES_REFUSE"));
    }

    @Test
    void inconnu404() throws Exception {
        mvc.perform(get("/api/v1/fichiers/{id}", UUID.randomUUID()).with(StockageTestSupport.jwtLecteur()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("FICHIER_INTROUVABLE"));
    }

    @Test
    void diffuseLeFluxAvecLesBonsEntetes() throws Exception {
        mvc.perform(get("/api/v1/fichiers/{id}", fichier()).with(StockageTestSupport.jwtLecteur()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(content().bytes(StockageTestSupport.pdf()));
    }
}
