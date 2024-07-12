package io.hhplus.concert_reservation_service_java.domain.token.application.service;

import io.hhplus.concert_reservation_service_java.domain.token.Token;

public class TokenWithPosition {
  private final Token token;
  private final long queuePosition;

  public TokenWithPosition(Token token, long queuePosition) {
    this.token = token;
    this.queuePosition = queuePosition;
  }

  public Token getToken() {
    return token;
  }

  public long getQueuePosition() {
    return queuePosition;
  }

}
