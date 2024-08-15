package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.hhplus.concert_reservation_service_java.domain.common.event.CustomEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.repository.jpa.Payment;
import io.hhplus.concert_reservation_service_java.domain.reservation.infrastructure.jpa.Reservation;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentEvent implements CustomEvent {
  private long reservationId;
  private long userId;
  private long paymentId;
  private String accessKey;

  private PaymentOutbox paymentOutbox;

  public PaymentEvent(Long reservationId, Long userId, Long paymentId, String accessKey) {
    this.reservationId = reservationId;
    this.userId = userId;
    this.paymentId = paymentId;
    this.accessKey = accessKey;
  }

  @Override
  public void createOutboxMessage() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    try {
      // Create a JSON string with all three objects
      String message = objectMapper.writeValueAsString(new Object() {
        public final Long reservationId = PaymentEvent.this.reservationId;
        public final Long userId = PaymentEvent.this.userId;
        public final Long paymentId = PaymentEvent.this.paymentId;
      });
      this.paymentOutbox = paymentOutbox.builder()
          .message(message)
          .build();

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}