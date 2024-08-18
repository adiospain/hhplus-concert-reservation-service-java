package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class PaymentOutboxManagerImpl implements PaymentOutboxManager{

  private final PaymentOutboxRepository paymentOutboxRepository;

  private String createMessageJson(PaymentEvent event) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    try {
      return objectMapper.writeValueAsString(new Object() {
        public final Long reservationId = event.getReservationId();
        public final Integer reservedPrice = event.getReservedPrice();
        public final Long userId = event.getUserId();
        public final String accessKey = event.getAccessKey();
      });
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }
  }

  public PaymentOutbox createOutbox(PaymentEvent event){
    String message = createMessageJson(event);
    if (message != null) {
      PaymentOutbox paymentOutbox = PaymentOutbox.builder()
          .message(message)
          .build();
      return paymentOutbox;
    }
    return null;
  }

  @Override
  public PaymentOutbox create(PaymentEvent event) {
      PaymentOutbox paymentOutbox = createOutbox(event);
      return paymentOutboxRepository.save(paymentOutbox);
  }

  @Override
  public void markComplete(String message) {
    paymentOutboxRepository.markComplete(PaymentOutbox.getUUID(message));
  }
}
