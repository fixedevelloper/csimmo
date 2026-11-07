-- Équipe et périmètres d'accès (docs/adr/0009-equipe-et-perimetres.md) : rôle ENCAISSEUR et affectations d'immeubles.
ALTER TABLE utilisateur DROP CONSTRAINT utilisateur_role_valide;
ALTER TABLE utilisateur ADD CONSTRAINT utilisateur_role_valide CHECK (role IN
    ('ADMIN', 'GESTIONNAIRE', 'PROPRIETAIRE_TIERS', 'LOCATAIRE', 'PRESTATAIRE', 'ENCAISSEUR'));

-- Immeubles qu'un employé restreint peut voir. Pas de clé étrangère vers le patrimoine (frontière de module) :
-- un immeuble archivé ou inconnu reste sans effet. Un employé restreint sans ligne ici ne voit rien.
CREATE TABLE sec_affectation (
    utilisateur_id UUID                     NOT NULL REFERENCES utilisateur (id),
    immeuble_id    UUID                     NOT NULL,
    cree_par       UUID,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (utilisateur_id, immeuble_id)
);
CREATE INDEX sec_affectation_immeuble_idx ON sec_affectation (immeuble_id);
