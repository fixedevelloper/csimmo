package com.immogest.app;

import com.immogest.app.shared.LocatairesDuPerimetre;
import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Tests de module isolés : {@link LocatairesDuPerimetre} est implémenté par {@code bail}, absent de ces contextes. En production son absence
 * ferait échouer le démarrage (volontairement : jamais de filtrage désactivé en silence) ; ici, aucune restriction.
 */
@TestConfiguration(proxyBeanMethods = false)
public class PerimetreTestSupport {

    @Bean
    LocatairesDuPerimetre locatairesDuPerimetreSansRestriction() {
        return Optional::empty;
    }
}
