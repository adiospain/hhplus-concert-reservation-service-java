package io.hhplus.concert_reservation_service_java.presentation.scheduler;


import io.hhplus.concert_reservation_service_java.domain.token.ActivateNextTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.TouchExpiredTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenScheduler {

  private final TouchExpiredTokenUseCase touchExpiredTokenUseCase;
  private final ActivateNextTokenUseCase activateNextTokenUseCase;

  @Scheduled(fixedRate = 30 * 1000)
  public void touchExpiredToken(){
    touchExpiredTokenUseCase.execute();
  }

  @Scheduled(fixedRate = 3 *1000)
  public void activateNextToken() {activateNextTokenUseCase.execute();}
}
