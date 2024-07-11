package io.hhplus.concert_reservation_service_java.application.reservation.port.in.useCase;

import io.hhplus.concert_reservation_service_java.domain.reservation.Reservation;
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
