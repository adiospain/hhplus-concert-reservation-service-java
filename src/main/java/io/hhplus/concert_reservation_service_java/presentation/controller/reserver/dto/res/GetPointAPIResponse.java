package io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.res;

public record GetPointAPIResponse
    (int point){

  public static GetPointAPIResponse from(int point) {
    return new GetPointAPIResponse(point);
  }
}
