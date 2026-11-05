-- Module notification (étape 5.2) : configuration des canaux, file d'envoi + journal, appareils push.
CREATE TABLE notif_canal (
    id         UUID                     NOT NULL,
    canal      TEXT                     NOT NULL,
    actif      BOOLEAN                  NOT NULL DEFAULT FALSE,
    config     TEXT                     NOT NULL DEFAULT '{}',
    -- JSON des secrets, chiffré AES-GCM (jamais renvoyé par l'API)
    secrets    TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version    BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT notif_canal_unique UNIQUE (canal),
    CONSTRAINT notif_canal_valide CHECK (canal IN ('SMS', 'WHATSAPP', 'EMAIL', 'PUSH'))
);

CREATE TABLE notif_message (
    id                   UUID                     NOT NULL,
    canal                TEXT                     NOT NULL,
    type_message         TEXT                     NOT NULL,
    destinataire         TEXT                     NOT NULL,
    sujet                TEXT,
    corps                TEXT                     NOT NULL,
    statut               TEXT                     NOT NULL,
    tentatives           INT                      NOT NULL DEFAULT 0,
    prochaine_tentative  TIMESTAMP WITH TIME ZONE NOT NULL,
    derniere_erreur      TEXT,
    envoye_le            TIMESTAMP WITH TIME ZONE,
    locataire_id         UUID,
    reference_id         UUID,
    cle_dedoublonnage    TEXT                     NOT NULL,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    version              BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT notif_message_dedoublonnage_unique UNIQUE (cle_dedoublonnage),
    CONSTRAINT notif_message_canal_valide CHECK (canal IN ('SMS', 'WHATSAPP', 'EMAIL', 'PUSH')),
    CONSTRAINT notif_message_statut_valide CHECK (statut IN ('EN_ATTENTE', 'ENVOYE', 'ECHEC'))
);
CREATE INDEX notif_message_file_idx ON notif_message (prochaine_tentative) WHERE statut = 'EN_ATTENTE';
CREATE INDEX notif_message_journal_idx ON notif_message (created_at DESC);

CREATE TABLE notif_appareil (
    id             UUID                     NOT NULL,
    utilisateur_id UUID                     NOT NULL,
    token          TEXT                     NOT NULL,
    plateforme     TEXT                     NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    version        BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT notif_appareil_token_unique UNIQUE (token),
    CONSTRAINT notif_appareil_plateforme_valide CHECK (plateforme IN ('ANDROID', 'IOS'))
);
CREATE INDEX notif_appareil_utilisateur_idx ON notif_appareil (utilisateur_id);
