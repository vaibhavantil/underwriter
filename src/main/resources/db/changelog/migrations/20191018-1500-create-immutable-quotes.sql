--liquibase formatted.sql

--changeset palmenhq:20191016-1600-create-immutable-quotes.sql

-- Create tables

CREATE TABLE master_quotes (
    id uuid PRIMARY KEY,
    initiated_from varchar(20) NOT NULL,
    created_at timestamp NOT NULL
);

CREATE TABLE quote_revision_apartment_data (
    internal_id serial PRIMARY KEY,
    id uuid NOT NULL,
    ssn varchar(12) NULL,
    first_name varchar(100) NULL,
    last_name varchar(100) NULL,
    street varchar (100) NULL,
    city varchar(100) NULL,
    zip_code varchar(6) NULL,
    household_size smallint NULL,
    living_space smallint NULL,
    sub_type varchar(20) NULL
);

CREATE TABLE quote_revision_house_data (
    internal_id serial PRIMARY KEY,
    id uuid NOT NULL,
    ssn varchar(12) NULL,
    first_name varchar(100) NULL,
    last_name varchar(100) NULL,
    street varchar (100) NULL,
    city varchar(100) NULL,
    zip_code varchar(6) NULL,
    household_size smallint NULL,
    living_space smallint NULL
);

CREATE TABLE quote_revisions (
    id serial PRIMARY KEY,
    master_quote_id uuid NOT NULL REFERENCES master_quotes(id),
    timestamp timestamp NOT NULL,
    validity bigint NOT NULL,
    product_type varchar(20) NOT NULL,
    state varchar(100) NOT NULL,
    attributed_to varchar(100),
    current_insurer varchar(100) NULL,
    start_date date NULL,
    price numeric NULL,
    quote_apartment_data_id integer NULL REFERENCES quote_revision_apartment_data(internal_id),
    quote_house_data_id integer NULL REFERENCES quote_revision_house_data(internal_id),
    member_id text,
    CONSTRAINT single_quote_type CHECK (
        (quote_apartment_data_id IS NOT NULL AND quote_house_data_id IS NULL)
         OR
        (quote_apartment_data_id IS NULL AND quote_house_data_id IS NOT NULL)
    )
);

-- Migrate existing data

INSERT INTO master_quotes (id, initiated_from, created_at)
SELECT id, initiated_from, created_at
FROM quotes
;

INSERT INTO quote_revision_apartment_data (id, ssn, first_name, last_name, street, city, zip_code, household_size, living_space, sub_type)
SELECT id, ssn, first_name, last_name, street, city, zip_code, household_size, living_space, sub_type
FROM quote_apartment_data
;

INSERT INTO quote_revisions (master_quote_id, timestamp, validity, product_type, state, attributed_to, current_insurer, start_date, price, quote_apartment_data_id, member_id)
SELECT q.id, now(), q.validity, q.product_type,
    CASE
        WHEN (signed_at IS NOT NULL) THEN 'SIGNED'
        WHEN (quoted_at IS NOT NULL) THEN 'QUOTED'
        ELSE 'INCOMPLETE'
    END
, q.attributed_to, q.current_insurer, q.start_date, q.price, qrad.internal_id, q.member_id
FROM quotes q
LEFT JOIN quote_revision_apartment_data qrad
    ON qrad.id = q.quote_apartment_data_id
;

--rollback DROP TABLE quote_revisions; DROP TABLE quote_revision_house_data; DROP TABLE quote_revision_apartment_data; DROP TABLE master_quotes;
