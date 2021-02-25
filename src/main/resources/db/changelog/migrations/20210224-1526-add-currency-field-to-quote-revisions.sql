--liquibase formatted.sql

--changeset ostenforshed:20210224-1526-add-currency-field-to-quote-revisions.sql

ALTER TABLE quote_revisions
    ADD COLUMN currency varchar(3) NULL
;

UPDATE quote_revisions set currency = 'NOK' where quote_norwegian_home_contents_data_id is not null;
UPDATE quote_revisions set currency = 'NOK' where quote_norwegian_travel_data_id is not null;
UPDATE quote_revisions set currency = 'DKK' where quote_danish_home_contents_data_id is not null;
UPDATE quote_revisions set currency = 'DKK' where quote_danish_accident_data_id is not null;
UPDATE quote_revisions set currency = 'DKK' where quote_danish_travel_data_id is not null;
UPDATE quote_revisions set currency = 'SEK' where quote_apartment_data_id is not null;
UPDATE quote_revisions set currency = 'SEK' where quote_house_data_id is not null;

--rollback ALTER TABLE quote_revisions DROP COLUMN currency;