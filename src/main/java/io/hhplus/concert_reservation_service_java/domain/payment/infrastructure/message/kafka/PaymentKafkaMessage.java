package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.message.kafka;

import io.hhplus.concert_reservation_service_java.domain.common.message.KafkaMessage;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEvent;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentKafkaMessage implements KafkaMessage {
  private Long reservationId;
  private Integer reservedPrice;
  private Long userId;
  private String accessKey;
}