package io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.res;

public record ChargePointAPIResponse (int point){

  public static ChargePointAPIResponse from(int point) {
    return new ChargePointAPIResponse(point);
  }
}
