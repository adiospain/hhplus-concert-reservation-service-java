package io.hhplus.concert_reservation_service_java.presentation.controller;

import io.hhplus.concert_reservation_service_java.presentation.dto.req.reserveSeatAPIRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

  @PostMapping
  public ResponseEntity<Boolean> reserveSeat(
      @RequestHeader("token") String token,
      @RequestBody reserveSeatAPIRequest request) {

    boolean status = true;
    return ResponseEntity.ok(status);
  }
}
