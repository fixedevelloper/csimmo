package com.immogest.app.stockage.infra;

import com.immogest.app.shared.FileStorage;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

/** Implémentation de {@link FileStorage} sur l'API S3 uniquement (aucune API propre à un serveur). */
@Component
class S3FileStorage implements FileStorage {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final String bucket;

    S3FileStorage(S3Client s3, S3Presigner presigner, StockageProperties proprietes) {
        this.s3 = s3;
        this.presigner = presigner;
        this.bucket = proprietes.bucket();
    }

    @Override
    public void put(String cle, InputStream flux, String typeMime, long taille) {
        s3.putObject(PutObjectRequest.builder().bucket(bucket).key(cle).contentType(typeMime)
                .contentLength(taille).build(), RequestBody.fromInputStream(flux, taille));
    }

    @Override
    public InputStream get(String cle) {
        return s3.getObject(GetObjectRequest.builder().bucket(bucket).key(cle).build());
    }

    @Override
    public void delete(String cle) {
        s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(cle).build());
    }

    @Override
    public URI urlTemporaire(String cle, Duration duree) {
        var requete = GetObjectPresignRequest.builder()
                .signatureDuration(duree)
                .getObjectRequest(GetObjectRequest.builder().bucket(bucket).key(cle).build())
                .build();
        return URI.create(presigner.presignGetObject(requete).url().toString());
    }
}
