--liquibase formatted.sql

--changeset palmenhq:20191016-1600-create-quotes.sql

CREATE TABLE quote_apartment_data (
    id uuid PRIMARY KEY,
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

CREATE TABLE quote_house_data (
    id uuid PRIMARY KEY,
    ssn varchar(12) NULL,
    first_name varchar(100) NULL,
    last_name varchar(100) NULL,
    street varchar (100) NULL,
    city varchar(100) NULL,
    zip_code varchar(6) NULL,
    household_size smallint NULL,
    living_space smallint NULL
);

CREATE TABLE quotes (
    id uuid PRIMARY KEY,
    product_type varchar(20) NOT NULL,
    initiated_from varchar(20) NOT NULL,
    start_date date NULL,
    price numeric NULL,
    current_insurer varchar(100) NULL,
    created_at timestamp NOT NULL,
    quoted_at timestamp NULL,
    signed_at timestamp NULL,
    validity bigint NOT NULL,
    quote_apartment_data_id uuid NULL REFERENCES quote_apartment_data(id),
    quote_house_data_id uuid NULL REFERENCES quote_house_data(id),
    CONSTRAINT single_quote_type CHECK (
        (quote_apartment_data_id IS NOT NULL AND quote_house_data_id IS NULL)
         OR
        (quote_apartment_data_id IS NULL AND quote_house_data_id IS NOT NULL)
    )
);

--rollback DROP TABLE "quotes"; DROP TABLE "quote_house_data"; DROP TABLE "quote_apartment_data";