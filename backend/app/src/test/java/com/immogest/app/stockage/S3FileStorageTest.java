package com.immogest.app.stockage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.immogest.app.shared.FileStorage;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@SpringBootTest
@Import(StockageTestSupport.class)
class S3FileStorageTest {

    @Autowired
    FileStorage stockage;

    @Test
    void envoiLectureSuppression() throws Exception {
        String cle = "test/essai/" + UUID.randomUUID() + "/" + UUID.randomUUID() + ".pdf";
        byte[] contenu = "bonjour".getBytes(StandardCharsets.UTF_8);

        stockage.put(cle, new ByteArrayInputStream(contenu), "text/plain", contenu.length);
        try (var flux = stockage.get(cle)) {
            assertThat(flux.readAllBytes()).isEqualTo(contenu);
        }

        stockage.delete(cle);
        assertThatThrownBy(() -> stockage.get(cle)).isInstanceOf(NoSuchKeyException.class);
    }

    @Test
    void urlTemporaireDonneAccesAuContenu() throws Exception {
        String cle = "test/essai/" + UUID.randomUUID() + ".pdf";
        byte[] contenu = "presigne".getBytes(StandardCharsets.UTF_8);
        stockage.put(cle, new ByteArrayInputStream(contenu), "text/plain", contenu.length);

        URI url = stockage.urlTemporaire(cle, Duration.ofMinutes(5));
        var reponse = HttpClient.newHttpClient().send(HttpRequest.newBuilder(url).build(),
                HttpResponse.BodyHandlers.ofByteArray());

        assertThat(reponse.statusCode()).isEqualTo(200);
        assertThat(reponse.body()).isEqualTo(contenu);
    }
}
