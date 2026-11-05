package com.immogest.app.licence.infra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Quelles requêtes le mode lecture seule bloque-t-il ? (règle de licence n°4) */
class LicenceFilterTest {

    @ParameterizedTest(name = "{0} {1} → soumise au contrôle : {2}")
    @CsvSource({
            // lectures : jamais bloquées
            "GET,/api/v1/utilisateurs,false",
            "GET,/api/v1/baux/123,false",
            "HEAD,/api/v1/baux,false",
            "OPTIONS,/api/v1/baux,false",
            "get,/api/v1/baux,false",
            // écritures métier : bloquées
            "POST,/api/v1/utilisateurs,true",
            "PUT,/api/v1/baux/123,true",
            "PATCH,/api/v1/baux/123,true",
            "DELETE,/api/v1/lots/1,true",
            "POST,/api/v1/fichiers,true",
            "POST,/api/v1/webhooks/cinetpay,true",
            // exceptions : authentification, licence, installation, exports, sauvegardes
            "POST,/api/v1/auth/login,false",
            "POST,/api/v1/auth/refresh,false",
            "POST,/api/v1/auth/logout,false",
            "POST,/api/v1/auth/otp/verifier,false",
            "POST,/api/v1/auth/mot-de-passe,false",
            "POST,/api/v1/licence,false",
            "POST,/api/v1/installation,false",
            "POST,/api/v1/exports/encaissements,false",
            "POST,/api/v1/sauvegardes,false",
            "POST,/api/v1/sauvegardes/restaurer,false",
            // un préfixe voisin n'est pas une exception
            "POST,/api/v1/licences-autres,true",
            "POST,/api/v1/authentification,true",
            "POST,/api/v1/exports-secrets,true",
            // hors API (actuator, documentation) : non concernées
            "POST,/actuator/health,false",
            "POST,/v3/api-docs,false"
    })
    void matriceDesRequetes(String methode, String chemin, boolean soumise) {
        assertThat(LicenceFilter.soumise(methode, chemin)).isEqualTo(soumise);
    }
}
