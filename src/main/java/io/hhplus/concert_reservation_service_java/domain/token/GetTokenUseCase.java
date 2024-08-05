package io.hhplus.concert_reservation_service_java.domain.token;

import io.hhplus.concert_reservation_service_java.domain.token.application.port.in.GetTokenCommand;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;

public interface GetTokenUseCase {

  TokenDomain execute(GetTokenCommand command);
}
