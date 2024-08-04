package io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa;

import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
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

  private int position; // 0: activeQueue, 1: waitQueue

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public void renew(long userId) {
    if (userId != this.userId)
      throw new CustomException(ErrorCode.TOKEN_AND_USER_NOT_MATCHED);
    this.accessKey = UUID.randomUUID().toString();
  }
  public static Token createWaitingToken(Long userId) {
    return Token.builder()
        .userId(userId)
        .accessKey(UUID.randomUUID().toString())
        .build();
  }

  public static Token createToDelete(long userId, String accessKey) {
      return Token.builder()
          .userId(userId)
          .accessKey(accessKey)
          .build();
  }

  public static Token create(long userId, String accessKey, int position) {
    if (position > 0){
      return Token.builder()
          .userId(userId)
          .accessKey(accessKey)
          .status(TokenStatus.WAIT)
          .position(position)
          .build();
    }
    return Token.builder()
        .userId(userId)
        .accessKey(accessKey)
        .status(TokenStatus.ACTIVE)
        .position(position)
        .build();
  }
}

// TokenStatus enum
