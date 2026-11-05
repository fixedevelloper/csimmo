-- Étape 5.1 : quittances (échéance entièrement payée) et reçus (paiement partiel), numérotation continue par année.
-- Un document n'est jamais supprimé : s'il devient caduc (contre-passation), il passe ANNULE et son PDF est conservé.
CREATE TABLE fin_compteur_numero (
    type    TEXT   NOT NULL,
    annee   INT    NOT NULL,
    dernier BIGINT NOT NULL,
    PRIMARY KEY (type, annee)
);

CREATE TABLE fin_document (
    id                 UUID                     NOT NULL,
    type               TEXT                     NOT NULL,
    numero             TEXT                     NOT NULL,
    bail_id            UUID                     NOT NULL,
    echeance_id        UUID REFERENCES fin_echeance (id),
    paiement_id        UUID REFERENCES fin_paiement (id),
    fichier_id         UUID                     NOT NULL,
    montant            BIGINT                   NOT NULL,
    statut             TEXT                     NOT NULL,
    date_emission      DATE                     NOT NULL,
    motif_annulation   TEXT,
    annule_le          TIMESTAMP WITH TIME ZONE,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    version            BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fin_document_numero_unique UNIQUE (numero),
    CONSTRAINT fin_document_fichier_unique UNIQUE (fichier_id),
    CONSTRAINT fin_document_type_valide CHECK (type IN ('QUITTANCE', 'RECU')),
    CONSTRAINT fin_document_statut_valide CHECK (statut IN ('EMIS', 'ANNULE')),
    CONSTRAINT fin_document_cible CHECK (
        (type = 'QUITTANCE' AND echeance_id IS NOT NULL) OR (type = 'RECU' AND paiement_id IS NOT NULL)),
    CONSTRAINT fin_document_annulation CHECK (statut <> 'ANNULE' OR (motif_annulation IS NOT NULL AND annule_le IS NOT NULL))
);
-- idempotence : une seule quittance en vigueur par échéance, un seul reçu par paiement
CREATE UNIQUE INDEX fin_document_une_quittance ON fin_document (echeance_id) WHERE type = 'QUITTANCE' AND statut = 'EMIS';
CREATE UNIQUE INDEX fin_document_un_recu ON fin_document (paiement_id) WHERE type = 'RECU';
CREATE INDEX fin_document_bail_idx ON fin_document (bail_id);
