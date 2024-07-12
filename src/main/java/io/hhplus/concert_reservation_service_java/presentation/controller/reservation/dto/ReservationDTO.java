package io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReservationDTO {
  private long id;
  private LocalDateTime createdAt;
  private LocalDateTime expireAt;
}
