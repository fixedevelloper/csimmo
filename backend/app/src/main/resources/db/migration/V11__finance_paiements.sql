-- Étape 4.2 : paiements manuels, imputations signées, contre-passation. Aucune suppression ni modification (règle n°11).
ALTER TABLE fin_paiement
    ADD COLUMN mode                TEXT,
    ADD COLUMN reference           TEXT,
    ADD COLUMN saisi_par           UUID,
    ADD COLUMN cle_idempotence     TEXT,
    ADD COLUMN empreinte           TEXT,
    ADD COLUMN motif               TEXT,
    ADD COLUMN contre_passation_de UUID REFERENCES fin_paiement (id);

ALTER TABLE fin_paiement
    ADD CONSTRAINT fin_paiement_type_valide CHECK (type IN ('AVANCE', 'ENCAISSEMENT', 'CONTRE_PASSATION')),
    ADD CONSTRAINT fin_paiement_mode_valide CHECK (mode IS NULL OR mode IN ('ESPECES', 'VIREMENT', 'MTN_MOMO', 'ORANGE_MONEY')),
    ADD CONSTRAINT fin_paiement_encaissement_mode CHECK (type <> 'ENCAISSEMENT' OR mode IS NOT NULL),
    ADD CONSTRAINT fin_paiement_contre_passation_complete
        CHECK (type <> 'CONTRE_PASSATION' OR (contre_passation_de IS NOT NULL AND motif IS NOT NULL AND length(btrim(motif)) > 0));

-- rejeu d'une requête avec la même clé = même résultat
CREATE UNIQUE INDEX fin_paiement_cle_unique ON fin_paiement (cle_idempotence) WHERE cle_idempotence IS NOT NULL;
-- un paiement ne peut être contre-passé qu'une fois
CREATE UNIQUE INDEX fin_paiement_une_contre_passation ON fin_paiement (contre_passation_de) WHERE contre_passation_de IS NOT NULL;
CREATE INDEX fin_paiement_mode_date_idx ON fin_paiement (mode, date_paiement);

-- l'écriture inverse d'une imputation est une imputation négative
ALTER TABLE fin_imputation DROP CONSTRAINT fin_imputation_montant_positif;
ALTER TABLE fin_imputation ADD CONSTRAINT fin_imputation_montant_non_nul CHECK (montant <> 0);

CREATE FUNCTION fin_interdire_modification() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'Paiements et imputations sont immuables : utiliser une contre-passation';
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER fin_paiement_immuable BEFORE UPDATE OR DELETE ON fin_paiement
    FOR EACH ROW EXECUTE FUNCTION fin_interdire_modification();
CREATE TRIGGER fin_imputation_immuable BEFORE UPDATE OR DELETE ON fin_imputation
    FOR EACH ROW EXECUTE FUNCTION fin_interdire_modification();
