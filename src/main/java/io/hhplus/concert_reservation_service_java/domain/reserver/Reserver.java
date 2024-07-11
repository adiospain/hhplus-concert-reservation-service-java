package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.domain.concertScheduleSeat.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.payment.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationStatus;
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
        .createdAt(LocalDateTime.now())
        .reservedPrice(concertScheduleSeat.getPrice())
        .build();
  }

  public Payment createPayment(Reservation reservation) {
    if (reservation.getReservedPrice() > this.point){
      throw new CustomException(ErrorCode.NOT_ENOUGH_POINT);
    }
    if (reservation.getStatus() != ReservationStatus.OCCUPIED){
      throw new CustomException(ErrorCode.INVALID_RESERVATION_STATUS);
    }
    LocalDateTime now = LocalDateTime.now();
    if (reservation.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())){
      this.point -= reservation.getReservedPrice();
      reservation.setStatus(ReservationStatus.PAID);
      return Payment.builder()
          .reserver(this)
          .reservation(reservation)
          .createdAt(now)
          .build();
    }
    return null;
  }

  public void chargePoint(int amount) {
    this.point += amount;
  }
}
