-- Étape 6.1 : relances, paramètres de relance, mises en demeure. echeance_id / bail_id : identifiants, aucune clé étrangère inter-modules.
CREATE TABLE rel_parametres (
    id                           UUID                     NOT NULL,
    singleton                    BOOLEAN                  NOT NULL DEFAULT TRUE,
    actif                        BOOLEAN                  NOT NULL DEFAULT TRUE,
    -- jours par rapport à l'échéance, séparés par des virgules : -5 = 5 jours avant, 3 = 3 jours après
    niveaux                      TEXT                     NOT NULL DEFAULT '-5,3,10,30',
    delai_mise_en_demeure_jours  INT                      NOT NULL DEFAULT 8,
    created_at                   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                   TIMESTAMP WITH TIME ZONE NOT NULL,
    version                      BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT rel_parametres_une_ligne UNIQUE (singleton),
    CONSTRAINT rel_parametres_singleton CHECK (singleton),
    CONSTRAINT rel_parametres_delai_valide CHECK (delai_mise_en_demeure_jours BETWEEN 1 AND 90)
);

CREATE TABLE rel_relance (
    id            UUID                     NOT NULL,
    echeance_id   UUID                     NOT NULL,
    bail_id       UUID                     NOT NULL,
    locataire_id  UUID                     NOT NULL,
    type          TEXT                     NOT NULL,
    niveau        INT,
    date_relance  DATE                     NOT NULL,
    resultat      TEXT                     NOT NULL,
    message_id    UUID,
    reste_du      BIGINT                   NOT NULL,
    jours_retard  INT                      NOT NULL,
    auteur        UUID,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    version       BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT rel_relance_type_valide CHECK (type IN ('AUTO', 'MANUELLE')),
    CONSTRAINT rel_relance_resultat_valide CHECK (resultat IN ('EN_FILE', 'AUCUN_CANAL', 'IGNOREE')),
    CONSTRAINT rel_relance_niveau_auto CHECK (type = 'MANUELLE' OR niveau IS NOT NULL)
);
-- idempotence du job : un niveau n'est traité qu'une fois par échéance
CREATE UNIQUE INDEX rel_relance_niveau_unique ON rel_relance (echeance_id, niveau) WHERE type = 'AUTO';
CREATE INDEX rel_relance_echeance_idx ON rel_relance (echeance_id, created_at DESC);
CREATE INDEX rel_relance_bail_idx ON rel_relance (bail_id);

CREATE TABLE rel_compteur_numero (
    annee   INT    NOT NULL PRIMARY KEY,
    dernier BIGINT NOT NULL
);

CREATE TABLE rel_mise_en_demeure (
    id                UUID                     NOT NULL,
    numero            TEXT                     NOT NULL,
    bail_id           UUID                     NOT NULL,
    echeance_id       UUID                     NOT NULL,
    montant_arrieres  BIGINT                   NOT NULL,
    delai_jours       INT                      NOT NULL,
    date_emission     DATE                     NOT NULL,
    fichier_id        UUID                     NOT NULL,
    emis_par          UUID,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    version           BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT rel_med_numero_unique UNIQUE (numero),
    CONSTRAINT rel_med_fichier_unique UNIQUE (fichier_id),
    CONSTRAINT rel_med_echeance_unique UNIQUE (echeance_id)
);
CREATE INDEX rel_med_bail_idx ON rel_mise_en_demeure (bail_id);
