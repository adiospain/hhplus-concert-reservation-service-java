package io.hhplus.concert_reservation_service_java.presentation.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.concert_reservation_service_java.domain.common.message.MessageSender;
import io.hhplus.concert_reservation_service_java.domain.payment.PaymentService;
import io.hhplus.concert_reservation_service_java.domain.payment.message.kafka.PaymentKafkaMessage;
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

  @Override
  @KafkaListener(topics = "${spring.kafka.topic.payment.name}", groupId = "PaymentOutbox")
  public void paidToMarkOutBox(String message){
    log.info("paid:: start - message = {}",
        message);

    paymentOutboxManager.markComplete(message);

  }
}
