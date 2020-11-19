--liquibase formatted.sql

--changeset meletis:20201117-1030-add-danish-accident-and-travel-table.sql

CREATE TABLE quote_revision_danish_accident_data
(
    internal_id serial PRIMARY KEY,
    id          uuid NOT NULL
);

CREATE TABLE quote_revision_danish_travel_data
(
    internal_id serial PRIMARY KEY,
    id          uuid NOT NULL
);

ALTER TABLE quote_revisions
    ADD COLUMN quote_danish_accident_data_id integer NULL REFERENCES quote_revision_danish_accident_data (internal_id),
    ADD COLUMN quote_danish_travel_data_id   integer NULL REFERENCES quote_revision_danish_travel_data (internal_id),
    DROP CONSTRAINT single_quote_type,
    ADD CONSTRAINT single_quote_type CHECK (
            (quote_apartment_data_id IS NOT NULL
                AND quote_house_data_id IS NULL
                AND quote_norwegian_home_contents_data_id IS NULL
                AND quote_norwegian_travel_data_id IS NULL
                AND quote_danish_home_contents_data_id IS NULL
                AND quote_danish_accident_data_id IS NULL
                AND quote_danish_travel_data_id IS NULL
                )
            OR
            (quote_apartment_data_id IS NULL
                AND quote_house_data_id IS NOT NULL
                AND quote_norwegian_home_contents_data_id IS NULL
                AND quote_norwegian_travel_data_id IS NULL
                AND quote_danish_home_contents_data_id IS NULL
                AND quote_danish_accident_data_id IS NULL
                AND quote_danish_travel_data_id IS NULL
                )
            OR
            (quote_apartment_data_id IS NULL
                AND quote_house_data_id IS NULL
                AND quote_norwegian_home_contents_data_id IS NOT NULL
                AND quote_norwegian_travel_data_id IS NULL
                AND quote_danish_home_contents_data_id IS NULL
                AND quote_danish_accident_data_id IS NULL
                AND quote_danish_travel_data_id IS NULL
                )
            OR
            (quote_apartment_data_id IS NULL
                AND quote_house_data_id IS NULL
                AND quote_norwegian_home_contents_data_id IS NULL
                AND quote_norwegian_travel_data_id IS NOT NULL
                AND quote_danish_home_contents_data_id IS NULL
                AND quote_danish_accident_data_id IS NULL
                AND quote_danish_travel_data_id IS NULL
                )
            OR
            (quote_apartment_data_id IS NULL
                AND quote_house_data_id IS NULL
                AND quote_norwegian_home_contents_data_id IS NULL
                AND quote_norwegian_travel_data_id IS NULL
                AND quote_danish_home_contents_data_id IS NOT NULL
                AND quote_danish_accident_data_id IS NULL
                AND quote_danish_travel_data_id IS NULL
                )
            OR
            (quote_apartment_data_id IS NULL
                AND quote_house_data_id IS NULL
                AND quote_norwegian_home_contents_data_id IS NULL
                AND quote_norwegian_travel_data_id IS NULL
                AND quote_danish_home_contents_data_id IS NULL
                AND quote_danish_accident_data_id IS NOT NULL
                AND quote_danish_travel_data_id IS NULL
                )
            OR
            (quote_apartment_data_id IS NULL
                AND quote_house_data_id IS NULL
                AND quote_norwegian_home_contents_data_id IS NULL
                AND quote_norwegian_travel_data_id IS NULL
                AND quote_danish_home_contents_data_id IS NULL
                AND quote_danish_accident_data_id IS NULL
                AND quote_danish_travel_data_id IS NOT NULL
                )
        );

ALTER TABLE quote_revision_danish_accident_data
    ADD COLUMN ssn        varchar(10)  NULL,
    ADD COLUMN birth_date date         NOT NULL,
    ADD COLUMN first_name varchar(100) NOT NULL,
    ADD COLUMN last_name  varchar(100) NOT NULL,
    ADD COLUMN email      varchar(255) NULL,
    ADD COLUMN street     varchar(100) NOT NULL,
    ADD COLUMN zip_code   varchar(4)   NOT NULL,
    ADD COLUMN co_insured smallint     NOT NULL,
    ADD COLUMN is_student boolean      NOT NULL DEFAULT false
;

ALTER TABLE quote_revision_danish_travel_data
    ADD COLUMN ssn        varchar(10)  NULL,
    ADD COLUMN birth_date date         NOT NULL,
    ADD COLUMN first_name varchar(100) NOT NULL,
    ADD COLUMN last_name  varchar(100) NOT NULL,
    ADD COLUMN email      varchar(255) NULL,
    ADD COLUMN street     varchar(100) NOT NULL,
    ADD COLUMN zip_code   varchar(4)   NOT NULL,
    ADD COLUMN co_insured smallint     NOT NULL,
    ADD COLUMN is_student boolean      NOT NULL DEFAULT false
;

--rollback DROP TABLE quote_revision_danish_accident_data; DROP TABLE quote_revision_danish_travel_data; ALTER TABLE quote_revisions DROP CONSTRAINT single_quote_type; ALTER TABLE ADD CONSTRAINT single_quote_type CHECK (         (quote_apartment_data_id IS NOT NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NULL AND quote_danish_home_contents_data_id IS NULL) OR (quote_apartment_data_id IS NULL AND quote_house_data_id IS NOT NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NULL AND quote_danish_home_contents_data_id IS NULL) OR (quote_apartment_data_id IS NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NOT NULL AND quote_norwegian_travel_data_id IS NULL AND quote_danish_home_contents_data_id IS NULL) OR (quote_apartment_data_id IS NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NOT NULL AND quote_danish_home_contents_data_id IS NULL) OR (quote_apartment_data_id IS NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NULL AND quote_danish_home_contents_data_id IS NOT NULL))