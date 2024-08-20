package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "payment_outbox")
@Builder
@Getter
@NoArgsConstructor // Required for JPA
@AllArgsConstructor // Used by the builder
public class PaymentOutbox implements Outbox {

  @Id
  @Column (name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "message", nullable = false)
  private String message;

  @Column(name = "completed", nullable = false)
  private boolean completed;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    this.id = getUUID(this.message);
    this.completed = false;
    this.createdAt = LocalDateTime.now();
  }

  public static String getUUID(String message) {
    int hash = Objects.hash(message);
    long mostSignificantBits = ((long) hash << 32) | (hash & 0xFFFFFFFFL);
    long leastSignificantBits = ~mostSignificantBits;
    return new UUID (mostSignificantBits, leastSignificantBits).toString();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
