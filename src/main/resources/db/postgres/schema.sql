CREATE TABLE IF NOT EXISTS vets (
  id         SERIAL PRIMARY KEY,
  first_name TEXT,
  last_name  TEXT
);
CREATE INDEX ON vets (last_name);

CREATE TABLE IF NOT EXISTS specialties (
  id   SERIAL PRIMARY KEY,
  name TEXT
);
CREATE INDEX ON specialties (name);

CREATE TABLE IF NOT EXISTS vet_specialties (
  vet_id       INT NOT NULL REFERENCES vets (id),
  specialty_id INT NOT NULL REFERENCES specialties (id),
  UNIQUE (vet_id, specialty_id)
);

CREATE TABLE IF NOT EXISTS types (
  id   SERIAL PRIMARY KEY,
  name TEXT
);
CREATE INDEX ON types (name);

CREATE TABLE IF NOT EXISTS owners (
  id         SERIAL PRIMARY KEY,
  first_name TEXT,
  last_name  TEXT,
  address    TEXT,
  city       TEXT,
  telephone  TEXT
);
CREATE INDEX ON owners (last_name);

CREATE TABLE IF NOT EXISTS pets (
  id         SERIAL PRIMARY KEY,
  name       TEXT,
  birth_date DATE,
  type_id    INT NOT NULL REFERENCES types (id),
  owner_id   INT REFERENCES owners (id)
);
CREATE INDEX ON pets (name);
CREATE INDEX ON pets (owner_id);

CREATE TABLE IF NOT EXISTS visits (
  id          SERIAL PRIMARY KEY,
  pet_id      INT REFERENCES pets (id),
  visit_date  DATE,
  description TEXT
);
CREATE INDEX ON visits (pet_id);

CREATE TABLE IF NOT EXISTS users (
  id       SERIAL PRIMARY KEY,
  username TEXT UNIQUE,
  password TEXT,
  enabled  BOOLEAN,
  role     TEXT
);
CREATE INDEX ON users (username);
