-- Étape 6.3 : dépenses imputées au propriétaire et reversements mensuels (module finance).
-- immeuble_id, lot_id, proprietaire_id : identifiants d'autres modules, sans clé étrangère.
CREATE TABLE fin_depense (
    id               UUID                     NOT NULL,
    immeuble_id      UUID                     NOT NULL,
    lot_id           UUID,
    proprietaire_id  UUID                     NOT NULL,
    date_depense     DATE                     NOT NULL,
    nature           TEXT                     NOT NULL,
    montant          BIGINT                   NOT NULL,
    reference        TEXT,
    statut           TEXT                     NOT NULL DEFAULT 'ENREGISTREE',
    motif_annulation TEXT,
    saisi_par        UUID,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    version          BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fin_depense_montant_positif CHECK (montant > 0),
    CONSTRAINT fin_depense_statut_valide CHECK (statut IN ('ENREGISTREE', 'ANNULEE')),
    CONSTRAINT fin_depense_annulation CHECK (statut <> 'ANNULEE' OR (motif_annulation IS NOT NULL AND length(btrim(motif_annulation)) > 0))
);
CREATE INDEX fin_depense_proprietaire_date_idx ON fin_depense (proprietaire_id, date_depense);

CREATE TABLE fin_reversement (
    id                   UUID                     NOT NULL,
    proprietaire_id      UUID                     NOT NULL,
    periode              DATE                     NOT NULL,
    statut               TEXT                     NOT NULL,
    numero               TEXT,
    loyers_encaisses     BIGINT                   NOT NULL,
    charges_encaissees   BIGINT                   NOT NULL,
    brut                 BIGINT                   NOT NULL,
    honoraires           BIGINT                   NOT NULL,
    taux_moyen_bp        INT,
    depenses             BIGINT                   NOT NULL,
    net                  BIGINT                   NOT NULL,
    sans_mandat          BOOLEAN                  NOT NULL DEFAULT FALSE,
    fichier_id           UUID,
    calcule_le           TIMESTAMP WITH TIME ZONE NOT NULL,
    valide_le            TIMESTAMP WITH TIME ZONE,
    valide_par           UUID,
    paye_le              DATE,
    mode_paiement        TEXT,
    reference_paiement   TEXT,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    version              BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fin_reversement_periode_unique UNIQUE (proprietaire_id, periode),
    CONSTRAINT fin_reversement_numero_unique UNIQUE (numero),
    CONSTRAINT fin_reversement_statut_valide CHECK (statut IN ('CALCULE', 'VALIDE', 'PAYE')),
    CONSTRAINT fin_reversement_premier_du_mois CHECK (EXTRACT(DAY FROM periode) = 1),
    CONSTRAINT fin_reversement_coherent CHECK (brut = loyers_encaisses + charges_encaissees AND net = brut - honoraires - depenses),
    -- un reversement validé est figé : numéro et relevé PDF obligatoires
    CONSTRAINT fin_reversement_valide_complet CHECK (statut = 'CALCULE' OR (numero IS NOT NULL AND fichier_id IS NOT NULL AND valide_le IS NOT NULL)),
    CONSTRAINT fin_reversement_paye_complet CHECK (statut <> 'PAYE' OR (paye_le IS NOT NULL AND mode_paiement IS NOT NULL))
);

-- Un reversement VALIDE ou PAYE ne change plus (sauf le passage VALIDE → PAYE) : corrections par les mois suivants.
CREATE FUNCTION fin_reversement_fige() RETURNS trigger AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        RAISE EXCEPTION 'Un reversement ne se supprime pas';
    END IF;
    IF OLD.statut IN ('VALIDE', 'PAYE') AND (
        NEW.proprietaire_id <> OLD.proprietaire_id OR NEW.periode <> OLD.periode OR NEW.numero IS DISTINCT FROM OLD.numero
        OR NEW.loyers_encaisses <> OLD.loyers_encaisses OR NEW.charges_encaissees <> OLD.charges_encaissees OR NEW.honoraires <> OLD.honoraires
        OR NEW.depenses <> OLD.depenses OR NEW.net <> OLD.net OR NEW.fichier_id IS DISTINCT FROM OLD.fichier_id
        OR (OLD.statut = 'PAYE' AND NEW.statut <> 'PAYE') OR (OLD.statut = 'VALIDE' AND NEW.statut = 'CALCULE')) THEN
        RAISE EXCEPTION 'Un reversement validé est figé';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER fin_reversement_fige_trg BEFORE UPDATE OR DELETE ON fin_reversement
    FOR EACH ROW EXECUTE FUNCTION fin_reversement_fige();
