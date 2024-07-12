package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.domain.token.application.port.in.GetTokenUseCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;

public interface GetTokenUseCase {

  TokenDTO execute(GetTokenUseCommand command);
}
