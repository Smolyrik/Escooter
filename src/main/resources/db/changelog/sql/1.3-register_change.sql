UPDATE roles SET name = UPPER(name) WHERE name != UPPER(name);
UPDATE scooter_status SET name = UPPER(name) WHERE name != UPPER(name);
UPDATE payment_status SET name = UPPER(name) WHERE name != UPPER(name);
UPDATE rental_status SET name = UPPER(name) WHERE name != UPPER(name);