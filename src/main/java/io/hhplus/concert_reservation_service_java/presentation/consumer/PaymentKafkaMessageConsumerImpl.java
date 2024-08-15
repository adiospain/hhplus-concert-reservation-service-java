package io.hhplus.concert_reservation_service_java.presentation.consumer;

import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.concert_reservation_service_java.domain.common.message.MessageSender;
import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.kafka.PaymentKafkaMessage;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.PaymentOutboxManager;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class PaymentKafkaMessageConsumerImpl implements PaymentKafkaMessageConsumer {

  private final PaymentOutboxManager paymentOutboxManager;

  private final MessageSender messageSender;

  private final PaymentService paymentService;
  private final TokenService tokenService;

  @KafkaListener(topics = "${spring.kafka.topic.payment.name}", groupId = "PaymentOutbox")
  public void paidToMarkOutBox(String message){
    log.info("paid:: start - message = {}",
        message);
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
  @KafkaListener(topics = "${spring.kafka.topic.payment.name}", groupId = "createPayment")
  public void paidToCreatePayment(String message){
    log.info("paidToCreatePayment:: start - message = {}",
        message);
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      PaymentKafkaMessage paymentMessage = objectMapper.readValue(message, PaymentKafkaMessage.class);
      Long userId = paymentMessage.getUserId();
      Long reservationId = paymentMessage.getReservationId();
      Integer reservedPrice = paymentMessage.getReservedPrice();
      paymentService.createPayment(userId, reservationId, reservedPrice);
    } catch (JsonMappingException e) {
      throw new RuntimeException(e);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
