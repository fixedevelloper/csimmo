package com.immogest.app.licence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.RecordApplicationEvents;

/**
 * Test d'intégration du module licence : pas de licence ni d'ADMIN auto-installés (l'assistant est testable), pas de
 * planificateur (les tâches sont appelées directement), événements enregistrés.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SpringBootTest
@AutoConfigureMockMvc
@Import(LicenceTestSupport.class)
@TestPropertySource(properties = {"immogest.dev.installation-auto=false", "db-scheduler.enabled=false"})
@RecordApplicationEvents
public @interface LicenceTest {
}
