package io.hhplus.concert_reservation_service_java.domain.reservation;
import io.hhplus.concert_reservation_service_java.domain.concertScheduleSeat.ConcertScheduleSeat;
import io.hhplus.concert_reservation_service_java.domain.reserver.Reserver;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservation",
    uniqueConstraints = @UniqueConstraint(columnNames = {"concert_schedule_id", "seat_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "concert_schedule_id", nullable = false)
  private Long concertScheduleId;

  @Column(name = "seat_id", nullable = false)
  private Long seatId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private Reserver reserver;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "reserved_date")
  private LocalDateTime reservedDate;

  @Column(name = "reserved_price")
  private Integer reservedPrice;
}