package com.immogest.app.stockage.infra;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuration {@code immogest.stockage.*}. */
@ConfigurationProperties("immogest.stockage")
public record StockageProperties(
        String endpoint,
        String region,
        String bucket,
        String accessKey,
        String secretKey,
        boolean pathStyle,
        boolean urlPresignee,
        boolean creerBucketAuDemarrage,
        String cleChiffrement) {
}
