package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.application.reservation.port.in.CreateReservationCommand;
import io.hhplus.concert_reservation_service_java.domain.concertScheduleSeat.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.reservation.Reservation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "reserver")
@AllArgsConstructor
public class Reserver {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "point")
  private Integer point;

  public Reserver() {

  }


  public Reservation createReservation(ConcertScheduleSeat concertScheduleSeat) {
    return Reservation.builder()
        .reserver(this)
        .concertScheduleId(concertScheduleSeat.getConcertSchedule().getId())
        .seatId(concertScheduleSeat.getSeat().getId())
        .createdAt(LocalDateTime.now())
        .reservedDate(concertScheduleSeat.getConcertSchedule().getStartAt())
        .reservedPrice(concertScheduleSeat.getPrice())
        .build();
  }
}
