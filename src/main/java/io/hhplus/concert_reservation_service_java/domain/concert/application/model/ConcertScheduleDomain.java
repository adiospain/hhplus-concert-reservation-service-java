package io.hhplus.concert_reservation_service_java.domain.concert.application.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ConcertScheduleDomain {
    private long id;
    private LocalDateTime startAt;
    private int capacity;
}