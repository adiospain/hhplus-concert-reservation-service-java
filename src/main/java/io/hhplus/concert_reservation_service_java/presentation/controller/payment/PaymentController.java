package io.hhplus.concert_reservation_service_java.presentation.controller.payment;

import io.hhplus.concert_reservation_service_java.application.payment.port.in.CreatePaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.payment.CreatePaymentUseCase;
import io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.PaymentDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.req.CreatePaymentAPIRequest;
import io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.res.CreatePaymentAPIResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

  private final CreatePaymentUseCase createPaymentUseCase;

  @PostMapping
  @Operation(summary = "결제" , description = "새로운 결제를 생성합니다. 예약자 ID와 예약 ID를 받아 결제를 처리하고 결제 정보를 반환합니다.")
  public ResponseEntity<CreatePaymentAPIResponse> createPayment(
      @RequestBody CreatePaymentAPIRequest request) {
    CreatePaymentCommand command = CreatePaymentCommand.builder()
        .reserverId(request.reserverId())
        .reservationId(request.reservationId())
        .build();
    PaymentDTO payment = createPaymentUseCase.execute(command);
    return ResponseEntity.ok(CreatePaymentAPIResponse.from(payment));
  }
}
