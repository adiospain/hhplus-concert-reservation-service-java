package io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto;

import io.hhplus.concert_reservation_service_java.domain.reservation.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reserver.Reserver;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleSeatDTO;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
