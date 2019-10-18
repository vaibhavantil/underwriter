--liquibase formatted.sql

--changeset palmenhq:20191018-1100-add-quote-member-id.sql

ALTER TABLE quotes ADD COLUMN member_id text NULL;

--rollback ALTER TABLE quotes DROP COLUMN member_id;
