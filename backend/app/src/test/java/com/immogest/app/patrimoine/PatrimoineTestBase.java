package com.immogest.app.patrimoine;

import static com.immogest.app.securite.SecuriteTestSupport.json;
import static com.immogest.app.securite.SecuriteTestSupport.jwtRole;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.securite.Role;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Outils communs : création d'immeubles et de lots par l'API, remise à zéro des tables du module. */
abstract class PatrimoineTestBase {

    protected static final RequestPostProcessor ADMIN = jwtRole(Role.ADMIN, UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
    protected static final RequestPostProcessor GESTIONNAIRE = jwtRole(Role.GESTIONNAIRE);

    protected abstract MockMvc mvc();

    protected abstract ObjectMapper mapper();

    protected static void viderPatrimoine(JdbcTemplate jdbc) {
        jdbc.execute("TRUNCATE patrimoine_photo, compteur, lot_equipement, lot, immeuble CASCADE");
    }

    protected JsonNode corps(ResultActions r) throws Exception {
        return mapper().readTree(r.andReturn().getResponse().getContentAsString());
    }

    protected ResultActions creerImmeubleBrut(String nom, String ville, String quartier) throws Exception {
        return mvc().perform(post("/api/v1/immeubles").with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("nom", nom, "adresse", "12 rue des Palmiers", "ville", ville, "quartier", quartier)));
    }

    protected UUID creerImmeuble(String nom, String ville, String quartier) throws Exception {
        return UUID.fromString(corps(creerImmeubleBrut(nom, ville, quartier)).path("id").asText());
    }

    protected ResultActions creerLotBrut(UUID immeubleId, String reference, String type, Object... extra) throws Exception {
        Object[] champs = new Object[6 + extra.length];
        champs[0] = "immeubleId";
        champs[1] = immeubleId == null ? null : immeubleId.toString();
        champs[2] = "reference";
        champs[3] = reference;
        champs[4] = "type";
        champs[5] = type;
        System.arraycopy(extra, 0, champs, 6, extra.length);
        return mvc().perform(post("/api/v1/lots").with(ADMIN).contentType(MediaType.APPLICATION_JSON).content(json(champs)));
    }

    protected UUID creerLot(UUID immeubleId, String reference, String type, Object... extra) throws Exception {
        return UUID.fromString(corps(creerLotBrut(immeubleId, reference, type, extra)).path("id").asText());
    }

    protected ResultActions changerStatut(UUID lotId, String statut) throws Exception {
        return mvc().perform(put("/api/v1/lots/{id}/statut", lotId).with(ADMIN).contentType(MediaType.APPLICATION_JSON)
                .content(json("statut", statut)));
    }

    protected static List<String> textes(JsonNode tableau, String champ) {
        return tableau.findValuesAsText(champ);
    }
}
