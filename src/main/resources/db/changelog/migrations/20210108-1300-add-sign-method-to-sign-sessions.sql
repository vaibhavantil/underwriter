--liquibase formatted.sql

--changeset fredrikareschoug:20210108-1300-add-sign-method-to-sign-sessions.sql

ALTER TABLE sign_sessions
    ADD COLUMN sign_method VARCHAR (20) DEFAULT NULL;

--rollback ALTER TABLE sign_sessions DROP COLUMN sign_method;
