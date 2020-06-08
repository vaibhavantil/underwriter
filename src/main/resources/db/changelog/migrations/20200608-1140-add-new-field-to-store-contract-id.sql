--liquibase formatted.sql

--changeset johantj:20200608-1140-add-new-field-to-store-contract-id.sql

ALTER TABLE quote_revisions
    ADD COLUMN contract_id uuid NULL
;

ALTER TABLE quote_revisions
    RENAME COLUMN signed_product_id TO agreement_id
;

--rollback ALTER TABLE quote_revisions DROP COLUMN contract_id; ALTER TABLE quote_revisions RENAME COLUMN agreement_id TO signed_product_id;
