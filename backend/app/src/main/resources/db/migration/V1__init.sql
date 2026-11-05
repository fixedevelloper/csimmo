-- Registre de publication d'événements Spring Modulith (outbox)
CREATE TABLE event_publication (
    id               UUID                     NOT NULL,
    listener_id      TEXT                     NOT NULL,
    event_type       TEXT                     NOT NULL,
    serialized_event TEXT                     NOT NULL,
    publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date  TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);
CREATE INDEX event_publication_serialized_event_hash_idx ON event_publication USING hash (serialized_event);
CREATE INDEX event_publication_by_completion_date_idx ON event_publication (completion_date);

CREATE TABLE event_publication_archive (
    id               UUID                     NOT NULL,
    listener_id      TEXT                     NOT NULL,
    event_type       TEXT                     NOT NULL,
    serialized_event TEXT                     NOT NULL,
    publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date  TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);
CREATE INDEX event_publication_archive_serialized_event_hash_idx ON event_publication_archive USING hash (serialized_event);
CREATE INDEX event_publication_archive_by_completion_date_idx ON event_publication_archive (completion_date);

-- Journal d'audit : append-only, conservé 10 ans, jamais purgé automatiquement
CREATE TABLE audit_log (
    id         UUID                     NOT NULL,
    auteur     TEXT                     NOT NULL,
    action     TEXT                     NOT NULL,
    entite     TEXT                     NOT NULL,
    entite_id  UUID,
    avant      JSONB,
    apres      JSONB,
    date       TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX audit_log_entite_idx ON audit_log (entite, entite_id);
CREATE INDEX audit_log_date_idx ON audit_log (date);

-- Interdit toute modification ou suppression du journal (CLAUDE.md §6, règle 11)
CREATE FUNCTION audit_log_immuable() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'audit_log est en ajout seul';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER audit_log_pas_de_modification
    BEFORE UPDATE OR DELETE ON audit_log
    FOR EACH ROW EXECUTE FUNCTION audit_log_immuable();
