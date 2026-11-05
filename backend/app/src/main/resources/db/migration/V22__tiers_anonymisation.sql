-- Anonymisation des personnes (étape 11.2) : la fiche est conservée (les baux, quittances et écritures y renvoient) mais ses données personnelles
-- sont effacées. Les contraintes « un contact est obligatoire » ne s'appliquent plus à une fiche anonymisée.
ALTER TABLE locataire ADD COLUMN anonymise_le TIMESTAMP WITH TIME ZONE;
ALTER TABLE garant ADD COLUMN anonymise_le TIMESTAMP WITH TIME ZONE;
ALTER TABLE proprietaire_tiers ADD COLUMN anonymise_le TIMESTAMP WITH TIME ZONE;
ALTER TABLE prestataire ADD COLUMN anonymise_le TIMESTAMP WITH TIME ZONE;

ALTER TABLE locataire DROP CONSTRAINT locataire_contact;
ALTER TABLE locataire ADD CONSTRAINT locataire_contact CHECK (anonymise_le IS NOT NULL OR telephone IS NOT NULL OR email IS NOT NULL);
ALTER TABLE proprietaire_tiers DROP CONSTRAINT proprietaire_tiers_contact;
ALTER TABLE proprietaire_tiers ADD CONSTRAINT proprietaire_tiers_contact CHECK (anonymise_le IS NOT NULL OR telephone IS NOT NULL OR email IS NOT NULL);
ALTER TABLE garant ALTER COLUMN telephone DROP NOT NULL;
ALTER TABLE prestataire DROP CONSTRAINT prestataire_contact;
ALTER TABLE prestataire ADD CONSTRAINT prestataire_contact CHECK (anonymise_le IS NOT NULL OR telephone IS NOT NULL OR email IS NOT NULL);
