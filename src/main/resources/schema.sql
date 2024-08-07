CREATE TABLE reserver (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          version BIGINT NOT NULL DEFAULT 0,
                          point INT,
                          CONSTRAINT version_check CHECK (version >= 0)
);
-- ALTER TABLE reserver ALTER COLUMN version SET DEFAULT 0;



CREATE TABLE IF NOT EXISTS concert (
                                       id BIGINT GENERATED BY DEFAULT AS IDENTITY,
                                       name VARCHAR(255),
                                        PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS concert_schedule (
                                                id BIGINT GENERATED BY DEFAULT AS IDENTITY,
                                                concert_id BIGINT NOT NULL,
                                                start_at TIMESTAMP,
                                                capacity INTEGER,
                                                PRIMARY KEY (id),
    FOREIGN KEY (concert_id) REFERENCES concert(id)
    );


CREATE TABLE IF NOT EXISTS seat (
                                    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                    seat_number INTEGER
);

CREATE TABLE IF NOT EXISTS concert_schedule_seat (
                                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
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
                                       user_id BIGINT NOT NULL,
                                       concert_schedule_id BIGINT NOT NULL,
                                       seat_id BIGINT NOT NULL,
                                       created_at TIMESTAMP NOT NULL,
                                       reserved_price INT
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