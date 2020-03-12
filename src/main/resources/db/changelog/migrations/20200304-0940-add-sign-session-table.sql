--liquibase formatted.sql

--changeset fredrikareschoug:20200304-0940-add-sign-session-table.sql

CREATE TABLE sign_sessions (
    id uuid PRIMARY KEY,
    created_at timestamp NOT NULL
);

CREATE TABLE sign_session_master_quote (
    sign_session_id uuid references sign_sessions(id),
    master_quote_id uuid references master_quotes(id),
    PRIMARY KEY(sign_session_id, master_quote_id)
);

--rollback DROP TABLE sign_sessions; DROP TABLE sign_session_master_quote