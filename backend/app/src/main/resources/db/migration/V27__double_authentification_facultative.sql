-- La double authentification est facultative et désactivée par défaut (ADR 0010). Les comptes qui l'avaient « activée » à l'époque du code SMS,
-- ou imposée à leur rôle, mais qui n'ont jamais configuré d'application d'authentification, la perdent : elle ne protégeait plus rien (le SMS n'existe plus)
-- et les enverrait tous en enrôlement forcé. Les comptes qui ont une application configurée la gardent.
UPDATE utilisateur SET deux_facteurs_actif = FALSE WHERE deux_facteurs_actif AND totp_secret_chiffre IS NULL;
