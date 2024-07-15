package io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.res;

import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import java.time.LocalDateTime;

public record IssueTokenAPIResponse(long id, String accessKey, LocalDateTime expireAt, long order) {

  public static IssueTokenAPIResponse from(TokenDomain token) {
    return new IssueTokenAPIResponse (token.getId(), token.getAccessKey(), token.getExpiredAt(), token.getOrder());
  }
}
