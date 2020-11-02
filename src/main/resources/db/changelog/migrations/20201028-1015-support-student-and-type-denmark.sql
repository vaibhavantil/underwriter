--liquibase formatted.sql

--changeset elvingranat:20201028-1015-support-student-and-type-denmark.sql

ALTER TABLE quote_revision_danish_home_contents_data
    ADD COLUMN is_student boolean NOT NULL DEFAULT false;

ALTER TABLE quote_revision_danish_home_contents_data
    ADD COLUMN type VARCHAR (20) NOT NULL DEFAULT 'RENT';

--rollback ALTER TABLE quote_revision_danish_home_contents_data DROP COLUMN is_student; ALTER TABLE quote_revision_danish_home_contents_data DROP COLUMN type;