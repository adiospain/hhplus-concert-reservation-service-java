CREATE TABLE IF NOT EXISTS reserver (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          point INT
);

-- ALTER TABLE reserver ALTER COLUMN version SET DEFAULT 0;



CREATE TABLE IF NOT EXISTS concert (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS concert_schedule (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                concert_id BIGINT NOT NULL,
                                                start_at TIMESTAMP,
                                                capacity INTEGER,

    FOREIGN KEY (concert_id) REFERENCES concert(id)
    );


CREATE TABLE IF NOT EXISTS seat (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    seat_number INTEGER
);

CREATE TABLE IF NOT EXISTS concert_schedule_seat (
                                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                     concert_schedule_id BIGINT NOT NULL,
                                                     seat_id BIGINT NOT NULL,
                                                     price INTEGER NOT NULL,
                                                     CONSTRAINT UK_concert_schedule_seat UNIQUE (seat_id, concert_schedule_id),
    FOREIGN KEY (concert_schedule_id) REFERENCES concert_schedule(id),
    FOREIGN KEY (seat_id) REFERENCES seat(id)
    );


CREATE TABLE IF NOT EXISTS reservation (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      user_id BIGINT,
      concert_schedule_id BIGINT NOT NULL,
      seat_id BIGINT NOT NULL,
      status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    paid_at TIMESTAMP,
    reserved_price INTEGER,
    FOREIGN KEY (user_id) REFERENCES reserver(id),
    UNIQUE (seat_id, concert_schedule_id)
);

CREATE TABLE IF NOT EXISTS payment (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        reserver_id BIGINT NOT NULL,
                                       reservation_id BIGINT NOT NULL,
                                       created_at TIMESTAMP NOT NULL,
                                       reserved_price INT
);

CREATE TABLE IF NOT EXISTS payment_outbox (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                message VARCHAR(255) NOT NULL,
                                completed BOOLEAN NOT NULL,
                                created_at TIMESTAMP NOT NULL
);


CREATE TABLE IF NOT EXISTS token (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       reserver_id BIGINT NOT NULL,
                       access_key VARCHAR(255) NOT NULL,
                       status VARCHAR(255) NOT NULL,
                       expire_at TIMESTAMP NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP
);