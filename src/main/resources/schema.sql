DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(255)    NOT NULL,
    email           VARCHAR(512)    NOT NULL    UNIQUE
);

CREATE TABLE IF NOT EXISTS requests
(
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    description     VARCHAR(1024),
    created         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    owner_id        BIGINT          NOT NULL    REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(255)    NOT NULL,
    description     VARCHAR(1024)   NOT NULL,
    available       BOOLEAN         NOT NULL,
    owner_id        BIGINT          NOT NULL    REFERENCES users (id) ON DELETE CASCADE,
    request_id      BIGINT          REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    start_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id         BIGINT          NOT NULL    REFERENCES items (id) ON DELETE CASCADE,
    booker_id       BIGINT          NOT NULL    REFERENCES users (id) ON DELETE CASCADE,
    status          VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS comments
(
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    text            VARCHAR(1024)   NOT NULL,
    item_id         BIGINT          NOT NULL    REFERENCES items (id) ON DELETE CASCADE,
    author_id       BIGINT          NOT NULL    REFERENCES users (id) ON DELETE CASCADE,
    created         TIMESTAMP WITHOUT TIME ZONE NOT NULL
);