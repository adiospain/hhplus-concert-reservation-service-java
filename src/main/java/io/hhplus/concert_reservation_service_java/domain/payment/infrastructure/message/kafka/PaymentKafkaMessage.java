package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.kafka;

import io.hhplus.concert_reservation_service_java.domain.common.message.KafkaMessage;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentKafkaMessage implements KafkaMessage {
  private Long paymentId;
  private Long userId;
  private Long reservationId;
  private Long outboxId;
  private String accessKey;
}
