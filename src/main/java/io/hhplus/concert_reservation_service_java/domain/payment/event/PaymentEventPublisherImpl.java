package io.hhplus.concert_reservation_service_java.domain.payment.event;

import io.hhplus.concert_reservation_service_java.domain.common.event.CustomEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void execute(CustomEvent paymentEvent) {
    applicationEventPublisher.publishEvent(paymentEvent);
  }
}
