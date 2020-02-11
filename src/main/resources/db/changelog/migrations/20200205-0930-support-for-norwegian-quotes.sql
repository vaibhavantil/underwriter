--liquibase formatted.sql

--changeset fredrikareschoug:20200205-0930-support-for-norwegian-quotes.sql

CREATE TABLE quote_revision_norwegian_home_contents_data (
    internal_id serial PRIMARY KEY,
    id uuid NOT NULL,
    ssn varchar(12) NOT NULL,
    first_name varchar(100) NOT NULL,
    last_name varchar(100) NOT NULL,
    email varchar(255) NULL,
    street varchar (100) NOT NULL,
    city varchar(100) NULL,
    zip_code varchar(6) NOT NULL,
    coinsured smallint NOT NULL,
    living_space smallint NOT NULL,
    is_student boolean NOT NULL,
    type varchar(20) NOT NULL
);

CREATE TABLE quote_revision_norwegian_travel_data (
    internal_id serial PRIMARY KEY,
    id uuid NOT NULL,
    ssn varchar(12) NOT NULL,
    first_name varchar(100) NOT NULL,
    last_name varchar(100) NOT NULL,
    email varchar(255) NULL,
    coinsured smallint NOT NULL
);

ALTER TABLE quote_revisions
    ADD COLUMN quote_norwegian_home_contents_data_id integer NULL REFERENCES quote_revision_norwegian_home_contents_data(internal_id),
    ADD COLUMN quote_norwegian_travel_data_id integer NULL REFERENCES quote_revision_norwegian_travel_data(internal_id),
    DROP CONSTRAINT single_quote_type,
    ADD CONSTRAINT single_quote_type CHECK (
        (quote_apartment_data_id IS NOT NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NULL)
         OR
        (quote_apartment_data_id IS NULL AND quote_house_data_id IS NOT NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NULL)
         OR
        (quote_apartment_data_id IS NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NOT NULL AND quote_norwegian_travel_data_id IS NULL)
         OR
        (quote_apartment_data_id IS NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NOT NULL)
    )
;

--rollback DROP TABLE quote_norwegian_home_contents_data_id; DROP TABLE quote_norwegian_home_contents_data_id; ALTER TABLE quote_revisions DROP CONSTRAINT single_quote_type; ALTER TABLE ADD CONSTRAINT single_quote_type CHECK ( (quote_apartment_data_id IS NOT NULL AND quote_house_data_id IS NULL) OR (quote_apartment_data_id IS NULL AND quote_house_data_id IS NOT NULL))