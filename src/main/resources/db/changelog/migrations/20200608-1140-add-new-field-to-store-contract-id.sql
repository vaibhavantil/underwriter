--liquibase formatted.sql

--changeset johantj:20200608-1140-add-new-field-to-store-contract-id.sql

ALTER TABLE quote_revisions
    ADD COLUMN contract_id uuid NULL
;

--rollback ALTER TABLE quote_revisions DROP COLUMN contract_id;
