--liquibase formatted.sql

--changeset fredrikareschoug:20210226-1054-add-phone-number-to-quote-danish-travel.sql

ALTER TABLE quote_revision_danish_travel_data
    ADD COLUMN phone_number text;

--rollback ALTER TABLE quote_revision_danish_travel_data DROP COLUMN phone_number text;
