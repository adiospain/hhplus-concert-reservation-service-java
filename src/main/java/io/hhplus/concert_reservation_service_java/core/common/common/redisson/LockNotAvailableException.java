package io.hhplus.concert_reservation_service_java.core.common.common.redisson;


import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;

public class LockNotAvailableException extends CustomException {

  public LockNotAvailableException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}