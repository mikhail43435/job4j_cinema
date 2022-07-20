-- table of halls
CREATE TABLE IF NOT EXISTS halls
(
    id           SERIAL PRIMARY KEY,
    name         VARCHAR NOT NULL,
    num_of_rows  INT     NOT NULL,
    num_of_seats INT     NOT NULL
);

-- table of film sessions
CREATE TABLE IF NOT EXISTS sessions
(
    id      SERIAL PRIMARY KEY,
    name    TEXT,
    hall_id INT NOT NULL REFERENCES halls (id)
);

-- table of customers
CREATE TABLE IF NOT EXISTS customers
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL,
    email    VARCHAR NOT NULL UNIQUE,
    phone    VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL
);

-- table of tickets
CREATE TABLE IF NOT EXISTS tickets
(
    id          SERIAL PRIMARY KEY,
    session_id  INT NOT NULL REFERENCES sessions (id),
    row_num     INT NOT NULL,
    seat_num    INT NOT NULL,
    customer_id INT REFERENCES customers (id),
    UNIQUE (session_id, row_num, seat_num)
);

-- table of customers
CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    email    VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL
);