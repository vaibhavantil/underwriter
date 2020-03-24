--liquibase formatted.sql

--changeset elvingranat:20200324-1245-make-norwegian-ssn-nullable-and-11-characters.sql

ALTER TABLE quote_revision_norwegian_home_contents_data
    ALTER COLUMN ssn DROP NOT NULL,
    ALTER COLUMN ssn TYPE VARCHAR(11);

ALTER TABLE quote_revision_norwegian_travel_data
    ALTER COLUMN ssn DROP NOT NULL,
    ALTER COLUMN ssn TYPE VARCHAR(11);

--rollback ALTER TABLE quote_revision_norwegian_home_contents_data ALTER COLUMN ssn TYPE VARCHAR(12); ALTER TABLE quote_revision_norwegian_home_contents_data ALTER COLUMN ssn SET NOT NULL; ALTER TABLE quote_revision_norwegian_travel_data ALTER COLUMN ssn TYPE VARCHAR(12); ALTER TABLE quote_revision_norwegian_travel_data ALTER COLUMN ssn SET NOT NULL;