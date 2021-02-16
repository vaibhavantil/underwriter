--liquibase formatted.sql

--changeset ostenforshed:20210215-1200-make-norwegian-first-and-last-name-nullable.sql

ALTER TABLE quote_revision_norwegian_travel_data ALTER COLUMN first_name DROP NOT NULL;
ALTER TABLE quote_revision_norwegian_travel_data ALTER COLUMN last_name DROP NOT NULL;

--rollback ALTER TABLE quote_revision_norwegian_travel_data ALTER COLUMN first_name SET NOT NULL;
--rollback ALTER TABLE quote_revision_norwegian_travel_data ALTER COLUMN last_name SET NOT NULL;
