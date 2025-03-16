CREATE TABLE rental_type (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO rental_type (name) VALUES ('HOURLY'), ('SUBSCRIPTION');

ALTER TABLE rental
    ADD COLUMN rental_type_id INT REFERENCES rental_type(id) ON DELETE SET NULL;