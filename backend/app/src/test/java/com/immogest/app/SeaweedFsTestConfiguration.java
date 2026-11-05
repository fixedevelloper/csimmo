package com.immogest.app;

import java.time.Duration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;

/** Serveur S3 réel (SeaweedFS, comme livré chez le client), partagé par tous les tests du JVM. */
@TestConfiguration(proxyBeanMethods = false)
public class SeaweedFsTestConfiguration {

    public static final String ACCESS_KEY = "immogest";
    public static final String SECRET_KEY = "immogest-secret";
    /** 32 octets en base64. */
    public static final String CLE_CHIFFREMENT = "dGVzdC1vbmx5LWNsZS1hZXMtMjU2LWJpdHMtISEhISE=";

    private static final String S3_JSON = """
            {"identities":[{"name":"test","credentials":[{"accessKey":"%s","secretKey":"%s"}],
            "actions":["Admin","Read","List","Tagging","Write"]}]}
            """.formatted(ACCESS_KEY, SECRET_KEY);

    private static final GenericContainer<?> SEAWEEDFS = new GenericContainer<>("chrislusf/seaweedfs:4.50")
            .withCommand("server", "-dir=/data", "-s3", "-s3.config=/etc/seaweedfs/s3.json")
            .withCopyToContainer(Transferable.of(S3_JSON), "/etc/seaweedfs/s3.json")
            .withExposedPorts(8333)
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(90)));

    static {
        SEAWEEDFS.start();
    }

    @Bean
    DynamicPropertyRegistrar seaweedfsProprietes() {
        return registry -> {
            registry.add("immogest.stockage.endpoint",
                    () -> "http://" + SEAWEEDFS.getHost() + ":" + SEAWEEDFS.getMappedPort(8333));
            registry.add("immogest.stockage.access-key", () -> ACCESS_KEY);
            registry.add("immogest.stockage.secret-key", () -> SECRET_KEY);
            registry.add("immogest.stockage.cle-chiffrement", () -> CLE_CHIFFREMENT);
        };
    }
}
