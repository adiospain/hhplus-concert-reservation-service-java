package io.hhplus.concert_reservation_service_java.domain.user.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateReservationCommand {
  private String accessKey;
  private long userId;
  private long concertScheduleId;
  private long seatId;
}
