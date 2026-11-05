package com.immogest.licensing;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.List;

/**
 * Données de référence. Les clés ci-dessous sont des clés de TEST, générées une fois pour ces tests : elles ne signent
 * aucune licence réelle. Le format {@code GOLDEN_*} est figé : s'il change, les licences déjà émises seront invalides.
 */
final class Fixtures {

    static final String CLE_PRIVEE_TEST = "MC4CAQAwBQYDK2VwBCIEIF94I/XyPtqRt6Gy/pu69Fa8BXrLAatRr2QzqtOykHwx";
    static final String CLE_PUBLIQUE_TEST = "MCowBQYDK2VwAyEASgv/Mq4z8QUlGExPcveAHPYFl4DB9nP8AadmAxi0iWs=";

    /** SHA-256 de « immogest-empreinte-v1\nmachine-test\ninstall-test ». */
    static final String EMPREINTE = "e4b0db06d22596999a6cf16f41031182db7f4c753c94ea97a00ca1e79d95c464";

    static final String GOLDEN_CANONIQUE = "{\"format\":1,\"licence\":{\"client\":\"Société_Étoile\","
            + "\"dateDebut\":\"2026-01-01\",\"dateExpiration\":\"2026-12-31\","
            + "\"empreinteServeur\":\"" + EMPREINTE + "\",\"fonctionnalites\":[\"mobile\",\"relances\"],"
            + "\"licenseId\":\"L-GOLDEN-0001\",\"palierLots\":50}}";

    /** Ed25519 est déterministe (RFC 8032) : cette signature est toujours la même pour les mêmes octets et la même clé. */
    static final String GOLDEN_SIGNATURE =
            "sOofMfRNt-ISnL5Ev4Y2bwlorWeNWNTbvQpyCwYxH8qpJTXUJyEDy6__rpPhbLQ_O8WmMQIFiHx4_ty1974SAg";

    static final String GOLDEN_FICHIER = """
            {
              "format" : 1,
              "licence" : {
                "licenseId" : "L-GOLDEN-0001",
                "client" : "Société_Étoile",
                "palierLots" : 50,
                "dateDebut" : "2026-01-01",
                "dateExpiration" : "2026-12-31",
                "empreinteServeur" : "%s",
                "fonctionnalites" : [ "mobile", "relances" ]
              },
              "signature" : "%s"
            }
            """.formatted(EMPREINTE, GOLDEN_SIGNATURE);

    static final LocalDate DEBUT = LocalDate.of(2026, 1, 1);
    static final LocalDate EXPIRATION = LocalDate.of(2026, 12, 31);

    private Fixtures() {
    }

    static PrivateKey clePrivee() {
        return Ed25519.decoderClePrivee(CLE_PRIVEE_TEST);
    }

    static PublicKey clePublique() {
        return Ed25519.decoderClePublique(CLE_PUBLIQUE_TEST);
    }

    static Licence licence() {
        return new Licence("L-GOLDEN-0001", "Société_Étoile", 50, DEBUT, EXPIRATION, EMPREINTE, List.of("relances", "mobile"));
    }

    static LicenceSignee licenceSignee() {
        return LicenceSignee.signer(licence(), clePrivee());
    }
}
