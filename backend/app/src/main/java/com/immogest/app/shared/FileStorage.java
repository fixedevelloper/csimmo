package com.immogest.app.shared;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;

/** Stockage de fichiers (implémenté par le module {@code stockage} au-dessus de l'API S3). */
public interface FileStorage {

    void put(String cle, InputStream flux, String typeMime, long taille);

    InputStream get(String cle);

    void delete(String cle);

    URI urlTemporaire(String cle, Duration duree);
}
