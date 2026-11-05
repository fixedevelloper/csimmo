-- Étape 8.1 : paiement Mobile Money par agrégateur (webhook signé, vérification de statut). Règle n°8 : un paiement n'existe qu'APRÈS
-- confirmation de l'opérateur. bail_id, locataire_id, paiement_id : identifiants d'autres modules, sans clé étrangère.
CREATE TABLE ol_fournisseur (
    id         UUID                     NOT NULL,
    code       TEXT                     NOT NULL,
    actif      BOOLEAN                  NOT NULL DEFAULT FALSE,
    config     TEXT                     NOT NULL DEFAULT '{}',
    -- JSON des secrets, chiffré AES-GCM (jamais renvoyé par l'API)
    secrets    TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version    BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT ol_fournisseur_code_unique UNIQUE (code)
);

CREATE TABLE ol_transaction (
    id                    UUID                     NOT NULL,
    bail_id               UUID                     NOT NULL,
    locataire_id          UUID                     NOT NULL,
    fournisseur           TEXT                     NOT NULL,
    operateur             TEXT                     NOT NULL,
    telephone             TEXT                     NOT NULL,
    montant               BIGINT                   NOT NULL,
    statut                TEXT                     NOT NULL,
    reference_fournisseur TEXT,
    reference_operateur   TEXT,
    cle_idempotence       TEXT                     NOT NULL,
    empreinte             TEXT                     NOT NULL,
    paiement_id           UUID,
    erreur                TEXT,
    verifications         INT                      NOT NULL DEFAULT 0,
    derniere_verification TIMESTAMP WITH TIME ZONE,
    confirmee_le          TIMESTAMP WITH TIME ZONE,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    version               BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT ol_transaction_montant_positif CHECK (montant > 0),
    CONSTRAINT ol_transaction_operateur_valide CHECK (operateur IN ('MTN_MOMO', 'ORANGE_MONEY')),
    CONSTRAINT ol_transaction_statut_valide CHECK (statut IN ('EN_ATTENTE', 'CONFIRMEE', 'ECHOUEE', 'EXPIREE', 'ANOMALIE')),
    -- une transaction confirmée a toujours son paiement (et inversement : pas de paiement sans confirmation)
    CONSTRAINT ol_transaction_paiement_ssi_confirmee CHECK ((statut = 'CONFIRMEE') = (paiement_id IS NOT NULL)),
    CONSTRAINT ol_transaction_cle_unique UNIQUE (locataire_id, cle_idempotence)
);
-- une référence de l'agrégateur ne désigne qu'une transaction
CREATE UNIQUE INDEX ol_transaction_reference_unique ON ol_transaction (fournisseur, reference_fournisseur) WHERE reference_fournisseur IS NOT NULL;
CREATE INDEX ol_transaction_attente_idx ON ol_transaction (created_at) WHERE statut = 'EN_ATTENTE';
CREATE INDEX ol_transaction_locataire_idx ON ol_transaction (locataire_id, created_at DESC);

-- Webhooks déjà traités : un rejeu (l'agrégateur réessaie) ne refait rien. Aucune donnée personnelle : seulement l'identifiant d'événement.
CREATE TABLE ol_webhook_recu (
    id           UUID                     NOT NULL,
    fournisseur  TEXT                     NOT NULL,
    evenement_id TEXT                     NOT NULL,
    recu_le      TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ol_webhook_unique UNIQUE (fournisseur, evenement_id)
);
