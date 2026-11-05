package com.immogest.licensing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CanonicalJsonTest {

    @Test
    void formatFigeDeLaLicenceDeReference() {
        // Garde-fou de compatibilité : modifier ce format invaliderait toutes les licences déjà émises.
        assertThat(new String(LicenceSignee.octetsSignes(Fixtures.licence()), StandardCharsets.UTF_8))
                .isEqualTo(Fixtures.GOLDEN_CANONIQUE);
    }

    @Test
    void clesTrieesQuelQueSoitLOrdreDInsertion() {
        Map<String, Object> a = new LinkedHashMap<>();
        a.put("b", 1);
        a.put("a", 2);
        Map<String, Object> b = new LinkedHashMap<>();
        b.put("a", 2);
        b.put("b", 1);

        assertThat(CanonicalJson.ecrire(a)).isEqualTo("{\"a\":2,\"b\":1}").isEqualTo(CanonicalJson.ecrire(b));
    }

    @Test
    void imbricationListesEtTypes() {
        Object valeur = Map.of("z", List.of("x", "y"), "n", 5L, "ok", true, "o", Map.of("k", "v"));

        assertThat(CanonicalJson.ecrire(valeur)).isEqualTo("{\"n\":5,\"o\":{\"k\":\"v\"},\"ok\":true,\"z\":[\"x\",\"y\"]}");
    }

    @Test
    void echappementUnique() {
        assertThat(CanonicalJson.ecrire("a\"b\\c")).isEqualTo("\"a\\\"b\\\\c\"");
        assertThat(CanonicalJson.ecrire("l1\nl2\t\u0001")).isEqualTo("\"l1\\u000al2\\u0009\\u0001\"");
        assertThat(CanonicalJson.ecrire("é/€")).isEqualTo("\"é/€\""); // pas d'échappement des caractères non ASCII ni de /
    }

    @Test
    void refuseNullEtTypesInconnus() {
        assertThatThrownBy(() -> CanonicalJson.ecrire(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CanonicalJson.ecrire(Map.of("a", 1.5))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CanonicalJson.ecrire(Map.of("a", new Object()))).isInstanceOf(IllegalArgumentException.class);
    }
}
