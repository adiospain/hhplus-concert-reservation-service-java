SET FOREIGN_KEY_CHECKS = 0;
# TRUNCATE concert_schedule;
# TRUNCATE concert_schedule_seat;
# TRUNCATE concert;
#
#
# TRUNCATE payment;
# TRUNCATE reservation;
# TRUNCATE reserver;
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

INSERT INTO concert (name)
SELECT RAND() * 10000 AS name
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
    FLOOR(RAND() * 40000) + 10000 AS capacity
FROM concert c
         CROSS JOIN (
    SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 10
) AS numbers;


INSERT INTO seat (seat_number) VALUES
                                   (1), (2), (3), (4), (5), (6), (7), (8), (9), (10),
                                   (11), (12), (13), (14), (15), (16), (17), (18), (19), (20),
                                   (21), (22), (23), (24), (25), (26), (27), (28), (29), (30),
                                   (31), (32), (33), (34), (35), (36), (37), (38), (39), (40),
                                   (41), (42), (43), (44), (45), (46), (47), (48), (49), (50),
                                   (51), (52), (53), (54), (55), (56), (57), (58), (59), (60),
                                   (61), (62), (63), (64), (65), (66), (67), (68), (69), (70),
                                   (71), (72), (73), (74), (75), (76), (77), (78), (79), (80),
                                   (81), (82), (83), (84), (85), (86), (87), (88), (89), (90),
                                   (91), (92), (93), (94), (95), (96), (97), (98), (99), (100),
                                   (101), (102), (103), (104), (105), (106), (107), (108), (109), (110),
                                   (111), (112), (113), (114), (115), (116), (117), (118), (119), (120),
                                   (121), (122), (123), (124), (125), (126), (127), (128), (129), (130),
                                   (131), (132), (133), (134), (135), (136), (137), (138), (139), (140),
                                   (141), (142), (143), (144), (145), (146), (147), (148), (149), (150),
                                   (151), (152), (153), (154), (155), (156), (157), (158), (159), (160),
                                   (161), (162), (163), (164), (165), (166), (167), (168), (169), (170),
                                   (171), (172), (173), (174), (175), (176), (177), (178), (179), (180),
                                   (181), (182), (183), (184), (185), (186), (187), (188), (189), (190),
                                   (191), (192), (193), (194), (195), (196), (197), (198), (199), (200),
                                   (201), (202), (203), (204), (205), (206), (207), (208), (209), (210),
                                   (211), (212), (213), (214), (215), (216), (217), (218), (219), (220),
                                   (221), (222), (223), (224), (225), (226), (227), (228), (229), (230),
                                   (231), (232), (233), (234), (235), (236), (237), (238), (239), (240),
                                   (241), (242), (243), (244), (245), (246), (247), (248), (249), (250),
                                   (251), (252), (253), (254), (255), (256), (257), (258), (259), (260),
                                   (261), (262), (263), (264), (265), (266), (267), (268), (269), (270),
                                   (271), (272), (273), (274), (275), (276), (277), (278), (279), (280),
                                   (281), (282), (283), (284), (285), (286), (287), (288), (289), (290),
                                   (291), (292), (293), (294), (295), (296), (297), (298), (299), (300);

INSERT INTO concert_schedule_seat (concert_schedule_id, seat_id, price)
SELECT
    cs.id AS concert_schedule_id,
    s.id AS seat_id,
    2000 AS price
FROM concert_schedule cs
         CROSS JOIN seat s;
