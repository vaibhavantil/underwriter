--liquibase formatted.sql

--changeset AlexSJ1990:20200120-1342-add-contract-id-to-quote.sql

ALTER TABLE quote_revisions
    ADD COLUMN contract_id uuid NULL
;

--rollback ALTER TABLE quote_revisions DROP COLUMN contract_id;
