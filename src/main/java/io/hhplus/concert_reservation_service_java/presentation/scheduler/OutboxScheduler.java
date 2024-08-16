package io.hhplus.concert_reservation_service_java.presentation.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.PaymentMessageSender;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.kafka.PaymentKafkaMessage;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.kafka.PaymentKafkaMessageProducer;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository.PaymentOutboxRepository;
import io.hhplus.concert_reservation_service_java.domain.reservation.ReservationService;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.ReservationStatus;
import io.hhplus.concert_reservation_service_java.domain.token.TokenService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class OutboxScheduler {

  private final PaymentOutboxRepository paymentOutboxRepository;
  private final ReservationService reservationService;
  private final PaymentKafkaMessageProducer paymentKafkaMessageProducer;
  private final TokenService tokenService;


  @Value("${spring.kafka.topic.payment.name}")
  private String PAYMENT_TOPIC_NAME;

  @Scheduled(fixedRate = 3 * 1000)
  public void scheduler() {
    log.info("scheduler::");
    paymentOutboxRepository.deleteCompleted();
  }

    @Scheduled(fixedRate = 5 * 1000)
    public void retryPaymentOutboxEvent() {
      log.info("retryOutboxEvent::");
      List<PaymentOutbox> notCompletedOutbox = paymentOutboxRepository.findByCompleted(false);
      for (PaymentOutbox outbox : notCompletedOutbox){
        ObjectMapper objectMapper = new ObjectMapper();
        PaymentKafkaMessage paymentMessage = null;
        try {
          paymentMessage = objectMapper.readValue(outbox.getMessage(), PaymentKafkaMessage.class);
          Reservation reservation = reservationService.getById(paymentMessage.getReservationId());

          if (reservation.getStatus() == ReservationStatus.PAID){
            paymentKafkaMessageProducer.send(outbox.getMessage());
          }
          tokenService.expireToken(paymentMessage.getUserId(), paymentMessage.getAccessKey());
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
    }
}