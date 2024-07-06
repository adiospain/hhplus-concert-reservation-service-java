package io.hhplus.concert_reservation_service_java.presentation.controller;

import io.hhplus.concert_reservation_service_java.presentation.dto.req.CreatePaymentAPIRequest;
import io.hhplus.concert_reservation_service_java.presentation.dto.res.CreatePaymentAPIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
  @PostMapping
  public ResponseEntity<CreatePaymentAPIResponse> createPayment(
      @RequestParam Long concertId,
      @RequestParam Long concertScheduleId,
      @RequestParam Long seatId,
      @RequestBody CreatePaymentAPIRequest request) {
    CreatePaymentAPIResponse response = new CreatePaymentAPIResponse(3L, 3, 54000, 400);
    return ResponseEntity.ok(response);
  }
}
