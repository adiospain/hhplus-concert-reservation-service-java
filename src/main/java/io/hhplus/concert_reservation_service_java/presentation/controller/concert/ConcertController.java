package io.hhplus.concert_reservation_service_java.presentation.controller.concert;

import io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in.GetAvailableConcertSchedulesCommand;
import io.hhplus.concert_reservation_service_java.application.concertScheduleSeat.port.in.GetAvailableSeatsCommand;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertDetailUseCase;
import io.hhplus.concert_reservation_service_java.domain.concert.GetConcertsUseCase;
import io.hhplus.concert_reservation_service_java.domain.concertSchedule.GetAvailableConcertSchedulesUseCase;
import io.hhplus.concert_reservation_service_java.domain.concertScheduleSeat.GetAvailableSeatsUseCae;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertDTO;
import io.hhplus.concert_reservation_service_java.application.concert.port.in.GetConcertDetailCommand;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleSeatDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res.GetConcertDetailAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res.GetConcertScheduleAPIRespose;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res.GetConcertsAPIResponse;

import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.ConcertScheduleDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res.GetSeatAPIRespose;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts")
public class ConcertController {

  private final GetConcertsUseCase getConcertsUseCase;
  private final GetConcertDetailUseCase getConcertDetailUseCase;
  private final GetAvailableConcertSchedulesUseCase getAvailableConcertSchedulesUseCase;
  private final GetAvailableSeatsUseCae getAvailableSeatsUseCae;

  @GetMapping
  @Operation(summary = "콘서트 목록 조회" , description = "DB에 존재하는 모든 콘서트 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  public ResponseEntity<GetConcertsAPIResponse> getConcerts(
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "10") int pageSize) {
      List<ConcertDTO> result = getConcertsUseCase.execute();
    return ResponseEntity.ok(GetConcertsAPIResponse.from(result, page, pageSize));
  }

  @GetMapping("/{concertId}")
  @Operation(summary = "콘서트 상세 조회" , description = "지정된 ID의 콘서트 상세 정보를 조회합니다.")
  public ResponseEntity<GetConcertDetailAPIResponse> getConcertDetail(
      @PathVariable Long concertId) {
    GetConcertDetailCommand command = GetConcertDetailCommand.builder()
        .concertId(concertId)
        .build();
    ConcertDTO result = getConcertDetailUseCase.execute(command);
    return ResponseEntity.ok(GetConcertDetailAPIResponse.from(result));
  }

  @GetMapping("/{concertId}/schedules/available")
  @Operation( summary = "콘서트 예약 가능 날짜 조회" , description = "지정된 콘서트의 예약 가능한 모든 날짜를 조회합니다.")
  public ResponseEntity<GetConcertScheduleAPIRespose> getAvailableConcertSchedules(
      @PathVariable Long concertId) {

    GetAvailableConcertSchedulesCommand command = GetAvailableConcertSchedulesCommand.builder()
        .concertId(concertId)
        .build();
    List<ConcertScheduleDTO> result = getAvailableConcertSchedulesUseCase.execute(command);
    return ResponseEntity.ok(GetConcertScheduleAPIRespose.from(result));
  }


  @GetMapping("/{concertId}/schedules/{concertScheduleId}/seats/available")
  @Operation(summary = "콘서트 예약 가능 좌석 조회" , description = "지정된 콘서트 일정에 대해 예약 가능한 모든 좌석을 조회합니다")
  public ResponseEntity<GetSeatAPIRespose> getAvailableSeats(
      @PathVariable Long concertId,
      @PathVariable Long concertScheduleId) {

    GetAvailableSeatsCommand command = GetAvailableSeatsCommand.builder()
        .concertId(concertId)
        .concertScheduleId(concertScheduleId)
        .build();
    List<ConcertScheduleSeatDTO> result = getAvailableSeatsUseCae.execute(command);
    return ResponseEntity.ok(GetSeatAPIRespose.from(result));
  }
}
