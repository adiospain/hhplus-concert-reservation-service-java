package io.hhplus.concert_reservation_service_java.domain.reservation.application.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReservationDomain {
  private long id;
  private LocalDateTime createdAt;
  private LocalDateTime expireAt;
}
