SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE concert_schedule;
TRUNCATE concert;

# TRUNCATE concert_schedule_seat;
TRUNCATE payment;
TRUNCATE reservation;
TRUNCATE reserver;
# TRUNCATE seat;
SET FOREIGN_KEY_CHECKS = 1;



CREATE TEMPORARY TABLE names (n VARCHAR(255));

INSERT INTO names (n)
SELECT a.N + b.N * 10 + c.N * 100 + d.N * 1000 + e.N * 10000
FROM (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
      UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a,
     (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
      UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b,
     (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
      UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) c,
     (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
      UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d,
     (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
      UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) e
WHERE a.N + b.N * 10 + c.N * 100 + d.N * 1000 + e.N * 10000 BETWEEN 1 AND 200000;


INSERT INTO reserver (point) VALUES
                                 (1000),
                                 (500);

INSERT INTO concert (name)
SELECT FLOOR(RAND()) * 200 AS name
FROM names;

INSERT INTO concert (name) VALUES
                               ('아이유 콘서트'),
                               ('뉴진스 하우 스윗'),
                               ('아이브 쇼케이스'),
                               ('아일릿 유유유유유유유 매그내릭'),
                               ('트와이스 원스인어마일'),
                               ('레드벨벳 콘서트');

INSERT INTO concert_schedule (concert_id, start_at, capacity)
SELECT
    c.id AS concert_id,
    DATE_ADD('2025-01-01', INTERVAL FLOOR(RAND() * 365) DAY) AS start_at,
    10000 AS capacity
FROM concert c;

# INSERT INTO seat (seat_number) VALUES
#                                    (1), (2), (3), (4), (5), (6), (7), (8), (9), (10),
#                                    (11), (12), (13), (14), (15), (16), (17), (18), (19), (20),
#                                    (21), (22), (23), (24), (25), (26), (27), (28), (29), (30),
#                                    (31), (32), (33), (34), (35), (36), (37), (38), (39), (40),
#                                    (41), (42), (43), (44), (45), (46), (47), (48), (49), (50);

# INSERT INTO reservation (
#     user_id,
#     concert_schedule_id,
#     seat_id,
#     status,
#     created_at,
#     paid_at,
#     reserved_price
# ) VALUES (
#              1,  -- Replace with the actual user ID
#              2,  -- Replace with the actual concert schedule ID
#              3,  -- Replace with the actual seat ID
#              'OCCUPIED',  -- Replace with the actual reservation status
#              DATE_ADD(NOW(), INTERVAL 9 HOUR),  -- Or replace with a specific datetime if needed
#              NULL,  -- Replace with the actual paid_at datetime or NULL
#              10   -- Replace with the actual reserved price
#          );
#
# INSERT INTO reservation (
#     user_id,
#     concert_schedule_id,
#     seat_id,
#     status,
#     created_at,
#     paid_at,
#     reserved_price
# ) VALUES (
#              1,  -- Replace with the actual user ID
#              2,  -- Replace with the actual concert schedule ID
#              4,  -- Replace with the actual seat ID
#              'OCCUPIED',  -- Replace with the actual reservation status
#              DATE_ADD(NOW(), INTERVAL 9 HOUR),  -- Or replace with a specific datetime if needed
#              NULL,  -- Replace with the actual paid_at datetime or NULL
#              10   -- Replace with the actual reserved price
#          );
#
# INSERT INTO concert_schedule_seat (concert_schedule_id, seat_id, price)
#     SELECT
#             cs.id AS concert_schedule_id,
#             s.id AS seat_id,
#             2000 AS price
#         FROM concert_schedule cs
#                  CROSS JOIN seat s;

#
EXPLAIN INSERT INTO concert_schedule_seat (concert_schedule_id, seat_id, price)
SELECT
    cs.id AS concert_schedule_id,
    s.id AS seat_id,
    2000 AS price
FROM concert_schedule cs
         CROSS JOIN seat s;

CREATE TEMPORARY TABLE temp_concert_schedule_seat (
                                                      concert_schedule_id BIGINT,
                                                      seat_id BIGINT,
                                                      price INTEGER
);

-- Step 2: Insert new combinations into the temporary table
# INSERT INTO temp_concert_schedule_seat (concert_schedule_id, seat_id, price)
# SELECT cs.id, s.id, 2000
# FROM concert_schedule cs
#          CROSS JOIN seat s
#          LEFT JOIN concert_schedule_seat css
#                    ON cs.id = css.concert_schedule_id AND s.id = css.seat_id
# WHERE css.concert_schedule_id IS NULL;

-- Step 3: Insert from the temporary table into the main table
# INSERT INTO concert_schedule_seat (concert_schedule_id, seat_id, price)
# SELECT concert_schedule_id, seat_id, price
# FROM temp_concert_schedule_seat;

-- Step 4: Drop the temporary table
DROP TEMPORARY TABLE temp_concert_schedule_seat;


DROP TEMPORARY TABLE IF EXISTS names;