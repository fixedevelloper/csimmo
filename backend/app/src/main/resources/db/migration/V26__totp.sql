-- Double authentification par application (TOTP, RFC 6238 : Google Authenticator, Microsoft Authenticator, Authy…) à la place du code SMS.
-- Le secret est chiffré par l'application (AES-GCM, clé de l'installation) : jamais en clair en base, jamais renvoyé par l'API.
-- totp_dernier_pas : dernier pas de 30 s accepté, pour qu'un code ne serve qu'une fois.
-- Un compte dont deux_facteurs_actif est vrai mais sans secret (ancienne double authentification par SMS, ou employé restreint nouvellement créé)
-- configure son application à la prochaine connexion, avant d'obtenir une session.
ALTER TABLE utilisateur ADD COLUMN totp_secret_chiffre TEXT;
ALTER TABLE utilisateur ADD COLUMN totp_dernier_pas BIGINT NOT NULL DEFAULT 0;

-- Défi de double authentification : jeton opaque (seul son hash est stocké) délivré après un bon mot de passe, valable quelques minutes.
--   VERIFICATION : le compte a une application configurée, il doit fournir un code.
--   ENROLEMENT   : le compte doit configurer son application (secret en attente, confirmé par un premier code) avant d'avoir une session.
--   PROFIL       : activation demandée depuis le profil d'un utilisateur déjà connecté (secret en attente).
CREATE TABLE sec_defi_2fa (
    id                        UUID                     NOT NULL,
    utilisateur_id            UUID                     NOT NULL REFERENCES utilisateur (id),
    type                      TEXT                     NOT NULL,
    token_hash                TEXT                     NOT NULL,
    expire_le                 TIMESTAMP WITH TIME ZONE NOT NULL,
    tentatives                INT                      NOT NULL DEFAULT 0,
    utilise                   BOOLEAN                  NOT NULL DEFAULT FALSE,
    secret_en_attente_chiffre TEXT,
    created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    version                   BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT sec_defi_2fa_hash_unique UNIQUE (token_hash),
    CONSTRAINT sec_defi_2fa_type_valide CHECK (type IN ('VERIFICATION', 'ENROLEMENT', 'PROFIL'))
);
CREATE INDEX sec_defi_2fa_utilisateur_idx ON sec_defi_2fa (utilisateur_id, type, created_at DESC);
