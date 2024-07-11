package io.hhplus.concert_reservation_service_java.exception;


import static org.springframework.http.HttpStatus.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  //400
  NOT_ENOUGH_POINT(BAD_REQUEST, "포인트가 부족 합니다."),
  INVALID_RESERVATION_STATUS(BAD_REQUEST, "결제할 수 없는 예약 상태입니다."),
  //401
  INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰 입니다."),
  EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰 입니다."),

  //404
  RESERVER_NOT_FOUND(NOT_FOUND, "예약자를 찾을 수 없습니다"),
  CONCERT_NOT_FOUND(NOT_FOUND, "콘서트를 찾을 수 없습니다"),
  CONCERT_SCHEDULE_NOT_FOUND(NOT_FOUND, "콘서트 날짜를 찾을 수 없습니다"),
  CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND(NOT_FOUND, "콘서트 날짜/좌석을 찾을 수 없습니다"),
  RESERVATION_NOT_FOUND(NOT_FOUND, "예약을 찾을 수 없습니다"),
  PAYMENT_NOT_FOUND(NOT_FOUND, "결제 정보를 찾을 수 없습니다."),

  //409
  ALREADY_RESERVED(CONFLICT, "해당 좌석은 이미 예약 되었습니다"),
  CONCERT_SCHEDULE_FULL(CONFLICT, "콘서트 날짜의 예약 인원이 다 찼습니다."),

  //500
  RESERVATION_FAILED(INTERNAL_SERVER_ERROR,"예약 중 오류가 발생했습니다"),
  AVAILABLE_SEAT_FAILED(INTERNAL_SERVER_ERROR, "예약 가능한 좌석을 찾는 중 오류가 발생했습니다"),

  UNSPECIFIED_FAIL(INTERNAL_SERVER_ERROR, "정의 되지 않은 에러 입니다.");
  private final HttpStatus httpStatus;
  private final String message;

}