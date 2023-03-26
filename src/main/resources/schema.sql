DROP TABLE IF EXISTS users, requests, items, comments, bookings;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email),
    CONSTRAINT pk_user PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS requests
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description  VARCHAR(255)                                        NOT NULL,
    requester_id BIGINT                                              NOT NULL,
    CONSTRAINT "request_user_foreign" FOREIGN KEY (requester_id) REFERENCES users (id)
    );

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    descriprion VARCHAR(1000) NOT NULL,
    is_available BOOLEAN,
    owner_id BIGINT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
    request_id BIGINT REFERENCES requests (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)
    );
CREATE TABLE IF NOT EXISTS bookings
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT NOT NULL,
    booker_id  BIGINT NOT NULL,
    status     VARCHAR(10) NOT NULL,
    CONSTRAINT "booking_item_foreign" FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT "booking_user_foreign" FOREIGN KEY (booker_id) REFERENCES users (id)
    );

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    text      VARCHAR(255)                                        NOT NULL,
    item_id   BIGINT                                              NOT NULL,
    author_id BIGINT,
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "comments_item_foreign" FOREIGN KEY (item_id) references items (id),
    CONSTRAINT "comments_author_foreign" FOREIGN KEY (author_id) references users (id)
    );