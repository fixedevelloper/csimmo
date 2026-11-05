-- Module securite : utilisateurs, jetons de rafraîchissement, codes OTP
CREATE TABLE utilisateur (
    id                  UUID                     NOT NULL,
    nom                 TEXT                     NOT NULL,
    telephone           TEXT,
    email               TEXT,
    role                TEXT                     NOT NULL,
    mot_de_passe_hash   TEXT                     NOT NULL,
    actif               BOOLEAN                  NOT NULL DEFAULT TRUE,
    deux_facteurs_actif BOOLEAN                  NOT NULL DEFAULT FALSE,
    tentatives_echouees INT                      NOT NULL DEFAULT 0,
    verrouille_jusqua   TIMESTAMP WITH TIME ZONE,
    derniere_connexion  TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    version             BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT utilisateur_telephone_unique UNIQUE (telephone),
    CONSTRAINT utilisateur_email_unique UNIQUE (email),
    CONSTRAINT utilisateur_contact_requis CHECK (telephone IS NOT NULL OR email IS NOT NULL),
    CONSTRAINT utilisateur_role_valide CHECK (role IN
        ('ADMIN', 'GESTIONNAIRE', 'PROPRIETAIRE_TIERS', 'LOCATAIRE', 'PRESTATAIRE'))
);

CREATE TABLE sec_refresh_token (
    id             UUID                     NOT NULL,
    utilisateur_id UUID                     NOT NULL REFERENCES utilisateur (id),
    token_hash     TEXT                     NOT NULL,
    expire_le      TIMESTAMP WITH TIME ZONE NOT NULL,
    revoque_le     TIMESTAMP WITH TIME ZONE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    version        BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT sec_refresh_token_hash_unique UNIQUE (token_hash)
);
CREATE INDEX sec_refresh_token_utilisateur_idx ON sec_refresh_token (utilisateur_id);

CREATE TABLE sec_otp_code (
    id             UUID                     NOT NULL,
    utilisateur_id UUID                     NOT NULL REFERENCES utilisateur (id),
    code_hash      TEXT                     NOT NULL,
    expire_le      TIMESTAMP WITH TIME ZONE NOT NULL,
    tentatives     INT                      NOT NULL DEFAULT 0,
    utilise        BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    version        BIGINT,
    PRIMARY KEY (id)
);
CREATE INDEX sec_otp_code_utilisateur_idx ON sec_otp_code (utilisateur_id, created_at);
