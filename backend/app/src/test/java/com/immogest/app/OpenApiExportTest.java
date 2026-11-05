package com.immogest.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/** Écrit {@code api/openapi.yaml}. Activé seulement par le profil Maven {@code openapi}. */
@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, SeaweedFsTestConfiguration.class})
@EnabledIfSystemProperty(named = "openapi.export", matches = "true")
class OpenApiExportTest {

    @Autowired
    MockMvc mvc;

    @Test
    void exporteOpenApi() throws Exception {
        var reponse = mvc.perform(get("/v3/api-docs.yaml")).andReturn().getResponse();
        if (reponse.getStatus() != 200) {
            throw new AssertionError("Export OpenAPI impossible : HTTP " + reponse.getStatus());
        }
        String yaml = reponse.getContentAsString(StandardCharsets.UTF_8);
        Path cible = Path.of("../../api/openapi.yaml");
        Files.createDirectories(cible.getParent());
        Files.writeString(cible, yaml, StandardCharsets.UTF_8);
    }
}
