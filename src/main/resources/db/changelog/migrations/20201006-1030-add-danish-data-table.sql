--liquibase formatted.sql

--changeset fredrikareschoug:20201006-1030-add-danish-data-table.sql

CREATE TABLE quote_revision_danish_home_contents_data (
    internal_id serial PRIMARY KEY,
    id uuid NOT NULL
);

ALTER TABLE quote_revisions
    ADD COLUMN quote_danish_home_contents_data_id integer NULL REFERENCES quote_revision_danish_home_contents_data(internal_id),
    DROP CONSTRAINT single_quote_type,
    ADD CONSTRAINT single_quote_type CHECK (
        (quote_apartment_data_id IS NOT NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NULL AND quote_danish_home_contents_data_id IS NULL)
         OR
        (quote_apartment_data_id IS NULL AND quote_house_data_id IS NOT NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NULL AND quote_danish_home_contents_data_id IS NULL)
         OR
        (quote_apartment_data_id IS NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NOT NULL AND quote_norwegian_travel_data_id IS NULL AND quote_danish_home_contents_data_id IS NULL)
         OR
        (quote_apartment_data_id IS NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NOT NULL AND quote_danish_home_contents_data_id IS NULL)
        OR
        (quote_apartment_data_id IS NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NULL AND quote_danish_home_contents_data_id IS NOT NULL)
    )
;

--rollback DROP TABLE quote_revision_danish_home_contents_data; ALTER TABLE quote_revisions DROP CONSTRAINT single_quote_type; ALTER TABLE ADD CONSTRAINT single_quote_type CHECK ((quote_apartment_data_id IS NOT NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NULL) OR (quote_apartment_data_id IS NULL AND quote_house_data_id IS NOT NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NULL) OR (quote_apartment_data_id IS NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NOT NULL AND quote_norwegian_travel_data_id IS NULL) OR (quote_apartment_data_id IS NULL AND quote_house_data_id IS NULL AND quote_norwegian_home_contents_data_id IS NULL AND quote_norwegian_travel_data_id IS NOT NULL) )