INSERT INTO vets (id, first_name, last_name) SELECT 1, 'James', 'Carter' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=1);
INSERT INTO vets (id, first_name, last_name) SELECT 2, 'Helen', 'Leary' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=2);
INSERT INTO vets (id, first_name, last_name) SELECT 3, 'Linda', 'Douglas' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=3);
INSERT INTO vets (id, first_name, last_name) SELECT 4, 'Rafael', 'Ortega' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=4);
INSERT INTO vets (id, first_name, last_name) SELECT 5, 'Henry', 'Stevens' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=5);
INSERT INTO vets (id, first_name, last_name) SELECT 6, 'Sharon', 'Jenkins' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=6);

INSERT INTO specialties (id, name) SELECT 1, 'radiology' WHERE NOT EXISTS (SELECT * FROM specialties WHERE name='radiology');
INSERT INTO specialties (id, name) SELECT 2, 'surgery' WHERE NOT EXISTS (SELECT * FROM specialties WHERE name='surgery'); 
INSERT INTO specialties (id, name) SELECT 3, 'dentistry' WHERE NOT EXISTS (SELECT * FROM specialties WHERE name='dentistry');

INSERT INTO vet_specialties VALUES (2, 1) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (3, 2) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (3, 3) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (4, 2) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (5, 1) ON CONFLICT (vet_id, specialty_id) DO NOTHING;

INSERT INTO types (id, name) SELECT 1, 'cat' WHERE NOT EXISTS (SELECT * FROM types WHERE name='cat');
INSERT INTO types (id, name) SELECT 2, 'dog' WHERE NOT EXISTS (SELECT * FROM types WHERE name='dog');
INSERT INTO types (id, name) SELECT 3, 'lizard' WHERE NOT EXISTS (SELECT * FROM types WHERE name='lizard');
INSERT INTO types (id, name) SELECT 4, 'snake' WHERE NOT EXISTS (SELECT * FROM types WHERE name='snake');
INSERT INTO types (id, name) SELECT 5, 'bird' WHERE NOT EXISTS (SELECT * FROM types WHERE name='bird');
INSERT INTO types (id, name) SELECT 6, 'hamster' WHERE NOT EXISTS (SELECT * FROM types WHERE name='hamster');

INSERT INTO owners (id, first_name, last_name, address, city, telephone) SELECT 1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=1);
INSERT INTO owners (id, first_name, last_name, address, city, telephone) SELECT 2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=2);
INSERT INTO owners (id, first_name, last_name, address, city, telephone) SELECT 3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=3);
INSERT INTO owners (id, first_name, last_name, address, city, telephone) SELECT 4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=4);
INSERT INTO owners (id, first_name, last_name, address, city, telephone) SELECT 5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=5);
INSERT INTO owners (id, first_name, last_name, address, city, telephone) SELECT 6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=6);
INSERT INTO owners (id, first_name, last_name, address, city, telephone) SELECT 7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=7);
INSERT INTO owners (id, first_name, last_name, address, city, telephone) SELECT 8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=8);
INSERT INTO owners (id, first_name, last_name, address, city, telephone) SELECT 9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=9);
INSERT INTO owners (id, first_name, last_name, address, city, telephone) SELECT 10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487' WHERE NOT EXISTS (SELECT * FROM owners WHERE id=10);

INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 1, 'Leo', '2000-09-07', 1, 1 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=1);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 2, 'Basil', '2002-08-06', 6, 2 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=2);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 3, 'Rosy', '2001-04-17', 2, 3 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=3);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 4, 'Jewel', '2000-03-07', 2, 3 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=4);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 5, 'Iggy', '2000-11-30', 3, 4 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=5);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 6, 'George', '2000-01-20', 4, 5 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=6);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 7, 'Samantha', '1995-09-04', 1, 6 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=7);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 8, 'Max', '1995-09-04', 1, 6 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=8);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 9, 'Lucky', '1999-08-06', 5, 7 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=9);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 10, 'Mulligan', '1997-02-24', 2, 8 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=10);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 11, 'Freddy', '2000-03-09', 5, 9 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=11);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 12, 'Lucky', '2000-06-24', 2, 10 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=12);
INSERT INTO pets (id, name, birth_date, type_id, owner_id) SELECT 13, 'Sly', '2002-06-08', 1, 10 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=13);

INSERT INTO visits (id, pet_id, visit_date, description) SELECT 1, 7, '2010-03-04', 'rabies shot' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=1);
INSERT INTO visits (id, pet_id, visit_date, description) SELECT 2, 8, '2011-03-04', 'rabies shot' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=2);
INSERT INTO visits (id, pet_id, visit_date, description) SELECT 3, 8, '2009-06-04', 'neutered' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=3);
INSERT INTO visits (id, pet_id, visit_date, description) SELECT 4, 7, '2008-09-04', 'spayed' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=4);

-- Users with encoded passwords (password is 'password')
INSERT INTO users (username, password, enabled) 
  SELECT 'admin', '{bcrypt}$2a$10$jK.lJx6AYfYPIXkvRFQaAe3DzYIB6rriYkZqkpXYqBJWLIxLcHay.', TRUE 
  WHERE NOT EXISTS (SELECT * FROM users WHERE username='admin');
INSERT INTO users (username, password, enabled) 
  SELECT 'user', '{bcrypt}$2a$10$jK.lJx6AYfYPIXkvRFQaAe3DzYIB6rriYkZqkpXYqBJWLIxLcHay.', TRUE 
  WHERE NOT EXISTS (SELECT * FROM users WHERE username='user');

INSERT INTO authorities (username, authority) 
  SELECT 'admin', 'ROLE_ADMIN' 
  WHERE NOT EXISTS (SELECT * FROM authorities WHERE username='admin' AND authority='ROLE_ADMIN');
INSERT INTO authorities (username, authority) 
  SELECT 'admin', 'ROLE_USER' 
  WHERE NOT EXISTS (SELECT * FROM authorities WHERE username='admin' AND authority='ROLE_USER');
INSERT INTO authorities (username, authority) 
  SELECT 'user', 'ROLE_USER' 
  WHERE NOT EXISTS (SELECT * FROM authorities WHERE username='user' AND authority='ROLE_USER');
