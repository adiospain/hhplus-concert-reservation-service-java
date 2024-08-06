package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@Builder
@AllArgsConstructor
@Getter
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "concert_schedule_id", nullable = false)
  private Long concertScheduleId;

  @Column(name = "seat_id", nullable = false)
  private Long seatId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "reserved_price")
  private Integer reservedPrice;

  public Payment() {

  }

  public static Payment createToPay(long userId, long concertScheduleId, long seatId, int pirce) {
    return Payment.builder()
        .userId(userId)
        .concertScheduleId(concertScheduleId)
        .seatId(seatId)
        .reservedPrice(pirce)
        .createdAt(LocalDateTime.now())
        .build();
  }
}