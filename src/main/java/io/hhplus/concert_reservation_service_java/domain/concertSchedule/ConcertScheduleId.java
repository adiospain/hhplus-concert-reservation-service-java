package io.hhplus.concert_reservation_service_java.domain.concertSchedule;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

@Embeddable
@Data
public class ConcertScheduleId implements Serializable {

  private Long id;
  private Long concertId;

  public ConcertScheduleId() {
  }

  public ConcertScheduleId(Long id, Long concertId) {
    this.id = id;
    this.concertId = concertId;
  }


}
