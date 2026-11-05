package com.immogest.app.stockage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.hamcrest.Matchers;

@SpringBootTest(properties = "immogest.stockage.url-presignee=true")
@AutoConfigureMockMvc
@Import(StockageTestSupport.class)
class FichierPresigneTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    StockageService service;

    @Test
    void fichierEnClairRedirigeVersUneUrlPresignee() throws Exception {
        UUID id = service.enregistrer("test", "dossier", UUID.randomUUID(), "a.pdf",
                new ByteArrayInputStream(StockageTestSupport.pdf()), false).id();

        mvc.perform(get("/api/v1/fichiers/{id}", id).with(StockageTestSupport.jwtLecteur()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", Matchers.containsString("X-Amz-Signature")));
    }

    @Test
    void fichierChiffreResteDiffuseParLApiMemeAvecPresignee() throws Exception {
        UUID id = service.enregistrer("test", "dossier", UUID.randomUUID(), "cni.pdf",
                new ByteArrayInputStream(StockageTestSupport.pdf()), true).id();

        mvc.perform(get("/api/v1/fichiers/{id}", id).with(StockageTestSupport.jwtLecteur())).andExpect(status().isOk());
    }
}
