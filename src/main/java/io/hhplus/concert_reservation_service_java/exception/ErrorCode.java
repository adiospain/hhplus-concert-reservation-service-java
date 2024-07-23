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
  INVALID_AMOUNT(BAD_REQUEST, "충전할 수 없는 금액 입니다."),
  INVALID_RESERVATION_STATUS(BAD_REQUEST, "결제할 수 없는 예약 상태입니다."),
  INVALID_CONCERT(BAD_REQUEST, "유효하지 않은 콘서트ID 입니다."),
  EXPIRED_RESERVATION(BAD_REQUEST, "임시 배정이 만료되었습니다."),
  //401
  NOT_YET(UNAUTHORIZED, "아직 대기열 순번이 아닙니다"),
  NO_TOKEN(UNAUTHORIZED, "토큰이 없습니다."),
  INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰 입니다."),
  EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰 입니다."),

  //404
  USER_NOT_FOUND(NOT_FOUND, "사용자를 찾을 수 없습니다"),
  CONCERT_NOT_FOUND(NOT_FOUND, "콘서트를 찾을 수 없습니다"),
  CONCERT_SCHEDULE_NOT_FOUND(NOT_FOUND, "콘서트 날짜를 찾을 수 없습니다"),
  CONCERT_SCHEDULE_OR_SEAT_NOT_FOUND(NOT_FOUND, "콘서트 날짜/좌석을 찾을 수 없습니다"),
  RESERVATION_NOT_FOUND(NOT_FOUND, "예약을 찾을 수 없습니다"),
  PAYMENT_NOT_FOUND(NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
  TOKEN_NOT_FOUND(NOT_FOUND, "토큰이 존재하지 않습니다"),
  DISCONNECTED_TOKEN_NOT_FOUND(NOT_FOUND, "연결이 끊긴 토큰을 찾을 수 없습니다"),

  //409
  ALREADY_RESERVED(CONFLICT, "해당 좌석은 이미 예약 되었습니다"),
  CONCERT_SCHEDULE_FULL(CONFLICT, "콘서트 날짜의 예약 인원이 다 찼습니다."),
  WAITING_CONTINUE(CONFLICT, "아직 예약 순번이 아닙니다."),
  TOKEN_AND_USER_NOT_MATCHED(CONFLICT, "유저와 토큰이 일치하지 않습니다."),
  RESERVATION_AND_USER_NOT_MATCHED(CONFLICT, "유저와 예약이 일치하지 않습니다."),

  //500
  RESERVATION_FAILED(INTERNAL_SERVER_ERROR,"예약 중 오류가 발생했습니다"),
  AVAILABLE_SEAT_FAILED(INTERNAL_SERVER_ERROR, "예약 가능한 좌석을 찾는 중 오류가 발생했습니다"),
  INTEGER_OVERFLOW(INTERNAL_SERVER_ERROR, "integer 오버플로우 발생했습니다."),

  CONCURRENT_LOCK (INTERNAL_SERVER_ERROR, "동시에 데이터를 수정하여 작업을 완료할 수 없습니다."),
  OPERATION_INTERRUPTED(INTERNAL_SERVER_ERROR, "작업 중단"),

  USECASE(INTERNAL_SERVER_ERROR, "유즈케이스에서 오류가 발생했습니다"),
  MAPPER(INTERNAL_SERVER_ERROR, "매퍼에서 오류가 발생했습니다."),
  SERVICE(INTERNAL_SERVER_ERROR, "서비스에서 오류가 발생했습니다"),
  DATABASE(INTERNAL_SERVER_ERROR, "데이터베이스에서 오류가 발생했습니다."),
  OBJECT_CANNOT_BE_NULL(INTERNAL_SERVER_ERROR, "객체가 null일 수 없습니다."),
  TIME_PARADOX(INTERNAL_SERVER_ERROR, "업데이트 되는 시간은 현재 시간보다 줄어들 수 없습니다. 업데이트 시간은 현재 생성 시간보다 이후여야 합니다."),

  UNSPECIFIED_FAIL(INTERNAL_SERVER_ERROR, "정의 되지 않은 에러 입니다.");
  private final HttpStatus httpStatus;
  private final String message;

}