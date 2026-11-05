-- Module tiers : propriétaires tiers, mandats, locataires, garants, prestataires, pièces d'identité.
-- Les numéros de CNI sont chiffrés par l'application (AES-GCM) : la base ne contient jamais le clair.
-- Rien n'est supprimé : « supprimer » archive.
CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE proprietaire_tiers (
    id                      UUID                     NOT NULL,
    nom                     TEXT                     NOT NULL,
    telephone               TEXT,
    email                   TEXT,
    adresse                 TEXT,
    mode_reversement        TEXT                     NOT NULL DEFAULT 'VIREMENT',
    coordonnees_reversement TEXT,
    utilisateur_id          UUID,
    notes                   TEXT,
    archive                 BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,
    version                 BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT proprietaire_tiers_contact CHECK (telephone IS NOT NULL OR email IS NOT NULL),
    CONSTRAINT proprietaire_tiers_mode_valide CHECK (mode_reversement IN ('VIREMENT', 'MOBILE_MONEY', 'ESPECES', 'CHEQUE'))
);
CREATE UNIQUE INDEX proprietaire_tiers_utilisateur_unique ON proprietaire_tiers (utilisateur_id) WHERE utilisateur_id IS NOT NULL;
CREATE INDEX proprietaire_tiers_nom_idx ON proprietaire_tiers (lower(nom));

CREATE TABLE mandat (
    id                 UUID                     NOT NULL,
    proprietaire_id    UUID                     NOT NULL REFERENCES proprietaire_tiers (id),
    taux_honoraires_bp INT                      NOT NULL,
    date_debut         DATE                     NOT NULL,
    date_fin           DATE,
    notes              TEXT,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    version            BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT mandat_taux_valide CHECK (taux_honoraires_bp BETWEEN 0 AND 10000),
    CONSTRAINT mandat_dates_valides CHECK (date_fin IS NULL OR date_fin >= date_debut),
    -- un propriétaire n'a jamais deux mandats qui se chevauchent (date_fin vide = sans limite)
    CONSTRAINT mandat_pas_de_chevauchement EXCLUDE USING gist (
        proprietaire_id WITH =, daterange(date_debut, date_fin, '[]') WITH &&)
);

CREATE TABLE locataire (
    id                 UUID                     NOT NULL,
    nom                TEXT                     NOT NULL,
    profession         TEXT,
    telephone          TEXT,
    email              TEXT,
    adresse            TEXT,
    date_naissance     DATE,
    numero_cni_chiffre TEXT,
    utilisateur_id     UUID,
    notes              TEXT,
    archive            BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    version            BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT locataire_contact CHECK (telephone IS NOT NULL OR email IS NOT NULL)
);
CREATE UNIQUE INDEX locataire_utilisateur_unique ON locataire (utilisateur_id) WHERE utilisateur_id IS NOT NULL;
CREATE INDEX locataire_nom_idx ON locataire (lower(nom));
CREATE INDEX locataire_telephone_idx ON locataire (telephone);

CREATE TABLE garant (
    id                 UUID                     NOT NULL,
    locataire_id       UUID                     NOT NULL REFERENCES locataire (id),
    nom                TEXT                     NOT NULL,
    lien               TEXT,
    profession         TEXT,
    telephone          TEXT                     NOT NULL,
    adresse            TEXT,
    numero_cni_chiffre TEXT,
    archive            BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    version            BIGINT,
    PRIMARY KEY (id)
);
CREATE INDEX garant_locataire_idx ON garant (locataire_id);

CREATE TABLE prestataire (
    id             UUID                     NOT NULL,
    nom            TEXT                     NOT NULL,
    metier         TEXT,
    telephone      TEXT,
    email          TEXT,
    adresse        TEXT,
    utilisateur_id UUID,
    notes          TEXT,
    archive        BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    version        BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT prestataire_contact CHECK (telephone IS NOT NULL OR email IS NOT NULL)
);
CREATE UNIQUE INDEX prestataire_utilisateur_unique ON prestataire (utilisateur_id) WHERE utilisateur_id IS NOT NULL;
CREATE INDEX prestataire_nom_idx ON prestataire (lower(nom));

-- Pièces d'identité (fichiers chiffrés dans le stockage S3) d'un locataire ou d'un garant
CREATE TABLE tiers_piece (
    id          UUID                     NOT NULL,
    cible_type  TEXT                     NOT NULL,
    cible_id    UUID                     NOT NULL,
    fichier_id  UUID                     NOT NULL,
    type_piece  TEXT                     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    version     BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT tiers_piece_cible_valide CHECK (cible_type IN ('LOCATAIRE', 'GARANT')),
    CONSTRAINT tiers_piece_type_valide CHECK (type_piece IN ('CNI_RECTO', 'CNI_VERSO', 'PASSEPORT', 'AUTRE')),
    CONSTRAINT tiers_piece_fichier_unique UNIQUE (fichier_id)
);
CREATE INDEX tiers_piece_cible_idx ON tiers_piece (cible_type, cible_id);
