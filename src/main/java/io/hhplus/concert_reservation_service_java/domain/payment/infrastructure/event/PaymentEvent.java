package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.hhplus.concert_reservation_service_java.domain.common.event.CustomEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentEvent implements CustomEvent {
  private long reservationId;
  private long userId;
  private long paymentId;
  private String accessKey;
  private String message;
  private PaymentOutbox paymentOutbox;





  public PaymentEvent(Long reservationId, Long userId, Long paymentId, String accessKey) {
    this.reservationId = reservationId;
    this.userId = userId;
    this.paymentId = paymentId;
    this.accessKey = accessKey;
  }

  @Override
  public void createOutboxMessage() {
    String message = createMessageJson(false);
    if (message != null) {
      this.message = message;
      this.paymentOutbox = PaymentOutbox.builder()
          .message(this.message)
          .build();
    }
  }

  @Override
  public void createKafkaMessage() {
    String message = createMessageJson(true);
    if (message != null) {
      this.message = message;
    }
  }

  private String createMessageJson(boolean includeOutboxId) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    try {
      if (includeOutboxId) {
        return objectMapper.writeValueAsString(new Object() {
          public final Long paymentId = PaymentEvent.this.paymentId;
          public final Long userId = PaymentEvent.this.userId;
          public final Long reservationId = PaymentEvent.this.reservationId;
          public final String accessKey = PaymentEvent.this.accessKey;
          public final Long outboxId = PaymentEvent.this.paymentOutbox.getId();
        });
      }
      else {
        return objectMapper.writeValueAsString(new Object() {
          public final Long paymentId = PaymentEvent.this.paymentId;
          public final Long userId = PaymentEvent.this.userId;
          public final Long reservationId = PaymentEvent.this.reservationId;
          public final String accessKey = PaymentEvent.this.accessKey;
        });
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }
}