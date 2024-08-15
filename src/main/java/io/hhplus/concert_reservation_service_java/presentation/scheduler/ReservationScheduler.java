package io.hhplus.concert_reservation_service_java.presentation.scheduler;

import io.hhplus.concert_reservation_service_java.domain.reservation.TouchExpiredReservationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReservationScheduler {
  private final TouchExpiredReservationUseCase touchExpiredReservationUseCase;

  @Scheduled(fixedRate = 5 * 1000)
  public void touchExpiredReservation() {
    log.info("touchExpiredReservation::");
    touchExpiredReservationUseCase.execute();
  }
}
