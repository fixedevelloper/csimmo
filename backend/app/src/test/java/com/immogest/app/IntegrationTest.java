package com.immogest.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/** Test d'intégration complet : application, MockMvc, PostgreSQL et SeaweedFS (Testcontainers). */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(properties = {"immogest.notification.simuler=true", "immogest.paiementenligne.simuler=true",
        // la limite de débit par IP ne doit pas gêner les centaines de connexions des tests (un test dédié la rétablit)
        "immogest.securite.limite-auth-par-minute=100000", "immogest.securite.limite-refresh-par-minute=100000"})
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, SeaweedFsTestConfiguration.class})
public @interface IntegrationTest {
}
