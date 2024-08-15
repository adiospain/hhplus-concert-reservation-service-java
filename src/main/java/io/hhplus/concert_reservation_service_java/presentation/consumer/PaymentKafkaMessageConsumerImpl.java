package io.hhplus.concert_reservation_service_java.presentation.consumer;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManager;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentKafkaMessageConsumerImpl implements PaymentKafkaMessageConsumer {

  private final PaymentOutboxManager paymentOutboxManager;

  @KafkaListener(topics = "${spring.kafka.topic.payment.name}")
  public void paid(long outboxId, String message){
    paymentOutboxManager.markComplete(outboxId);
  }
}
