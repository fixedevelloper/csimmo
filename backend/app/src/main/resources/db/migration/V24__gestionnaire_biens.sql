-- Gestionnaire de biens (docs/adr/0009-equipe-et-perimetres.md, étape 12.3) : rôle restreint à ses immeubles ; les locataires qu'il crée
-- lui restent visibles avant leur premier bail dans son périmètre.
ALTER TABLE utilisateur DROP CONSTRAINT utilisateur_role_valide;
ALTER TABLE utilisateur ADD CONSTRAINT utilisateur_role_valide CHECK (role IN
    ('ADMIN', 'GESTIONNAIRE', 'PROPRIETAIRE_TIERS', 'LOCATAIRE', 'PRESTATAIRE', 'ENCAISSEUR', 'GESTIONNAIRE_BIENS'));

ALTER TABLE locataire ADD COLUMN cree_par UUID;
