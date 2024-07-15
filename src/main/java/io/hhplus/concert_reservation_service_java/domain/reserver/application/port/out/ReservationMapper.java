package io.hhplus.concert_reservation_service_java.domain.reserver.application.port.out;

import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
  public ReservationDomain from(Reservation reservation) {
    return ReservationDomain.builder()
        .id(reservation.getId())
        .createdAt(reservation.getCreatedAt())
        .expireAt(reservation.getCreatedAt().plusMinutes(5))
        .build();
  }
}
