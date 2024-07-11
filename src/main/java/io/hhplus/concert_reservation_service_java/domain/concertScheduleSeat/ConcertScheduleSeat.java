package io.hhplus.concert_reservation_service_java.domain.concertScheduleSeat;

import io.hhplus.concert_reservation_service_java.domain.concertSchedule.ConcertSchedule;
import io.hhplus.concert_reservation_service_java.domain.reservation.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reserver.Reserver;
import io.hhplus.concert_reservation_service_java.domain.seat.Seat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(
    name = "concert_schedule_seat",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_concert_schedule_seat",
        columnNames = {"concert_schedule_id", "seat_id"}
    )
)
@Data
@AllArgsConstructor
public class ConcertScheduleSeat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "concert_schedule_id", nullable = false)
  private ConcertSchedule concertSchedule;

  @ManyToOne
  @JoinColumn(name = "seat_id", nullable = false)
  private Seat seat;

  @Column(name = "price")
  private Integer price;


  public ConcertScheduleSeat() {

  }
}
