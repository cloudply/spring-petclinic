INSERT INTO vets (id, first_name, last_name) VALUES (1, 'James', 'Carter') ON CONFLICT (id) DO NOTHING;
INSERT INTO vets (id, first_name, last_name) VALUES (2, 'Helen', 'Leary') ON CONFLICT (id) DO NOTHING;
INSERT INTO vets (id, first_name, last_name) VALUES (3, 'Linda', 'Douglas') ON CONFLICT (id) DO NOTHING;
INSERT INTO vets (id, first_name, last_name) VALUES (4, 'Rafael', 'Ortega') ON CONFLICT (id) DO NOTHING;
INSERT INTO vets (id, first_name, last_name) VALUES (5, 'Henry', 'Stevens') ON CONFLICT (id) DO NOTHING;
INSERT INTO vets (id, first_name, last_name) VALUES (6, 'Sharon', 'Jenkins') ON CONFLICT (id) DO NOTHING;

INSERT INTO specialties (id, name) VALUES (1, 'radiology') ON CONFLICT (id) DO NOTHING;
INSERT INTO specialties (id, name) VALUES (2, 'surgery') ON CONFLICT (id) DO NOTHING;
INSERT INTO specialties (id, name) VALUES (3, 'dentistry') ON CONFLICT (id) DO NOTHING;

INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (2, 1) ON CONFLICT DO NOTHING;
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 2) ON CONFLICT DO NOTHING;
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (3, 3) ON CONFLICT DO NOTHING;
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (4, 2) ON CONFLICT DO NOTHING;
INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (5, 1) ON CONFLICT DO NOTHING;

INSERT INTO types (id, name) VALUES (1, 'cat') ON CONFLICT (id) DO NOTHING;
INSERT INTO types (id, name) VALUES (2, 'dog') ON CONFLICT (id) DO NOTHING;
INSERT INTO types (id, name) VALUES (3, 'lizard') ON CONFLICT (id) DO NOTHING;
INSERT INTO types (id, name) VALUES (4, 'snake') ON CONFLICT (id) DO NOTHING;
INSERT INTO types (id, name) VALUES (5, 'bird') ON CONFLICT (id) DO NOTHING;
INSERT INTO types (id, name) VALUES (6, 'hamster') ON CONFLICT (id) DO NOTHING;

INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023') ON CONFLICT (id) DO NOTHING;
INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749') ON CONFLICT (id) DO NOTHING;
INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763') ON CONFLICT (id) DO NOTHING;
INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198') ON CONFLICT (id) DO NOTHING;
INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765') ON CONFLICT (id) DO NOTHING;
INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654') ON CONFLICT (id) DO NOTHING;
INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387') ON CONFLICT (id) DO NOTHING;
INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683') ON CONFLICT (id) DO NOTHING;
INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435') ON CONFLICT (id) DO NOTHING;
INSERT INTO owners (id, first_name, last_name, address, city, telephone) VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487') ON CONFLICT (id) DO NOTHING;

INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (1, 'Leo', '2000-09-07', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (2, 'Basil', '2002-08-06', 6, 2) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (3, 'Rosy', '2001-04-17', 2, 3) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (4, 'Jewel', '2000-03-07', 2, 3) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (5, 'Iggy', '2000-11-30', 3, 4) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (6, 'George', '2000-01-20', 4, 5) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (7, 'Samantha', '1995-09-04', 1, 6) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (8, 'Max', '1995-09-04', 1, 6) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (9, 'Lucky', '1999-08-06', 5, 7) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (10, 'Mulligan', '1997-02-24', 2, 8) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (11, 'Freddy', '2000-03-09', 5, 9) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (12, 'Lucky', '2000-06-24', 2, 10) ON CONFLICT (id) DO NOTHING;
INSERT INTO pets (id, name, birth_date, type_id, owner_id) VALUES (13, 'Sly', '2002-06-08', 1, 10) ON CONFLICT (id) DO NOTHING;

INSERT INTO visits (id, pet_id, visit_date, description) VALUES (1, 7, '2010-03-04', 'rabies shot') ON CONFLICT (id) DO NOTHING;
INSERT INTO visits (id, pet_id, visit_date, description) VALUES (2, 8, '2011-03-04', 'rabies shot') ON CONFLICT (id) DO NOTHING;
INSERT INTO visits (id, pet_id, visit_date, description) VALUES (3, 8, '2009-06-04', 'neutered') ON CONFLICT (id) DO NOTHING;
INSERT INTO visits (id, pet_id, visit_date, description) VALUES (4, 7, '2008-09-04', 'spayed') ON CONFLICT (id) DO NOTHING;

-- Default users (password: password)
INSERT INTO users (id, username, password, enabled, role) VALUES (1, 'admin', '$2a$10$Y.7UF9jLVQB2rdk9BL09EOVUvJ5igi.M2u9C2rVeADh/jCyYvZUye', TRUE, 'ADMIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, username, password, enabled, role) VALUES (2, 'user', '$2a$10$Y.7UF9jLVQB2rdk9BL09EOVUvJ5igi.M2u9C2rVeADh/jCyYvZUye', TRUE, 'USER') ON CONFLICT (id) DO NOTHING;
