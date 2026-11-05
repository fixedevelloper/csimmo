-- Métadonnées des fichiers stockés dans le bucket S3 (module stockage)
CREATE TABLE fichier (
    id                 UUID                     NOT NULL,
    cle                TEXT                     NOT NULL,
    nom_origine        TEXT                     NOT NULL,
    type_mime          TEXT                     NOT NULL,
    taille             BIGINT                   NOT NULL,
    sha256             TEXT                     NOT NULL,
    chiffre            BOOLEAN                  NOT NULL,
    module_proprietaire TEXT                    NOT NULL,
    entite             TEXT                     NOT NULL,
    entite_id          UUID                     NOT NULL,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    version            BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fichier_cle_unique UNIQUE (cle)
);
CREATE INDEX fichier_proprietaire_idx ON fichier (module_proprietaire, entite, entite_id);
