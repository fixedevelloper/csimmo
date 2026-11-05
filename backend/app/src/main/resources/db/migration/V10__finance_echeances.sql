-- Module finance (étape 4.1) : échéances, paiements (ici seulement l'avance), imputations.
-- bail_id n'est qu'un identifiant : aucune clé étrangère vers le module bail.
CREATE TABLE fin_echeance (
    id            UUID                     NOT NULL,
    bail_id       UUID                     NOT NULL,
    periode_debut DATE                     NOT NULL,
    periode_fin   DATE                     NOT NULL,
    date_echeance DATE                     NOT NULL,
    montant       BIGINT                   NOT NULL,
    montant_paye  BIGINT                   NOT NULL DEFAULT 0,
    statut        TEXT                     NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    version       BIGINT,
    PRIMARY KEY (id),
    -- idempotence du job : une seule échéance par bail et par période
    CONSTRAINT fin_echeance_periode_unique UNIQUE (bail_id, periode_debut),
    CONSTRAINT fin_echeance_statut_valide CHECK (statut IN ('A_VENIR', 'DUE', 'PARTIELLE', 'PAYEE', 'EN_RETARD', 'ANNULEE')),
    CONSTRAINT fin_echeance_montants_valides CHECK (montant >= 0 AND montant_paye >= 0 AND montant_paye <= montant),
    CONSTRAINT fin_echeance_dates_valides CHECK (periode_fin >= periode_debut)
);
CREATE INDEX fin_echeance_statut_idx ON fin_echeance (statut);
CREATE INDEX fin_echeance_bail_idx ON fin_echeance (bail_id);

-- Un encaissement. L'étape 4.2 y ajoute les paiements manuels et la contre-passation (jamais de suppression).
CREATE TABLE fin_paiement (
    id            UUID                     NOT NULL,
    bail_id       UUID                     NOT NULL,
    type          TEXT                     NOT NULL,
    montant       BIGINT                   NOT NULL,
    date_paiement DATE                     NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    version       BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fin_paiement_montant_positif CHECK (montant > 0)
);
CREATE INDEX fin_paiement_bail_idx ON fin_paiement (bail_id);
-- l'avance de signature n'existe qu'une fois par bail
CREATE UNIQUE INDEX fin_paiement_une_avance ON fin_paiement (bail_id) WHERE type = 'AVANCE';

CREATE TABLE fin_imputation (
    id          UUID                     NOT NULL,
    paiement_id UUID                     NOT NULL REFERENCES fin_paiement (id),
    echeance_id UUID                     NOT NULL REFERENCES fin_echeance (id),
    montant     BIGINT                   NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    version     BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fin_imputation_montant_positif CHECK (montant > 0)
);
CREATE INDEX fin_imputation_paiement_idx ON fin_imputation (paiement_id);
CREATE INDEX fin_imputation_echeance_idx ON fin_imputation (echeance_id);
