--liquibase formatted.sql

--changeset elvingranat:20201028-1015-support-student-and-type-denmark.sql

ALTER TABLE quote_revision_danish_home_content
    ADD COLUMN is_student boolean NOT NULL;

ALTER TABLE quote_revision_danish_home_content
    ADD COLUMN type VARCHAR (20) NOT NULL;

--rollback ALTER TABLE quote_revision_danish_home_content DROP COLUMN is_student; ALTER TABLE quote_revision_danish_home_content DROP COLUMN type;