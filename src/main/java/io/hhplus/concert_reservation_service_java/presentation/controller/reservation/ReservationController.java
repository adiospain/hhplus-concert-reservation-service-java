package io.hhplus.concert_reservation_service_java.presentation.controller.reservation;

import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreateReservationCommand;
import io.hhplus.concert_reservation_service_java.domain.user.CreateReservationUseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;
import io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.req.CreateReservationAPIRequest;
import io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.res.CreateReservationAPIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

  private final CreateReservationUseCase createReservationUseCase;

  @PostMapping
  @Operation(summary = "좌석 예약" , description = "콘서트 좌석을 예약하고, 유저에게 임시 배정 합니다.")
  @ApiResponse(responseCode = "200" , description = "예약 성공")
  @ApiResponse(responseCode = "409" , description = "이미 예약된 좌석")
  public ResponseEntity<CreateReservationAPIResponse> createReservation(
      @RequestBody CreateReservationAPIRequest request) {

    CreateReservationCommand command = CreateReservationCommand.builder()
        .reserverId(request.userId())
        .concertScheduleId(request.concertScheduleId())
        .seatId(request.seatId())
        .build();

    ReservationDomain reservation = createReservationUseCase.execute(command);

    return ResponseEntity.ok(CreateReservationAPIResponse.from(reservation));
  }
}
