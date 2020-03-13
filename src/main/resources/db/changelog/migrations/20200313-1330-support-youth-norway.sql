--liquibase formatted.sql

--changeset fredrikareschoug:20200313-1330-support-youth-norway.sql

ALTER TABLE quote_revision_norwegian_home_contents_data
    RENAME COLUMN is_student TO is_youth;

ALTER TABLE quote_revision_norwegian_travel_data
    ADD COLUMN is_youth boolean NOT NULL DEFAULT false;

--rollback ALTER TABLE quote_revision_norwegian_home_contents_data RENAME COLUMN is_youth TO is_student; ALTER TABLE quote_revision_norwegian_travel_data DROP COLUMN is_youth;