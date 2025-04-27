CREATE TABLE hash (
    hash             VARCHAR(256)     PRIMARY KEY,
    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    version          INT
);

CREATE TABLE url (
    hash             VARCHAR(10)       PRIMARY KEY,
    url              VARCHAR(2048)    NOT NULL,
    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    version          INT
);

CREATE INDEX url_index ON url(url)