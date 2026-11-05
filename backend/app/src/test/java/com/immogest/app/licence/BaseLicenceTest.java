package com.immogest.app.licence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.immogest.app.licence.infra.RegistreInstallation;
import com.immogest.licensing.Ed25519;
import com.immogest.licensing.Licence;
import com.immogest.licensing.LicenceSignee;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;

/** Base des tests du module licence : base remise à zéro, horloge et serveur simulé réinitialisés avant chaque test. */
@LicenceTest
abstract class BaseLicenceTest {

    @Autowired
    protected LicenceService licences;
    @Autowired
    protected InstallationService installation;
    @Autowired
    protected RegistreInstallation registre;
    @Autowired
    protected MutableClock horloge;
    @Autowired
    protected ServeurLicencesFake serveur;
    @Autowired
    protected JdbcTemplate jdbc;
    @Autowired
    protected ApplicationEvents evenements;
    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper json;

    @BeforeEach
    void remiseAZero() {
        horloge.reinitialiser();
        serveur.reinitialiser();
        jdbc.execute("TRUNCATE lic_licence, sec_refresh_token, sec_otp_code, utilisateur CASCADE");
        jdbc.update("UPDATE lic_installation SET installee = FALSE, installee_le = NULL");
        registre.oublier();
        licences.invalider();
        evenements.clear();
    }

    protected LocalDate aujourdhui() {
        return LocalDate.now(horloge.withZone(ZoneId.of("Africa/Douala")));
    }

    protected Licence licence(String id, int palier, LocalDate debut, LocalDate fin) {
        return new Licence(id, "Client Test", palier, debut, fin, LicenceTestSupport.EMPREINTE, List.of("relances", "mobile"));
    }

    /** Licence commencée il y a 10 jours, valable encore 365 jours. */
    protected Licence licenceValide() {
        return licence("L-TEST-1", 50, aujourdhui().minusDays(10), aujourdhui().plusDays(365));
    }

    protected String signer(Licence licence) {
        return LicenceSignee.signer(licence, LicenceTestSupport.EDITEUR.getPrivate()).versJson();
    }

    protected String signerParUnFaussaire(Licence licence) {
        return LicenceSignee.signer(licence, LicenceTestSupport.FAUSSAIRE.getPrivate()).versJson();
    }

    protected String contenuValide() {
        return signer(licenceValide());
    }

    protected long nombreD(Class<?> type) {
        return evenements.stream(type).count();
    }

    protected String clePubliqueDeTest() {
        return Ed25519.encoder(LicenceTestSupport.EDITEUR.getPublic());
    }
}
