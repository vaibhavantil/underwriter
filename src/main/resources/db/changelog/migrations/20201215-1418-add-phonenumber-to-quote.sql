--liquibase formatted.sql

--changeset johantj:20201215-1418-add-phonenumber-to-quote.sql

ALTER TABLE quote_revision_apartment_data
    ADD COLUMN phone_number text;

ALTER TABLE quote_revision_danish_accident_data
    ADD COLUMN phone_number text;

ALTER TABLE quote_revision_danish_home_contents_data
    ADD COLUMN phone_number text;

ALTER TABLE quote_revision_house_data
    ADD COLUMN phone_number text;

ALTER TABLE quote_revision_norwegian_home_contents_data
    ADD COLUMN phone_number text;

ALTER TABLE quote_revision_norwegian_travel_data
    ADD COLUMN phone_number text;

--rollback ALTER TABLE quote_revision_apartment_data DROP COLUMN phone_number text;
--rollback ALTER TABLE quote_revision_danish_accident_data DROP COLUMN phone_number text;
--rollback ALTER TABLE quote_revision_danish_home_contents_data DROP COLUMN phone_number text;
--rollback ALTER TABLE quote_revision_house_data DROP COLUMN phone_number text;
--rollback ALTER TABLE quote_revision_norwegian_home_contents_data DROP COLUMN phone_number text;
--rollback ALTER TABLE quote_revision_norwegian_travel_data DROP COLUMN phone_number text;
