--liquibase formatted.sql

--changeset ostenforshed:20210422-1030-add-deleted-quotes-table.sql

CREATE TABLE deleted_quotes (
    quote_id uuid PRIMARY KEY,
    created_at timestamp NOT NULL,
    deleted_at timestamp NOT NULL,
    type varchar(128) NOT NULL,
    member_id varchar(40) NULL,
    quote jsonb NOT NULL,
    revs jsonb NOT NULL
);

--rollback DROP TABLE deleted_quotes;