package io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.res;

public record UsePointAPIResponse (int point){
  public static UsePointAPIResponse from(int point) {
    return new UsePointAPIResponse(point);
  }
}
