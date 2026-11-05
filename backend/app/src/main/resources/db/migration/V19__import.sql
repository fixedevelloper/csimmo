-- Journal des imports de données réussis (étape 11.1) : qui, quand, quel fichier (empreinte), combien d'objets. Écriture seule.
CREATE TABLE imp_journal (
    id           UUID PRIMARY KEY,
    effectue_le  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    par          TEXT NOT NULL,
    fichier_sha256 TEXT NOT NULL,
    comptes      TEXT NOT NULL
);
CREATE FUNCTION imp_interdire_modification() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'table imp_journal en écriture seule';
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER imp_journal_immuable BEFORE UPDATE OR DELETE ON imp_journal FOR EACH ROW EXECUTE FUNCTION imp_interdire_modification();
