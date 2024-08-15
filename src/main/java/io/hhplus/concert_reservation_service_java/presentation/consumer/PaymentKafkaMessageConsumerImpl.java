package io.hhplus.concert_reservation_service_java.presentation.consumer;

import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManager;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class PaymentKafkaMessageConsumerImpl implements PaymentKafkaMessageConsumer {

  private final PaymentOutboxManager paymentOutboxManager;

  private final MessageSender messageSender;

  private final TokenService tokenService;

  @KafkaListener(topics = "${spring.kafka.topic.payment.name}")
  public void paidToCreateOutBox(String message){
    log.info("paid::");
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      PaymentKafkaMessage paymentMessage = objectMapper.readValue(message, PaymentKafkaMessage.class);
      Long outboxId = paymentMessage.getOutboxId();
      paymentOutboxManager.markComplete(outboxId);
    } catch (JsonMappingException e) {
      throw new RuntimeException(e);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
  @KafkaListener(topics = "${spring.kafka.topic.payment.name}")
  public void paid(long outboxId, String message){
    paymentOutboxManager.markComplete(outboxId);
  }
}
