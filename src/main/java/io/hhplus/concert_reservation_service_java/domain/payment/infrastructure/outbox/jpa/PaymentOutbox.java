package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_outbox")
public class PaymentOutbox implements Outbox {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String id;

  @Column(name = "message", nullable = false)
  private String message;

  @Column(name = "completed", nullable = false)
  private boolean completed;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime created_at;

  public PaymentOutbox(String message, boolean completed) {
    this.message = message;
    this.completed = completed;
  }

  public PaymentOutbox() {

  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public boolean isCompleted() {
    return this.completed;
  }
}
