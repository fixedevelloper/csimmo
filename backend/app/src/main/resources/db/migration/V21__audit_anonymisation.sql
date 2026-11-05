-- Le journal d'audit reste en ajout seul (CLAUDE.md §6, règle 11), avec UNE exception contrôlée : l'anonymisation d'une personne (étape 11.2)
-- peut remplacer, dans les colonnes avant/apres seulement, ses données personnelles. Elle doit déclarer l'opération dans la transaction
-- (SET LOCAL immogest.anonymisation = 'on') ; ni l'auteur, ni l'action, ni la date, ni l'entité ne peuvent changer ; aucune suppression.
CREATE OR REPLACE FUNCTION audit_log_immuable() RETURNS trigger AS $$
BEGIN
    IF TG_OP = 'UPDATE'
       AND current_setting('immogest.anonymisation', true) = 'on'
       AND NEW.id = OLD.id AND NEW.auteur = OLD.auteur AND NEW.action = OLD.action AND NEW.entite = OLD.entite
       AND NEW.entite_id IS NOT DISTINCT FROM OLD.entite_id AND NEW.date = OLD.date THEN
        RETURN NEW;
    END IF;
    RAISE EXCEPTION 'audit_log est en ajout seul';
END;
$$ LANGUAGE plpgsql;
