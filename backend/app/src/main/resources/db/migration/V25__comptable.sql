-- Comptable (docs/adr/0009-equipe-et-perimetres.md, étape 12.4) : lecture des paiements, dépenses et reversements de son périmètre, validation des reversements.
ALTER TABLE utilisateur DROP CONSTRAINT utilisateur_role_valide;
ALTER TABLE utilisateur ADD CONSTRAINT utilisateur_role_valide CHECK (role IN
    ('ADMIN', 'GESTIONNAIRE', 'PROPRIETAIRE_TIERS', 'LOCATAIRE', 'PRESTATAIRE', 'ENCAISSEUR', 'GESTIONNAIRE_BIENS', 'COMPTABLE'));
