package io.hhplus.concert_reservation_service_java.domain.token.application.useCase;

import io.hhplus.concert_reservation_service_java.core.common.annotation.UseCase;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import io.hhplus.concert_reservation_service_java.domain.token.TouchExpiredTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class TouchExpiredTokenUseCaseImpl implements TouchExpiredTokenUseCase {
  private final TokenService tokenService;

  @Override
  @Transactional
  public void execute(){
    tokenService.bulkUpdateExpiredTokens();
  }
}
