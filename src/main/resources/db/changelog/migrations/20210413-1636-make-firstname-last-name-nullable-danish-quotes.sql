ALTER TABLE quote_revision_danish_home_contents_data
    ALTER COLUMN first_name DROP NOT NULL;

ALTER TABLE quote_revision_danish_home_contents_data
    ALTER COLUMN last_name DROP NOT NULL;

ALTER TABLE quote_revision_danish_accident_data
    ALTER COLUMN first_name DROP NOT NULL;

ALTER TABLE quote_revision_danish_accident_data
    ALTER COLUMN last_name DROP NOT NULL;

ALTER TABLE quote_revision_danish_travel_data
    ALTER COLUMN first_name DROP NOT NULL;

ALTER TABLE quote_revision_danish_travel_data
    ALTER COLUMN last_name DROP NOT NULL;

--rollback ALTER TABLE quote_revision_danish_home_contents_data ALTER COLUMN first_name NOT NULL;
--rollback ALTER TABLE quote_revision_danish_home_contents_data ALTER COLUMN last_name NOT NULL;
--rollback ALTER TABLE quote_revision_danish_accident_data ALTER COLUMN first_name NOT NULL;
--rollback ALTER TABLE quote_revision_danish_accident_data ALTER COLUMN last_name NOT NULL;
--rollback ALTER TABLE quote_revision_danish_travel_data ALTER COLUMN first_name NOT NULL;
--rollback ALTER TABLE quote_revision_danish_travel_data ALTER COLUMN last_name NOT NULL;