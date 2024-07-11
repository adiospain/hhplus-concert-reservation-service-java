package io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.res;

import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;
import java.time.LocalDateTime;

public record IssueTokenAPIResponse(long id, LocalDateTime expireAt, long order) {

  public static IssueTokenAPIResponse from(TokenDTO token) {
    return new IssueTokenAPIResponse (token.getId(), token.getExpiredAt(), token.getOrder());
  }
}
