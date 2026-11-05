-- Module patrimoine : immeubles, lots, équipements, compteurs, photos.
-- Rien n'est supprimé : « supprimer » archive (un lot peut être référencé par des baux qui ne portent que son identifiant).

CREATE TABLE immeuble (
    id          UUID                     NOT NULL,
    nom         TEXT                     NOT NULL,
    adresse     TEXT                     NOT NULL,
    quartier    TEXT,
    ville       TEXT                     NOT NULL,
    latitude    NUMERIC(9, 6),
    longitude   NUMERIC(9, 6),
    description TEXT,
    archive     BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    version     BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT immeuble_gps_complet CHECK ((latitude IS NULL) = (longitude IS NULL)),
    CONSTRAINT immeuble_latitude_valide CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT immeuble_longitude_valide CHECK (longitude BETWEEN -180 AND 180)
);
CREATE INDEX immeuble_ville_idx ON immeuble (lower(ville));
CREATE INDEX immeuble_quartier_idx ON immeuble (lower(quartier));

CREATE TABLE lot (
    id              UUID                     NOT NULL,
    immeuble_id     UUID                     NOT NULL REFERENCES immeuble (id),
    reference       TEXT                     NOT NULL,
    type            TEXT                     NOT NULL,
    surface_m2      NUMERIC(10, 2),
    pieces          INT,
    loyer_indicatif BIGINT,
    statut          TEXT                     NOT NULL,
    archive         BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    version         BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT lot_reference_unique UNIQUE (immeuble_id, reference),
    CONSTRAINT lot_type_valide CHECK (type IN ('APPARTEMENT', 'STUDIO', 'BOUTIQUE', 'BUREAU', 'TERRAIN')),
    CONSTRAINT lot_statut_valide CHECK (statut IN ('LIBRE', 'OCCUPE', 'EN_TRAVAUX')),
    CONSTRAINT lot_surface_positive CHECK (surface_m2 IS NULL OR surface_m2 > 0),
    CONSTRAINT lot_pieces_positives CHECK (pieces IS NULL OR pieces >= 0),
    CONSTRAINT lot_loyer_positif CHECK (loyer_indicatif IS NULL OR loyer_indicatif >= 0)
);
CREATE INDEX lot_immeuble_idx ON lot (immeuble_id);
CREATE INDEX lot_statut_idx ON lot (statut) WHERE NOT archive;

CREATE TABLE lot_equipement (
    lot_id  UUID NOT NULL REFERENCES lot (id),
    libelle TEXT NOT NULL,
    PRIMARY KEY (lot_id, libelle)
);

CREATE TABLE compteur (
    id         UUID                     NOT NULL,
    lot_id     UUID                     NOT NULL REFERENCES lot (id),
    type       TEXT                     NOT NULL,
    numero     TEXT                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version    BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT compteur_type_valide CHECK (type IN ('EAU', 'ELECTRICITE', 'GAZ'))
);
CREATE INDEX compteur_lot_idx ON compteur (lot_id);

-- Photos d'un immeuble ou d'un lot : le fichier lui-même est dans le stockage S3 (module stockage)
CREATE TABLE patrimoine_photo (
    id         UUID                     NOT NULL,
    cible_type TEXT                     NOT NULL,
    cible_id   UUID                     NOT NULL,
    fichier_id UUID                     NOT NULL,
    legende    TEXT,
    ordre      INT                      NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version    BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT patrimoine_photo_cible_valide CHECK (cible_type IN ('IMMEUBLE', 'LOT')),
    CONSTRAINT patrimoine_photo_fichier_unique UNIQUE (fichier_id)
);
CREATE INDEX patrimoine_photo_cible_idx ON patrimoine_photo (cible_type, cible_id, ordre);
