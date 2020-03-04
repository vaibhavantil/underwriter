--liquibase formatted.sql

--changeset fredrikareschoug:20200304-0940-add-sign-session-table.sql

CREATE TABLE sign_sessions (
    id uuid PRIMARY KEY
);

CREATE TABLE sign_session_quote_revision (
    sign_session_id uuid references sign_sessions(id),
    master_quote_id integer references master_quotes(id),
    PRIMARY KEY(sign_session_id, master_quote_id)
);

--rollback DROP TABLE sign_sessions; DROP TABLE sign_session_quote_revision