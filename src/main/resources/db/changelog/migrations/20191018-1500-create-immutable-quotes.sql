--liquibase formatted.sql

--changeset palmenhq:20191016-1600-create-immutable-quotes.sql

CREATE TABLE master_quotes (
    id uuid PRIMARY KEY,
    initiated_from varchar(20) NOT NULL,
    created_at timestamp NOT NULL
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
    quote_apartment_data_id uuid NULL REFERENCES quote_apartment_data(id),
    quote_house_data_id uuid NULL REFERENCES quote_house_data(id),
    member_id text,
    CONSTRAINT single_quote_type CHECK (
        (quote_apartment_data_id IS NOT NULL AND quote_house_data_id IS NULL)
         OR
        (quote_apartment_data_id IS NULL AND quote_house_data_id IS NOT NULL)
    )
)

--rollback DROP TABLE quotes_revisions; DROP TABLE master_quotes;
