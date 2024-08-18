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
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentEvent implements CustomEvent {
  private long reservationId;
  private int reservedPrice;
  private long userId;
  //private long paymentId;
  private String accessKey;
  private String message;





//  public PaymentEvent(Long reservationId, Long userId, Long paymentId, String accessKey) {
//    this.reservationId = reservationId;
//    this.userId = userId;
//    this.paymentId = paymentId;
//    this.accessKey = accessKey;
//  }

  public PaymentEvent(Long reservationId, int reservedPrice, Long userId, String accessKey) {
    this.reservationId = reservationId;
    this.reservedPrice = reservedPrice;
    this.userId = userId;
    this.accessKey = accessKey;
  }

  @Override
  public void createOutboxMessage() {
    String message = createMessageJson();
    if (message != null) {
      this.message = message;
    }
  }

  @Override
  public void createKafkaMessage() {
    String message = createMessageJson();
    if (message != null) {
      this.message = message;
    }
  }

  private String createMessageJson() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    try {
        return objectMapper.writeValueAsString(new Object() {
          public final Long reservationId = PaymentEvent.this.reservationId;
          public final Integer reservedPrice = PaymentEvent.this.reservedPrice;
          public final Long userId = PaymentEvent.this.userId;
          public final String accessKey = PaymentEvent.this.accessKey;
        });
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }
}