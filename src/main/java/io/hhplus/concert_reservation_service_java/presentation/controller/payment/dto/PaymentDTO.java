package io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PaymentDTO {
  private long id;
  private long reservationId;
  private int price;
  private LocalDateTime createdAt;
}
