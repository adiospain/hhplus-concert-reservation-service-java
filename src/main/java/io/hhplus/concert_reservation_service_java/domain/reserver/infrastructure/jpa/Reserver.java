package io.hhplus.concert_reservation_service_java.domain.reserver.infrastructure.jpa;

import io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Table(name = "reserver")
@AllArgsConstructor
@Getter
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
        .status(ReservationStatus.OCCUPIED)
        .reservedPrice(concertScheduleSeat.getPrice())
        .build();
  }

  public void usePoint(int price) {
    if (price > this.point){
      throw new CustomException(ErrorCode.NOT_ENOUGH_POINT);
    }
    this.point -= price;
  }

  public void chargePoint(int amount) {
    if (amount <= 0) {
      throw new CustomException(ErrorCode.INVALID_AMOUNT);
    }
    this.point += amount;
  }
}
