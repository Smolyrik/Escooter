CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL

);

CREATE TABLE scooter_status (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE rental_status (
                               id SERIAL PRIMARY KEY,
                               name VARCHAR(50) UNIQUE NOT NULL

);

CREATE TABLE payment_status (
                                id SERIAL PRIMARY KEY,
                                name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       role_id INT REFERENCES roles(id) ON DELETE SET NULL,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       phone VARCHAR(20) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       balance DECIMAL(10,2) DEFAULT 0 CHECK (balance >= 0)
);

CREATE TABLE rental_point (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               name VARCHAR(255) NOT NULL,
                               latitude DECIMAL(10,8) NOT NULL,
                               longitude DECIMAL(11,8) NOT NULL,
                               address VARCHAR(255) NOT NULL,
                               manager_id UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE models (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) UNIQUE NOT NULL
);


CREATE TABLE pricing_plan (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              name VARCHAR(255) NOT NULL,
                              price_per_hour DECIMAL(10,2) CHECK (price_per_hour >= 0),
                              subscription_price DECIMAL(10,2) CHECK (subscription_price >= 0),
                              discount DECIMAL(5,2) CHECK (discount >= 0 AND discount <= 100)
);

CREATE TABLE scooter (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          rental_point_id UUID REFERENCES rental_point(id) ON DELETE SET NULL,
                          model_id INT REFERENCES models(id) ON DELETE CASCADE,
                          pricing_plan_id UUID REFERENCES pricing_plan(id) ON DELETE SET NULL,
                          battery_level DECIMAL(5,2) CHECK (battery_level >= 0 AND battery_level <= 100),
                          status_id INT REFERENCES scooter_status(id) ON DELETE SET NULL,
                          mileage DECIMAL(10,2) DEFAULT 0 CHECK (mileage >= 0)
);

CREATE TABLE rental (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                         scooter_id UUID REFERENCES scooter(id) ON DELETE CASCADE,
                         start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         end_time TIMESTAMP NULL,
                         distance DECIMAL(10,2) CHECK (distance >= 0),
                         total_price DECIMAL(10,2) CHECK (total_price >= 0) DEFAULT 0,
                         status_id INT REFERENCES rental_status(id) ON DELETE SET NULL
);

CREATE TABLE payment (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                          amount DECIMAL(10,2) CHECK (amount > 0) NOT NULL,
                          payment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          status_id INT REFERENCES payment_status(id) ON DELETE SET NULL
);

CREATE TABLE report (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         report_type VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         data JSON NOT NULL
);
