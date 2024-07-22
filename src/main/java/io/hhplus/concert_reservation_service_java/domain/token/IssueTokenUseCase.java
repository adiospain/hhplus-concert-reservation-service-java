package io.hhplus.concert_reservation_service_java.domain.token;

import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.IssueTokenUseCommand;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;

public interface IssueTokenUseCase {

  public TokenDomain execute(IssueTokenUseCommand command);
}
