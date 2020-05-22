--liquibase formatted.sql

--changeset meletis:20200522-1100-add-new-field-to-track-iex-token-on-quote.sql

ALTER TABLE quote_revisions
    ADD COLUMN sign_from_hope_triggered_by varchar(255) NULL
;

--rollback ALTER TABLE quote_revisions DROP COLUMN sign_from_hope_triggered_by;