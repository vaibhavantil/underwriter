--liquibase formatted.sql

--changeset palmenhq:20191029-1639-add-required-house-data.sql

ALTER TABLE quote_revision_house_data
    ADD COLUMN ancillary_area smallint NULL,
    ADD COLUMN year_of_construction smallint NULL,
    ADD COLUMN number_of_bathrooms smallint NULL,
    ADD COLUMN extra_buildings jsonb NOT NULL, -- should rather be empty array than null
    ADD COLUMN is_subleted boolean NULL,
    ADD COLUMN floor smallint NULL
;

COMMENT ON COLUMN quote_revision_house_data.extra_buildings IS 'should rather be empty array than null';
