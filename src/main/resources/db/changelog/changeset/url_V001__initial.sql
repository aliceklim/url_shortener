CREATE TABLE hash (
    id               UUID             PRIMARY KEY,
    hash             VARCHAR(255)     NOT NULL,
    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    version          INT
);

CREATE TABLE url (
    id               UUID             PRIMARY KEY,
    hash             VARCHAR(255)     NOT NULL,
    url              VARCHAR(2048)    NOT NULL,
    created_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    version          INT,
    CONSTRAINT fk_url_hash FOREIGN KEY (hash) REFERENCES hash(id)
);

CREATE INDEX url_index ON url(url)