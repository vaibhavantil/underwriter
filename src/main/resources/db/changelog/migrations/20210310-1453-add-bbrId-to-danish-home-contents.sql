--liquibase formatted.sql

--changeset alexsj1990:20210310-1453-add-bbrId-to-danish-home-contents.sql

ALTER TABLE quote_revision_danish_home_contents_data
    ADD COLUMN bbr_id VARCHAR(36) NULL;

--rollback ALTER TABLE quote_revision_danish_home_contents_data DROP COLUMN bbr_id;