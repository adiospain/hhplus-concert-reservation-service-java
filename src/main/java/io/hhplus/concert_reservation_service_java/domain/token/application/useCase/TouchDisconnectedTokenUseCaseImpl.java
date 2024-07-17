package io.hhplus.concert_reservation_service_java.domain.token.application.useCase;

import io.hhplus.concert_reservation_service_java.core.common.common.UseCase;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.TouchDisconnectedTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.infrastructure.jpa.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class TouchDisconnectedTokenUseCaseImpl implements TouchDisconnectedTokenUseCase {
  private final TokenService tokenService;


  @Override
  @Transactional
  public void execute() {
    int disconnectedCount = tokenService.bulkUpdateDisconnectedToken();

    if (disconnectedCount > 0){
      Token token = tokenService.findMostRecentlyDisconnectedToken()
              .orElseThrow();
      tokenService.activateNextToken(token.getId());
    }
  }
}
