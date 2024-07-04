package io.hhplus.concert_reservation_service_java.presentation.controller;

import io.hhplus.concert_reservation_service_java.presentation.dto.ConcertDTO;
import io.hhplus.concert_reservation_service_java.presentation.dto.ConcertScheduleDTO;
import io.hhplus.concert_reservation_service_java.presentation.dto.SeatDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {
  @GetMapping
  public ResponseEntity<List<ConcertDTO>> getConcerts(
      @RequestParam(required = false, defaultValue = "true") boolean available,
      @RequestParam(required = false, defaultValue = "1") int pageNum,
      @RequestParam(required = false, defaultValue = "10") int pageSize) {
    ConcertDTO concert1 = new ConcertDTO();
    ConcertDTO concert2 = new ConcertDTO();
    List<ConcertDTO> concerts = new ArrayList<>();
    concerts.add(concert1);
    concerts.add(concert2);
    return ResponseEntity.ok(concerts);
  }

  @GetMapping("/{concertId}")
  public ResponseEntity<ConcertDTO> getConcertDetail(
      @PathVariable Long concertId) {
    // Find concert by concertId
    ConcertDTO concert1 = new ConcertDTO();
    ConcertDTO concert2 = new ConcertDTO();
    List<ConcertDTO> concerts = new ArrayList<>();
    concerts.add(concert1);
    concerts.add(concert2);
    for (ConcertDTO concert : concerts) {
      if (concert.getId() == concertId) {
        return ResponseEntity.ok(concert);
      }
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/{concertId}/schedules")
  public ResponseEntity<List<ConcertScheduleDTO>> getConcertDate(
      @PathVariable Long concertId,
      @RequestParam(required = false, defaultValue = "true") boolean available) {
    // Find concert by concertId
    ConcertDTO concert1 = new ConcertDTO();
    ConcertDTO concert2 = new ConcertDTO();
    List<ConcertDTO> concerts = new ArrayList<>();
    concerts.add(concert1);
    concerts.add(concert2);
    for (ConcertDTO concert : concerts) {
      if (concert.getId()==concertId) {
        return ResponseEntity.ok(concert.getConcertSchedule());
      }
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/{concertId}/schedules/{concertScheduleId}/seats")
  public ResponseEntity<List<SeatDTO>> getConcertSeat(
      @PathVariable Long concertId,
      @PathVariable Long concertScheduleId,
      @RequestParam(required = false, defaultValue = "true") boolean available) {
    // Mock data for seats
    List<SeatDTO> seats = new ArrayList<>();
    seats.add(new SeatDTO(1L, "A1", 50));
    seats.add(new SeatDTO(2L, "A2", 40));
    seats.add(new SeatDTO(3L, "B1", 45));

    return ResponseEntity.ok(seats);
  }
}
