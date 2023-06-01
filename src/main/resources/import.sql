INSERT INTO users(id, username, password, algorithm) VALUES(1, 'admin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'BCRYPT');
INSERT INTO users(id, username, password, algorithm) VALUES(2, 'martin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'BCRYPT');
INSERT INTO users(id, username, password, algorithm) VALUES(3, 'usuario', '$e0801$V5/rB1qLny3sy2mplgkMoiarWK1gjDAx7I7zfu1Q16WNTSERBoo9y0NDNfTdUxfJ39u182z4laSu9khUMTLvzA==$B7H1w7h8+HfCB6RJ6PsUc8M/5XRIhSylAFlhxNTQSNU=', 'SCRYPT');

INSERT INTO authorities(name, user_id) VALUES('READ', 1);
INSERT INTO authorities(name, user_id) VALUES('WRITE', 1);
INSERT INTO authorities(name, user_id) VALUES('READ', 2);
INSERT INTO authorities(name, user_id) VALUES('READ', 3);

INSERT INTO products(name, price, currency) VALUES('Chocolate', 10, 'USD');