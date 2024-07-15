package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.domain.token.application.port.in.GetTokenUseCommand;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;

public interface GetTokenUseCase {

  TokenDomain execute(GetTokenUseCommand command);
}
