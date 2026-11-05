package com.immogest.app.stockage.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

/** Crée le bucket au démarrage s'il n'existe pas (le serveur S3 peut mettre quelques secondes à être prêt). */
@Component
class BucketInitialiseur implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(BucketInitialiseur.class);
    private static final int TENTATIVES = 15;

    private final S3Client s3;
    private final StockageProperties proprietes;

    BucketInitialiseur(S3Client s3, StockageProperties proprietes) {
        this.s3 = s3;
        this.proprietes = proprietes;
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        if (!proprietes.creerBucketAuDemarrage()) {
            return;
        }
        RuntimeException derniere = null;
        for (int i = 1; i <= TENTATIVES; i++) {
            try {
                assurerBucket();
                return;
            } catch (RuntimeException e) {
                derniere = e;
                LOG.warn("Stockage S3 indisponible (tentative {}/{}) : {}", i, TENTATIVES, e.getMessage());
                Thread.sleep(2000);
            }
        }
        throw new IllegalStateException("Impossible de joindre le stockage S3 " + proprietes.endpoint(), derniere);
    }

    private void assurerBucket() {
        String bucket = proprietes.bucket();
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            s3.createBucket(b -> b.bucket(bucket));
            LOG.info("Bucket S3 '{}' créé", bucket);
        }
    }
}
