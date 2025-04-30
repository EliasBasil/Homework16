CREATE TABLE car (
    id SERIAL PRIMARY KEY,
    manufacturer VARCHAR NOT NULL,
    model VARCHAR NOT NULL,
    price INTEGER CHECK (price > 0)
);

CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    age SMALLINT CHECK (age > 0),
    driving_license BOOLEAN,
    car_id INTEGER,
    FOREIGN KEY (car_id) REFERENCES car (id)
);