-- Conformité (loi camerounaise n°2024/017 sur la protection des données personnelles) : consentements et demandes d'anonymisation.
-- Aucune donnée personnelle ici : seulement le type de personne et son identifiant.

-- Registre des consentements : chaque ligne est une décision (accordé ou refusé/retiré) ; la plus récente par finalité fait foi. Ajout seul.
CREATE TABLE conf_consentement (
    id                 UUID PRIMARY KEY,
    personne_type      TEXT NOT NULL CHECK (personne_type IN ('LOCATAIRE', 'GARANT', 'PROPRIETAIRE', 'PRESTATAIRE')),
    personne_id        UUID NOT NULL,
    finalite           TEXT NOT NULL CHECK (finalite IN ('NOTIFICATIONS', 'PIECES_IDENTITE', 'TRANSMISSION_PROPRIETAIRE', 'APPLICATION_MOBILE')),
    accorde            BOOLEAN NOT NULL,
    version_modele     TEXT NOT NULL,
    mode_recueil       TEXT NOT NULL CHECK (mode_recueil IN ('PAPIER', 'ELECTRONIQUE', 'ORAL')),
    reference_document TEXT,
    recueilli_par      TEXT NOT NULL,
    decide_le          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT clock_timestamp()
);
CREATE INDEX conf_consentement_personne ON conf_consentement (personne_type, personne_id, finalite, decide_le DESC);

CREATE FUNCTION conf_consentement_ajout_seul() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'conf_consentement est en ajout seul';
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER conf_consentement_immuable BEFORE UPDATE OR DELETE ON conf_consentement FOR EACH ROW EXECUTE FUNCTION conf_consentement_ajout_seul();

-- Demandes d'anonymisation (droit d'effacement compatible avec « aucune suppression » des écritures financières).
CREATE TABLE conf_demande (
    id            UUID PRIMARY KEY,
    personne_type TEXT NOT NULL CHECK (personne_type IN ('LOCATAIRE', 'GARANT', 'PROPRIETAIRE', 'PRESTATAIRE')),
    personne_id   UUID NOT NULL,
    statut        TEXT NOT NULL CHECK (statut IN ('RECUE', 'EXECUTEE', 'REFUSEE')),
    motif         TEXT,
    motif_refus   TEXT,
    recue_le      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    recue_par     TEXT NOT NULL,
    traitee_le    TIMESTAMP WITH TIME ZONE,
    traitee_par   TEXT,
    CHECK ((statut = 'RECUE') = (traitee_le IS NULL))
);
-- une seule demande ouverte par personne
CREATE UNIQUE INDEX conf_demande_une_ouverte ON conf_demande (personne_type, personne_id) WHERE statut = 'RECUE';
