--liquibase formatted.sql

--changeset johantj:20191018-1021-add-partner-attribution.sql

ALTER TABLE "quotes"
    ADD COLUMN attributed_to varchar(100) NOT NULL DEFAULT 'HEDVIG';

UPDATE "quotes"
    SET "initiated_from" = 'RAPIO' WHERE "initiated_from" = 'PARTNER';

--rollback ALTER TABLE "quotes" DROP COLUMN attributed_to