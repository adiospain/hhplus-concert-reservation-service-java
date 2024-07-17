package io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.res;

public record GetPointAPIResponse
    (int point){

  public static GetPointAPIResponse from(int point) {
    return new GetPointAPIResponse(point);
  }
}
