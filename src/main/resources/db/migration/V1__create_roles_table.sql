CREATE TABLE IF NOT EXISTS roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);
