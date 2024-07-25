INSERT INTO reserver (point) VALUES
                                 (1000),
                                 (500),
                                 (2000),
                                 (750),
                                 (1500),
                                 (300),
                                 (3000),
                                 (100),
                                 (2500),
                                 (800),
                                 (1200),
                                 (600),
                                 (1800),
                                 (400),
                                 (2200),
                                 (900),
                                 (1600),
                                 (200),
                                 (3500),
                                 (700);

INSERT INTO concert (name) VALUES
                                ('아이유 콘서트'),
                                ('뉴진스 하우 스윗'),
                                ('아이브 쇼케이스'),
                                ('아일릿 유유유유유유유 매그내릭'),
                                ('트와이스 원스인어마일'),
                                ('레드벨벳 콘서트');

INSERT INTO concert_schedule (concert_id, start_at, capacity) VALUES
                                                                  (1, '2025-08-15 19:00:00', 50000),
                                                                  (1, '2025-08-16 19:00:00', 50000),
                                                                  (2, '2025-09-20 20:00:00', 30000),
                                                                  (3, '2025-10-03 18:30:00', 15000),
                                                                  (4, '2025-11-11 19:00:00', 10000),
                                                                  (5, '2025-12-25 18:00:00', 20000);
INSERT INTO seat (seat_number) VALUES
                                   (1), (2), (3), (4), (5), (6), (7), (8), (9), (10),
                                   (11), (12), (13), (14), (15), (16), (17), (18), (19), (20),
                                   (21), (22), (23), (24), (25), (26), (27), (28), (29), (30),
                                   (31), (32), (33), (34), (35), (36), (37), (38), (39), (40),
                                   (41), (42), (43), (44), (45), (46), (47), (48), (49), (50);

INSERT INTO concert_schedule_seat (concert_schedule_id, seat_id, price)
SELECT
    cs.id AS concert_schedule_id,
    s.id AS seat_id,
    CASE
        WHEN LEFT(s.seat_number, 1) = 'A' THEN 150000 + 1 * 50000
    WHEN LEFT(s.seat_number, 1) = 'B' THEN 100000 + 2 * 50000
    ELSE 70000 + 1 * 30000
END AS price
FROM concert_schedule cs
CROSS JOIN seat s;
