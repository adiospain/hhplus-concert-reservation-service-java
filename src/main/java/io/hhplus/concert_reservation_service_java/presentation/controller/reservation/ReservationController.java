package io.hhplus.concert_reservation_service_java.presentation.controller.reservation;

import io.hhplus.concert_reservation_service_java.domain.reservation.GetReservationUseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.port.in.GetReservationCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreateReservationCommand;
import io.hhplus.concert_reservation_service_java.domain.reservation.CreateReservationUseCase;
import io.hhplus.concert_reservation_service_java.domain.reservation.application.model.ReservationDomain;
import io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.req.CreateReservationAPIRequest;
import io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.req.GetReservationAPIRequest;
import io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.res.CreateReservationAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.res.GetReservationAPIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

  private final CreateReservationUseCase createReservationUseCase;
  private final GetReservationUseCase getReservationUseCase;

  @GetMapping("/{reservationId}")
  @Operation(summary = "좌석 예약 조회" , description = "예약 조회")
  public ResponseEntity<GetReservationAPIResponse> getReservation(
      @PathVariable long reservationId) {

    GetReservationCommand command = GetReservationCommand.builder()
        .reservationId(reservationId)
        .build();

    ReservationDomain reservation = getReservationUseCase.execute(command);

    return ResponseEntity.ok(GetReservationAPIResponse.from(reservation));
  }

  @PostMapping
  @Operation(summary = "좌석 예약" , description = "콘서트 좌석을 예약하고, 유저에게 임시 배정 합니다.")
  @ApiResponse(responseCode = "200" , description = "예약 성공")
  @ApiResponse(responseCode = "409" , description = "이미 예약된 좌석")
  public ResponseEntity<CreateReservationAPIResponse> createReservation(
      @RequestHeader(value = "Authorization", required =false) String accessKey,
      @RequestBody CreateReservationAPIRequest request) {

    CreateReservationCommand command = CreateReservationCommand.builder()
        .accessKey(accessKey)
        .userId(request.userId())
        .concertScheduleId(request.concertScheduleId())
        .seatId(request.seatId())
        .build();

    ReservationDomain reservation = createReservationUseCase.execute(command);

    return ResponseEntity.ok(CreateReservationAPIResponse.from(reservation));
  }
}
