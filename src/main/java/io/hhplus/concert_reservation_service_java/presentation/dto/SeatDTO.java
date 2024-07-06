package io.hhplus.concert_reservation_service_java.presentation.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SeatDTO {
  private Long seatId;
  private String name;
  private int price;
}
