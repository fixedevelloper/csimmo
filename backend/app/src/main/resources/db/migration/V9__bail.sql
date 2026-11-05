-- Module bail : baux, garants du bail, documents PDF, modèle de bail.
-- lot_id et locataire_id sont de simples identifiants (jamais de clé étrangère vers un autre module).
CREATE SEQUENCE bail_numero_seq START 1;

CREATE TABLE bail (
    id                 UUID                     NOT NULL,
    numero             TEXT                     NOT NULL,
    lot_id             UUID                     NOT NULL,
    locataire_id       UUID                     NOT NULL,
    bail_precedent_id  UUID REFERENCES bail (id),
    statut             TEXT                     NOT NULL,
    date_debut         DATE                     NOT NULL,
    date_fin           DATE,
    loyer              BIGINT                   NOT NULL,
    charges            BIGINT                   NOT NULL DEFAULT 0,
    periodicite        TEXT                     NOT NULL,
    jour_echeance      INT                      NOT NULL,
    caution            BIGINT                   NOT NULL DEFAULT 0,
    mois_avance        INT                      NOT NULL DEFAULT 0,
    preavis_mois       INT                      NOT NULL DEFAULT 3,
    date_notification_preavis DATE,
    date_sortie        DATE,
    motif_resiliation  TEXT,
    -- dernier jour d'effet : la sortie si le bail est résilié, sinon la fin prévue (NULL = sans limite)
    date_fin_effective DATE,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    version            BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT bail_numero_unique UNIQUE (numero),
    CONSTRAINT bail_statut_valide CHECK (statut IN ('BROUILLON', 'ACTIF', 'RESILIE', 'TERMINE')),
    CONSTRAINT bail_periodicite_valide CHECK (periodicite IN ('MENSUELLE', 'TRIMESTRIELLE', 'ANNUELLE')),
    CONSTRAINT bail_jour_valide CHECK (jour_echeance BETWEEN 1 AND 31),
    CONSTRAINT bail_montants_valides CHECK (loyer > 0 AND charges >= 0 AND caution >= 0),
    CONSTRAINT bail_mois_avance_valide CHECK (mois_avance BETWEEN 0 AND 24),
    CONSTRAINT bail_preavis_valide CHECK (preavis_mois BETWEEN 0 AND 12),
    CONSTRAINT bail_dates_valides CHECK (date_fin IS NULL OR date_fin > date_debut),
    -- Règle métier n°1 : un lot a au plus un bail en vigueur à une date donnée (les brouillons ne comptent pas)
    CONSTRAINT bail_un_seul_actif_par_lot EXCLUDE USING gist (
        lot_id WITH =, daterange(date_debut, date_fin_effective, '[]') WITH &&)
        WHERE (statut IN ('ACTIF', 'RESILIE'))
);
CREATE INDEX bail_lot_idx ON bail (lot_id);
CREATE INDEX bail_locataire_idx ON bail (locataire_id);
CREATE INDEX bail_statut_idx ON bail (statut);

CREATE TABLE bail_garant (
    bail_id   UUID NOT NULL REFERENCES bail (id),
    garant_id UUID NOT NULL,
    PRIMARY KEY (bail_id, garant_id)
);

-- Exemplaires PDF générés (figés : un bail signé ne change pas si le modèle change ensuite)
CREATE TABLE bail_document (
    id         UUID                     NOT NULL,
    bail_id    UUID                     NOT NULL REFERENCES bail (id),
    fichier_id UUID                     NOT NULL,
    numero_version INT                  NOT NULL,
    genere_le  TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version    BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT bail_document_fichier_unique UNIQUE (fichier_id),
    CONSTRAINT bail_document_version_unique UNIQUE (bail_id, numero_version)
);

-- Textes modifiables du bail (une seule ligne ; valeurs par défaut dans l'application)
CREATE TABLE bail_modele (
    id              UUID                     NOT NULL,
    singleton       BOOLEAN                  NOT NULL DEFAULT TRUE,
    bailleur_nom    TEXT                     NOT NULL,
    bailleur_adresse TEXT,
    preambule       TEXT                     NOT NULL,
    clauses         TEXT                     NOT NULL,
    pied_de_page    TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    version         BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT bail_modele_une_ligne UNIQUE (singleton),
    CONSTRAINT bail_modele_singleton CHECK (singleton)
);
