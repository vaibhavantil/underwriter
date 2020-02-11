--liquibase formatted.sql

--changeset fredrikareschoug:20200210-1600-rename-coinsured-to-co-insured.sql

ALTER TABLE quote_revision_norwegian_home_contents_data
    RENAME COLUMN coinsured TO co_insured;

ALTER TABLE quote_revision_norwegian_travel_data
    RENAME COLUMN coinsured TO co_insured;

--rollback ALTER TABLE quote_norwegian_home_contents_data_id RENAME COLUMN co_insured TO coinsured; ALTER TABLE quote_revision_norwegian_travel_data RENAME COLUMN co_insured TO coinsured;