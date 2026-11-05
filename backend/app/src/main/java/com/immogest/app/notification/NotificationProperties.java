package com.immogest.app.notification;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param simuler        canaux simulés (journalisés, aucun appel externe) : développement et tests
 * @param delaisReessai  attente avant chaque réessai ; sa taille = nombre de réessais (défaut 3 : 1 min, 5 min, 30 min)
 * @param langue         langue des messages (fr ou en)
 */
@ConfigurationProperties("immogest.notification")
public record NotificationProperties(Boolean simuler, List<Duration> delaisReessai, String langue) {

    public NotificationProperties {
        simuler = simuler != null && simuler;
        delaisReessai = delaisReessai == null || delaisReessai.isEmpty()
                ? List.of(Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofMinutes(30)) : delaisReessai;
        langue = langue == null || langue.isBlank() ? "fr" : langue;
    }

    public int tentativesMax() {
        return delaisReessai.size() + 1;
    }
}
