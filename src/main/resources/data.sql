-- ADMIN
INSERT INTO client (public_id, first_name, last_name, date_of_birth, email, username, password)
VALUES ('d4f8915a-599d-4dd3-a8c3-034f7fa946c4', 'admin', 'test', '2000-01-01', 'admin@test.com', 'admin',
        '$2a$10$bOlA1.fY49cYPf0y3kmr5edADOD5vCAuU9iJObAS2SLb4AmyhDKv.');
-- ADMIN ROLE
INSERT INTO client_roles (client_id, role)
SELECT id, 'ROLE_ADMIN'
FROM client
WHERE public_id = 'd4f8915a-599d-4dd3-a8c3-034f7fa946c4';
-- USER ROLE
INSERT INTO client_roles (client_id, role)
SELECT id, 'ROLE_USER'
FROM client
WHERE public_id = 'd4f8915a-599d-4dd3-a8c3-034f7fa946c4';

-- CLIENT
INSERT INTO client (public_id, first_name, last_name, date_of_birth, email, username, password)
VALUES ('604d1d39-55ff-4abb-9d97-78e6125c3dd8', 'user', 'test', '2000-01-01', 'testuser@test.com', 'testuser',
        '$2a$10$bOlA1.fY49cYPf0y3kmr5edADOD5vCAuU9iJObAS2SLb4AmyhDKv.');

-- USER ROLE
INSERT INTO client_roles (client_id, role)
SELECT id, 'ROLE_USER'
FROM client
WHERE public_id = '604d1d39-55ff-4abb-9d97-78e6125c3dd8';

SELECT *
from client
