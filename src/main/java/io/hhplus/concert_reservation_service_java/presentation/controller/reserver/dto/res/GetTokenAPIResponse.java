package io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.res;

import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;
import java.time.LocalDateTime;

public record GetTokenAPIResponse(long id, LocalDateTime expireAt, long order) {
  public static GetTokenAPIResponse from(TokenDTO token) {
    return new GetTokenAPIResponse (token.getId(), token.getExpiredAt(), token.getOrder());
  }
}
