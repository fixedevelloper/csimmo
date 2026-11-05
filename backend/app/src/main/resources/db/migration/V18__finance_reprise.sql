-- Reprise de données (étape 11.1) : les échéances d'avant la mise en service d'ImmoGest sont créées déjà réglées,
-- sauf le solde d'ouverture, sans fausser les encaissements ni les reversements (aucun paiement n'est créé).
ALTER TABLE fin_echeance ADD COLUMN reprise BOOLEAN NOT NULL DEFAULT FALSE;
