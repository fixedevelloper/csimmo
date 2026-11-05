-- Rattachement Immeuble -> propriétaire tiers (facultatif). L'identifiant seul est stocké : jamais de clé étrangère
-- vers une table d'un autre module (CLAUDE.md §5.1).
ALTER TABLE immeuble ADD COLUMN proprietaire_tiers_id UUID;
CREATE INDEX immeuble_proprietaire_idx ON immeuble (proprietaire_tiers_id) WHERE proprietaire_tiers_id IS NOT NULL;
