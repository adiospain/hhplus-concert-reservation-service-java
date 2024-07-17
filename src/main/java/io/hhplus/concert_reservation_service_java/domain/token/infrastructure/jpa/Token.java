package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "reserver_id", nullable = false)
  private Long reserverId;

  @Column(name = "access_key")
  private String accessKey;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TokenStatus status;

  @Column(name = "expire_at", nullable = false)
  private LocalDateTime expireAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public Token renew() {
    this.accessKey = UUID.randomUUID().toString();
    this.status = TokenStatus.WAIT;
    this.expireAt = LocalDateTime.now().plusMinutes(5);
    this.updatedAt = LocalDateTime.now();
    return this;
  }
}

// TokenStatus enum
