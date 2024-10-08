package io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservation",
    uniqueConstraints = @UniqueConstraint(columnNames = {"seat_id", "concert_schedule_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "concert_schedule_id", nullable = false)
  private Long concertScheduleId;

  @Column(name = "seat_id", nullable = false)
  private Long seatId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReservationStatus status;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "paid_at")
  private LocalDateTime paidAt;

  @Column(name = "reserved_price")
  private Integer reservedPrice;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    createdAt = LocalDateTime.now();
  }


  public void completeReservation() {
    if (this.status == ReservationStatus.EXPIRED){
      throw new CustomException(ErrorCode.EXPIRED_RESERVATION);
    }
    this.status = ReservationStatus.PAID;
    this.paidAt = LocalDateTime.now();
  }

  public void validatePayment() {
    if (this.status != ReservationStatus.OCCUPIED) {
      throw new CustomException(ErrorCode.INVALID_RESERVATION_STATUS);
    }
    if (this.createdAt.plusMinutes(5).isBefore(LocalDateTime.now())) {
      throw new CustomException(ErrorCode.EXPIRED_RESERVATION);
    }
  }

  public void markAs (ReservationStatus updateStatus){
    this.status = updateStatus;
  }

  public void markCreatedAt (LocalDateTime timestamp){
    if (this.createdAt != null && this.createdAt.isBefore(timestamp)){
      throw new CustomException(ErrorCode.TIME_PARADOX);
    }
  }
}
