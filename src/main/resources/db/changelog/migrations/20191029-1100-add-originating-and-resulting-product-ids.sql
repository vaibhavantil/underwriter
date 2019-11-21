--liquibase formatted.sql

--changeset palmenhq:20191029-1100-add-originating-and-resulting-product-ids.sql

ALTER TABLE quote_revisions
    ADD COLUMN originating_product_id uuid NULL,
    ADD COLUMN signed_product_id uuid NULL
;
