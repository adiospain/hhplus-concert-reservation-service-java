package io.hhplus.concert_reservation_service_java.presentation.controller.concertSchedule;


import io.hhplus.concert_reservation_service_java.application.concertSchedule.port.in.GetConcertScheduleCommand;

import io.hhplus.concert_reservation_service_java.application.seat.port.in.GetSeatCommand;
import io.hhplus.concert_reservation_service_java.domain.seat.GetSeatUseCae;

import io.hhplus.concert_reservation_service_java.presentation.controller.concert.dto.res.GetConcertScheduleAPIRespose;

import io.hhplus.concert_reservation_service_java.presentation.controller.concertSchedule.dto.res.GetSeatAPIRespose;
import io.hhplus.concert_reservation_service_java.presentation.dto.SeatDTO;
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
@RequestMapping("/api/schedules")
public class ConcertScheduleController {

  private final GetSeatUseCae getSeatUseCae;

  @GetMapping("/{concertScheduleId}/seats")
  public ResponseEntity<GetSeatAPIRespose> getSeat(
      @PathVariable Long concertScheduleId,
      @RequestParam(required = false, defaultValue = "true") boolean available) {

    GetSeatCommand command = GetSeatCommand.builder()
        .concertScheduleId(concertScheduleId)
        .available(available)
        .build();
    List<SeatDTO> result = getSeatUseCae.execute(command);
    return ResponseEntity.ok(GetSeatAPIRespose.from(result));
  }
}
