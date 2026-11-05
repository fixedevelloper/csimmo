-- Module licence : état d'installation et licences importées (historique conservé, jamais supprimé)

-- Une seule ligne : identifiant d'installation (généré au premier démarrage) et assistant déjà passé ou non
CREATE TABLE lic_installation (
    id                       UUID                     NOT NULL,
    singleton                BOOLEAN                  NOT NULL DEFAULT TRUE,
    identifiant_installation TEXT                     NOT NULL,
    installee                BOOLEAN                  NOT NULL DEFAULT FALSE,
    installee_le             TIMESTAMP WITH TIME ZONE,
    created_at               TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITH TIME ZONE NOT NULL,
    version                  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT lic_installation_une_ligne UNIQUE (singleton),
    CONSTRAINT lic_installation_singleton CHECK (singleton)
);

CREATE TABLE lic_licence (
    id                             UUID                     NOT NULL,
    license_id                     TEXT                     NOT NULL,
    contenu                        TEXT                     NOT NULL,
    importee_le                    TIMESTAMP WITH TIME ZONE NOT NULL,
    actuelle                       BOOLEAN                  NOT NULL,
    -- départ du délai de grâce hors ligne (30 jours) : dernière confirmation du serveur de licences
    derniere_verification_en_ligne TIMESTAMP WITH TIME ZONE NOT NULL,
    derniere_tentative_en_ligne    TIMESTAMP WITH TIME ZONE,
    derniere_tentative_reussie     BOOLEAN,
    revoquee                       BOOLEAN                  NOT NULL DEFAULT FALSE,
    -- dernier seuil d'alerte notifié (30, 15 ou 7) : évite de renvoyer l'alerte chaque jour
    seuil_alerte_envoye            INT,
    expiration_notifiee            BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at                     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                     TIMESTAMP WITH TIME ZONE NOT NULL,
    version                        BIGINT,
    PRIMARY KEY (id)
);
-- au plus une licence courante
CREATE UNIQUE INDEX lic_licence_actuelle_unique ON lic_licence (actuelle) WHERE actuelle;
CREATE INDEX lic_licence_license_id_idx ON lic_licence (license_id);
