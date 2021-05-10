--liquibase formatted.sql

--changeset fredriklagerblad:20210505-1123-add-quote-line-items.sql

CREATE TABLE quote_line_item
(
    internal_id serial          NOT NULL PRIMARY KEY,
    revision_id integer         NOT NULL,
    type        varchar(64)     NOT NULL,
    subType     varchar(64)     NOT NULL,
    amount      NUMERIC(19, 10) NOT NULL,
    CONSTRAINT revision_id_fkey FOREIGN KEY (revision_id) REFERENCES "quote_revisions" ("id")
);

--rollback DROP TABLE quote_line_item
