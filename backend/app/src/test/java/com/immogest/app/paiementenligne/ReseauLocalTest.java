package com.immogest.app.paiementenligne;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.immogest.app.IntegrationTest;
import com.immogest.app.securite.Role;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/** Installation en réseau local (sans Internet) : le paiement en ligne est indisponible, quel que soit le fournisseur configuré. */
@IntegrationTest
@TestPropertySource(properties = "immogest.reseau-local=true")
class ReseauLocalTest {

    @Autowired
    MockMvc mvc;

    @Test
    void initierRefuseEnReseauLocal() throws Exception {
        mvc.perform(post("/api/v1/paiements-en-ligne").with(jwtRole(Role.LOCATAIRE, UUID.randomUUID())).header("Idempotency-Key", "k1").contentType(MediaType.APPLICATION_JSON)
                .content(json("bailId", UUID.randomUUID().toString(), "montant", 1000, "operateur", "MTN_MOMO", "telephone", "+237699112233")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("PAIEMENT_EN_LIGNE_INDISPONIBLE"));
    }
}
