-- Serveur de licences de l'éditeur : base propre, distincte de celle des installations clientes.

CREATE TABLE ls_editeur (
    id                 UUID PRIMARY KEY,
    email              TEXT NOT NULL UNIQUE,
    nom                TEXT NOT NULL,
    mot_de_passe_hash  TEXT NOT NULL,
    echecs             INT NOT NULL DEFAULT 0,
    verrouille_jusqu   TIMESTAMPTZ,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE ls_client (
    id          UUID PRIMARY KEY,
    nom         TEXT NOT NULL,
    email       TEXT,
    telephone   TEXT,
    pays        TEXT,
    notes       TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Une licence = un identifiant stable (licenseId) ; chaque fichier signé émis est une ligne de ls_emission.
CREATE TABLE ls_licence (
    id                      UUID PRIMARY KEY,
    client_id               UUID NOT NULL REFERENCES ls_client(id),
    statut                  TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (statut IN ('ACTIVE', 'REVOQUEE')),
    motif_revocation        TEXT,
    revoquee_le             TIMESTAMPTZ,
    transferts              INT NOT NULL DEFAULT 0,
    derniere_emission_livree UUID,
    derniere_verification   TIMESTAMPTZ,
    version_application     TEXT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK ((statut = 'REVOQUEE') = (revoquee_le IS NOT NULL))
);
CREATE INDEX ls_licence_client ON ls_licence(client_id);

-- Historique, jamais modifié ni supprimé : ce qui a été signé et remis au client.
CREATE TABLE ls_emission (
    id                 UUID PRIMARY KEY,
    licence_id         UUID NOT NULL REFERENCES ls_licence(id),
    motif              TEXT NOT NULL CHECK (motif IN ('EMISSION', 'RENOUVELLEMENT', 'TRANSFERT', 'MODIFICATION')),
    palier_lots        INT NOT NULL CHECK (palier_lots > 0),
    date_debut         DATE NOT NULL,
    date_expiration    DATE NOT NULL,
    empreinte_serveur  TEXT NOT NULL,
    fonctionnalites    TEXT NOT NULL,
    contenu            TEXT NOT NULL,
    emise_par          TEXT NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (date_expiration >= date_debut)
);
CREATE INDEX ls_emission_licence ON ls_emission(licence_id, created_at);

CREATE TABLE ls_paiement (
    id             UUID PRIMARY KEY,
    licence_id     UUID NOT NULL REFERENCES ls_licence(id),
    montant        BIGINT NOT NULL CHECK (montant > 0),
    mode           TEXT NOT NULL,
    reference      TEXT,
    date_paiement  DATE NOT NULL,
    note           TEXT,
    enregistre_par TEXT NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX ls_paiement_licence ON ls_paiement(licence_id);

CREATE TABLE ls_version (
    id                  UUID PRIMARY KEY,
    numero              TEXT NOT NULL UNIQUE,
    notes               TEXT,
    url_telechargement  TEXT NOT NULL,
    sha256              TEXT NOT NULL CHECK (sha256 ~ '^[0-9a-f]{64}$'),
    publiee_par         TEXT NOT NULL,
    publiee_le          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Émissions et paiements : écriture seule.
CREATE FUNCTION ls_interdire_modification() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'table % en écriture seule', TG_TABLE_NAME;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER ls_emission_immuable BEFORE UPDATE OR DELETE ON ls_emission FOR EACH ROW EXECUTE FUNCTION ls_interdire_modification();
CREATE TRIGGER ls_paiement_immuable BEFORE UPDATE OR DELETE ON ls_paiement FOR EACH ROW EXECUTE FUNCTION ls_interdire_modification();
