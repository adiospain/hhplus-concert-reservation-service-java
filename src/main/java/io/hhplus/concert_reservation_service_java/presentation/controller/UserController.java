package io.hhplus.concert_reservation_service_java.presentation.controller;

import io.hhplus.concert_reservation_service_java.presentation.dto.res.ChargePointAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.dto.res.IssueTokenAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.dto.res.GetPointAPIResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  @PostMapping("/{userId}/token")
  public ResponseEntity<IssueTokenAPIResponse> issueToken (@PathVariable long userId){
    IssueTokenAPIResponse mockResponse = new IssueTokenAPIResponse(
        2L,
        LocalDateTime.now().plusMinutes(5)
    );
    return ResponseEntity.ok(mockResponse);
  }

  @GetMapping("/{userId}/point")
  public ResponseEntity<GetPointAPIResponse> getPoint (@PathVariable long userId){
    GetPointAPIResponse mockResponse = new GetPointAPIResponse(
        10000
    );
    return ResponseEntity.ok(mockResponse);
  }

  @PatchMapping("/{userId}/charge")
  public ResponseEntity<ChargePointAPIResponse> chargePoint (@PathVariable long userId){
    ChargePointAPIResponse mockResponse = new ChargePointAPIResponse(
        10000,
        true
    );
    return ResponseEntity.ok(mockResponse);
  }
}
