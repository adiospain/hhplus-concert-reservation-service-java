package io.hhplus.concert_reservation_service_java.presentation.scheduler;

import io.hhplus.concert_reservation_service_java.domain.reservation.TouchExpiredReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReservationScheduler {
  private final TouchExpiredReservationUseCase touchExpiredReservationUseCase;

  @Scheduled(fixedRate = 10 * 60 * 1000)
  public void touchExpiredReservation() {
    touchExpiredReservationUseCase.execute();
  }
}
