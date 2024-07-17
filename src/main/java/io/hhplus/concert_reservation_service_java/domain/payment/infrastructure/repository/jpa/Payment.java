package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "payment")
@Builder
@Getter
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "reserver_id", nullable = false)
  private Long userId;

  @Column(name = "reservation_id", nullable = false)
  private Long reservationId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public static Payment createFrom(Long reserverId, Long reservationId) {
    return Payment.builder()
        .userId(reserverId)
        .reservationId(reservationId)
        .createdAt(LocalDateTime.now())
        .build();
  }
}