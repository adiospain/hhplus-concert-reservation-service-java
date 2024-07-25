package io.hhplus.concert_reservation_service_java.domain.concert.infrastructure.jpa.entity;

import io.hhplus.concert_reservation_service_java.domain.seat.infrastructure.jpa.Seat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
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

    @Version
    private long version;

  public ConcertScheduleSeat() {

  }
}
