package io.hhplus.concert_reservation_service_java.domain.reserver;

import io.hhplus.concert_reservation_service_java.application.token.port.in.IssueTokenUseCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;

public interface IssueTokenUseCase {

  public TokenDTO execute(IssueTokenUseCommand command);
}
