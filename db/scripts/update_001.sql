-- таблица кинозалов
CREATE TABLE IF NOT EXISTS halls (
  id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL,
  num_of_rows INT NOT NULL,
  num_of_seats INT NOT NULL
);

-- таблица киносеансов
CREATE TABLE IF NOT EXISTS sessions (
  id SERIAL PRIMARY KEY,
  name TEXT,
  hall_id INT NOT NULL REFERENCES halls(id)
);

-- таблица покупателей
CREATE TABLE IF NOT EXISTS customers (
  id SERIAL PRIMARY KEY,
  username VARCHAR NOT NULL,
  email VARCHAR NOT NULL UNIQUE,
  phone VARCHAR NOT NULL UNIQUE,
  password VARCHAR NOT NULL
);

-- таблица билетов
CREATE TABLE IF NOT EXISTS tickets (
    id SERIAL PRIMARY KEY,
    session_id INT NOT NULL REFERENCES sessions(id),
    row_num INT NOT NULL,
    seat_num INT NOT NULL,
    customer_id INT NOT NULL REFERENCES customers(id)
);