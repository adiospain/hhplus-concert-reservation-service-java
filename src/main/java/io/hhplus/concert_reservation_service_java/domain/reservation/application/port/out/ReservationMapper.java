package io.hhplus.concert_reservation_service_java.domain.reservation.application.port.out;

import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.ReservationDTO;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
  public ReservationDTO from(Reservation reservation) {
    return ReservationDTO.builder()
        .id(reservation.getId())
        .createdAt(reservation.getCreatedAt())
        .expireAt(reservation.getCreatedAt().plusMinutes(5))
        .build();
  }
}
