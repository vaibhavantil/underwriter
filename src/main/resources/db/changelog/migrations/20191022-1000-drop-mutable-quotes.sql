--liquibase formatted.sql

--changeset palmenhq:20191022-1000-drop-mutable-quotes.sql

DROP TABLE "quotes";
DROP TABLE "quote_house_data";
DROP TABLE "quote_apartment_data";
