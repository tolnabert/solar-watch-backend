INSERT INTO client (public_id, first_name, last_name, date_of_birth, email, username, password)
VALUES ('d4f8915a-599d-4dd3-a8c3-034f7fa946c4', 'admin', 'test', '2000-01-01', 'admin@test.com', 'admin', '$2a$10$bCR/4IByhDu20ni1bOO0H.6OkwF2BsoOaMWwR0ByoL32diwbASLf2');

INSERT INTO client_roles (client_id, role)
SELECT id, 'ROLE_ADMIN' FROM client
WHERE public_id = 'd4f8915a-599d-4dd3-a8c3-034f7fa946c4';

INSERT INTO client_roles (client_id, role)
SELECT id, 'ROLE_CLIENT' FROM client
WHERE public_id = 'd4f8915a-599d-4dd3-a8c3-034f7fa946c4';

SELECT * from client
