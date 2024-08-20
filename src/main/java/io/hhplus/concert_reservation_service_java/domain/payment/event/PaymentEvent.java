package io.hhplus.concert_reservation_service_java.domain.payment.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.hhplus.concert_reservation_service_java.domain.common.event.CustomEvent;
import lombok.Getter;

@Getter
public class PaymentEvent implements CustomEvent {
  private long reservationId;
  private int reservedPrice;
  private long userId;
  //private long paymentId;
  private String accessKey;
  private String message;

  public PaymentEvent(Long reservationId, int reservedPrice, Long userId, String accessKey) {
    this.reservationId = reservationId;
    this.reservedPrice = reservedPrice;
    this.userId = userId;
    this.accessKey = accessKey;
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