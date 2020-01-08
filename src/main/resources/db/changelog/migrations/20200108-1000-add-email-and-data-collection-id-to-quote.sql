--liquibase formatted.sql

--changeset fredrikareschoug:20200108-1000-add-email-and-data-collection-id-to-quote.sql

ALTER TABLE quote_revision_house_data
    ADD COLUMN email varchar(100) NULL
;

ALTER TABLE quote_revision_apartment_data
    ADD COLUMN email varchar(100) NULL
;

ALTER TABLE quote_revisions
    ADD COLUMN data_collection_id uuid NULL
;

--rollback ALTER TABLE quote_revision_house_data DROP COLUMN email; ALTER TABLE quote_revision_apartment_data DROP COLUMN email; ALTER TABLE quote_revisions DROP COLUMN data_collection_id;
