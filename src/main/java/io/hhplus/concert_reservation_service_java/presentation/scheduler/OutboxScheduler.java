package io.hhplus.concert_reservation_service_java.presentation.scheduler;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository.PaymentOutboxRepository;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class OutboxScheduler {
  private final PaymentOutboxRepository paymentOutboxRepository;



  @Scheduled(fixedRate = 3 * 1000)
  public void scheduler() {
    log.info("scheduler::");
    paymentOutboxRepository.deleteCompleted();
  }
}