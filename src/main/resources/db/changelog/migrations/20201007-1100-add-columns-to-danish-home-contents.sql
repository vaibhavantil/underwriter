--liquibase formatted.sql

--changeset fredrikareschoug:20201007-1100-add-danish-data-table.sql

ALTER TABLE quote_revision_danish_home_contents_data
    ADD COLUMN ssn varchar(10) NULL,
    ADD COLUMN birth_date date NOT NULL,
    ADD COLUMN first_name varchar(100) NOT NULL,
    ADD COLUMN last_name varchar(100) NOT NULL,
    ADD COLUMN email varchar(255) NULL,
    ADD COLUMN street varchar (100) NOT NULL,
    ADD COLUMN zip_code varchar(4) NOT NULL,
    ADD COLUMN co_insured smallint NOT NULL,
    ADD COLUMN living_space smallint NOT NULL
;

--rollback ALTER TABLE quote_revision_house_data DROP COLUMN ssn, DROP COLUMN birth_date DROP COLUMN first_name, DROP COLUMN last_name, DROP COLUMN email, DROP COLUMN street, DROP COLUMN zip_code, DROP COLUMN co_insured, DROP COLUMN living_space;
