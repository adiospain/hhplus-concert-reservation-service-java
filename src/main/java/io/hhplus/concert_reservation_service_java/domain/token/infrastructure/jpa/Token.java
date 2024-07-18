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
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "reserver_id", nullable = false)
  private Long userId;

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

  public void renew() {
    this.status = TokenStatus.WAIT;
    this.accessKey = UUID.randomUUID().toString();
    LocalDateTime now = LocalDateTime.now();
    this.expireAt = now.plusMinutes(5);
    this.createdAt = now;
    this.updatedAt = now;
  }
  public static Token createWaitingToken(Long userId) {
    return Token.builder()
        .userId(userId)
        .accessKey(UUID.randomUUID().toString())
        .status(TokenStatus.WAIT)
        .expireAt(LocalDateTime.now().plusMinutes(5))
        .build();
  }

  public void  turnActive() {
    this.status = TokenStatus.ACTIVE;
  }

  public void setIdForTest(long id) {
    this.id = id;
  }
}

// TokenStatus enum
