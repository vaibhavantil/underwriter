--liquibase formatted.sql

--changeset fredrikareschoug:20200225-1130-add-birth-date-to-quote-data.sql

ALTER TABLE quote_revision_house_data
    ADD COLUMN birth_date date NULL
;

ALTER TABLE quote_revision_apartment_data
    ADD COLUMN birth_date date NULL
;

ALTER TABLE quote_revision_norwegian_home_contents_data
    ADD COLUMN birth_date date NULL
;

ALTER TABLE quote_revision_norwegian_travel_data
    ADD COLUMN birth_date date NULL
;

--rollback ALTER TABLE quote_revision_house_data DROP COLUMN birth_date; ALTER TABLE quote_revision_apartment_data DROP COLUMN birth_date; ALTER TABLE quote_revision_norwegian_home_contents_data DROP COLUMN birth_date; ALTER TABLE quote_revision_norwegian_travel_data DROP COLUMN birth_date;