-- Étape 7.1 : signalements de pannes par les locataires (socle du module maintenance V2).
-- bail_id, lot_id, locataire_id, fichier_id : identifiants d'autres modules, sans clé étrangère.
CREATE TABLE mnt_signalement (
    id            UUID                     NOT NULL,
    bail_id       UUID                     NOT NULL,
    lot_id        UUID                     NOT NULL,
    locataire_id  UUID                     NOT NULL,
    description   TEXT                     NOT NULL,
    statut        TEXT                     NOT NULL DEFAULT 'NOUVEAU',
    fichier_id    UUID,
    note_gestion  TEXT,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    version       BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT mnt_signalement_statut_valide CHECK (statut IN ('NOUVEAU', 'EN_COURS', 'RESOLU', 'REJETE')),
    CONSTRAINT mnt_signalement_description_valide CHECK (length(btrim(description)) BETWEEN 5 AND 2000)
);
CREATE INDEX mnt_signalement_locataire_idx ON mnt_signalement (locataire_id, created_at DESC);
CREATE INDEX mnt_signalement_statut_idx ON mnt_signalement (statut, created_at DESC);
