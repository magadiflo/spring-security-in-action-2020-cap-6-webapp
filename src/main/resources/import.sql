INSERT INTO users(id, username, password, algorithm) VALUES(1, 'admin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'BCRYPT');
INSERT INTO users(id, username, password, algorithm) VALUES(2, 'martin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'BCRYPT');

INSERT INTO authorities(name, user_id) VALUES('READ', 1);
INSERT INTO authorities(name, user_id) VALUES('WRITE', 1);
INSERT INTO authorities(name, user_id) VALUES('READ', 2);

INSERT INTO products(name, price, currency) VALUES('Chocolate', 10, 'USD');