package io.hhplus.concert_reservation_service_java.domain.payment.application.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PaymentDomain {
  private long id;
  private long reservationId;
  private int price;
  private int pointAfter;
  private LocalDateTime createdAt;

}
