--liquibase formatted.sql

--changeset johant:20191014-1600-create-tables.sql

CREATE TABLE "quote" (
    id UUID PRIMARY KEY,
    incomplete_quote_id UUID NOT NULL,
    quote_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    state character varying(255),
    ssn text,
    first_name text,
    last_name text,

    line_of_business text,
    product_type character varying(255),


    street text,
    zip_code character varying(255),
    city character varying(255),
    livingSpace integer,
    householdSize integer,
    is_student boolean NOT NULL,

    price numeric(19,2),
    currency character varying(255),
    start_date date,
    start_date_zone character varying(255),

    partner text
)

--rollback DROP TABLE "quote_request"