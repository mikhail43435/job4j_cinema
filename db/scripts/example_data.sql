-- system administrator account
INSERT INTO users(email, password)
VALUES ('sa', 'sa');

-- example context
INSERT INTO customers(username, email, phone, password)
VALUES ('Tom Brady', 'tom@gmail.com', '345683958', 'password123'),
       ('Thomas Edison', 'thomas@yahoo.com', '938473628', 'mypassword'),
       ('Queen Latifah', 'ql@gmail.com', '341234567', 'passwordQL'),
       ('James Dean', 'james@gmail.com', '485737456', 'password222');
INSERT INTO halls(name, num_of_rows, num_of_seats)
VALUES ('Blue hall 30x50', 30, 50),
       ('VIP hall 5x5', 5, 5),
       ('Green hall 10x12', 10, 12);
INSERT INTO sessions(name, hall_id)
VALUES ('New film', 1),
       ('Great film', 2),
       ('Old good film', 3),
       ('Cartoons', 1),
       ('Boring film', 1);
INSERT INTO tickets(session_id, row_num, seat_num, customer_id)
VALUES (1, 20, 15, 3),
       (2, 6, 6, 2),
       (3, 3, 9, 1),
       (4, 2, 10, 3),
       (5, 8, 1, 4),
       (3, 3, 2, 2),
       (2, 1, 4, 4),
       (1, 2, 4, 1),
       (4, 2, 1, 1);