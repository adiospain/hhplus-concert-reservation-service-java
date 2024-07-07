package io.hhplus.concert_reservation_service_java.domain.seat;

import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertScheduleId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class SeatId {
  private Long id;
  private Long concertScheduleId;
  private Long concertId;
}
