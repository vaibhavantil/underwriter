--liquibase formatted.sql

--changeset palmenhq:20191125-1100-add-breached-underwriting-guidelines.sql

ALTER TABLE quote_revisions
    ADD COLUMN breached_underwriting_guidelines text[] NULL,
    ADD COLUMN underwriting_guidelines_bypassed_by text NULL
;

--rollback ALTER TABLE quote_revisions DROP COLUMN breached_underwriting_guidelines;
