INSERT INTO concert (name) VALUES
                               ('아이유 콘서트'),
                               ('블랙핑크 월드 투어'),
                               ('아일릿 유유유유유유유 매그내릭'),
                               ('트와이스 원스인어마일'),
                               ('레드벨벳 콘서트'),
                               ('아이브 쇼케이스'),
                               ('(여자)아이들 월드 투어'),
                               ('뉴진스 하우 스윗');

-- Insert data into concert_schedule table with Korea-friendly dates and times
INSERT INTO concert_schedule (concert_id, start_at, capacity) VALUES
                                                                  (1, '2025-08-15 19:00:00', 50000),
                                                                  (1, '2025-08-16 19:00:00', 50000),
                                                                  (2, '2025-09-20 20:00:00', 30000),
                                                                  (3, '2025-10-03 18:30:00', 15000),
                                                                  (4, '2025-11-11 19:00:00', 10000),
                                                                  (5, '2025-12-25 18:00:00', 20000);
INSERT INTO seat (seat_number) VALUES
                                   ('A01-01'), ('A01-02'), ('A01-03'), ('A01-04'), ('A01-05'),
                                   ('B01-01'), ('B01-02'), ('B01-03'), ('B01-04'), ('B01-05'),
                                   ('C01-01'), ('C01-02'), ('C01-03'), ('C01-04'), ('C01-05');

INSERT INTO concert_schedule_seat (concert_schedule_id, seat_id, price)
SELECT
    cs.id AS concert_schedule_id,
    s.id AS seat_id,
    CASE
        WHEN LEFT(s.seat_number, 1) = 'A' THEN 150000 + RAND() * 50000
    WHEN LEFT(s.seat_number, 1) = 'B' THEN 100000 + RAND() * 50000
    ELSE 70000 + RAND() * 30000
END AS price
FROM concert_schedule cs
CROSS JOIN seat s;
