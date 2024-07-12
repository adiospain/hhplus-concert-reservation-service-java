package io.hhplus.concert_reservation_service_java.presentation.scheduler;


import io.hhplus.concert_reservation_service_java.domain.token.TouchExpiredTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenScheduler {

  private final TouchExpiredTokenUseCase touchExpiredTokenUseCase;

  @Scheduled(fixedRate = 30000)
  public void touchExpiredToken(){
    touchExpiredTokenUseCase.execute();
  }

}
