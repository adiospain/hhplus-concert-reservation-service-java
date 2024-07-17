package io.hhplus.concert_reservation_service_java.domain.token.application.model;

import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TokenDomain {
  private long id;
  private Long userId;
  private String accessKey;
  private LocalDateTime expiredAt;
  private long queuePosition;

  public TokenDomain(Token token, long queuePosition) {
    this.id = token.getId();
    this.userId = token.getUserId();
    this.accessKey = token.getAccessKey();
    this.expiredAt = token.getExpireAt();
    this.queuePosition = queuePosition;
  }
}
