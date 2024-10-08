package io.hhplus.concert_reservation_service_java.presentation.controller.user;

import io.hhplus.concert_reservation_service_java.domain.payment.GetPaymentUseCase;
import io.hhplus.concert_reservation_service_java.domain.payment.application.model.PaymentDomain;
import io.hhplus.concert_reservation_service_java.domain.payment.application.port.in.GetPaymentCommand;
import io.hhplus.concert_reservation_service_java.domain.user.UsePointUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.CreateUserCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.UsePointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.GetPointCommand;

import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.IssueTokenCommand;

import io.hhplus.concert_reservation_service_java.domain.token.application.port.in.GetTokenCommand;
import io.hhplus.concert_reservation_service_java.domain.user.ChargePointUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.GetTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.GetPointUseCase;
import io.hhplus.concert_reservation_service_java.domain.token.application.model.TokenDomain;
import io.hhplus.concert_reservation_service_java.domain.user.CreateUserUseCase;
import io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.res.GetPaymentAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.req.ChargePointAPIRequest;
import io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.req.UsePointAPIRequest;
import io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.res.ChargePointAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.res.CreateUserAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.res.GetTokenAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.res.IssueTokenAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.res.GetPointAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.user.dto.res.UsePointAPIResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final CreateUserUseCase createUserUseCase;

  private final IssueTokenUseCase issueTokenUseCase;
  private final GetTokenUseCase getTokenUseCase;
  private final GetPointUseCase getPointUseCase;
  private final ChargePointUseCase chargePointUseCase;
  private final UsePointUseCase usePointUseCase;

  private final GetPaymentUseCase getPaymentUseCase;

  @PostMapping("/{userId}/token")
  @Operation(summary = "유저 토큰 발급" , description = "지정된 사용자 ID에 대한 새로운 토큰을 발급합니다. 이 토큰은 대기열 관리 및 서비스 접근 권한 부여에 사용됩니다.")
  public ResponseEntity<IssueTokenAPIResponse> issueToken (
      @RequestHeader(value = "Authorization", required =false) String accessKey,
      @PathVariable long userId){
    IssueTokenCommand command = IssueTokenCommand.builder()
        .userId(userId)
        .build();
    TokenDomain token = issueTokenUseCase.execute(command);
    return ResponseEntity.ok(IssueTokenAPIResponse.from(token));
  }

  @GetMapping("/{userId}/token")
  @Operation(summary = "유저 토큰 조회" , description = "현재 대기열 순번과 토큰 잔여시간을 제공됩니다.")
  public ResponseEntity<GetTokenAPIResponse> getToken (
      @RequestHeader(value = "Authorization", required =false) String accessKey,
      @PathVariable long userId){
    GetTokenCommand command = GetTokenCommand.builder()
        .accessKey(accessKey)
        .userId(userId)
        .build();
    TokenDomain token = getTokenUseCase.execute(command);
    return ResponseEntity.ok(GetTokenAPIResponse.from(token));
  }

  @GetMapping("/{userId}/point")
  @Operation(summary = "유저 잔액 조회" , description = "지정된 사용자 ID의 현재 포인트 잔액을 조회합니다.")
  public ResponseEntity<GetPointAPIResponse> getPoint (
      @RequestHeader(value = "Authorization", required =false) String accessKey,
      @PathVariable long userId){
    GetPointCommand command = GetPointCommand.builder()
        .reserverId(userId)
        .build();
    int point = getPointUseCase.execute(command);
    return ResponseEntity.ok(GetPointAPIResponse.from(point));
  }

  @PatchMapping("/{userId}/charge")
  @Operation(summary = "유저 잔액 충전" , description = "지정된 사용자 ID의 계정에 포인트를 충전합니다. 충전 후 업데이트된 총 포인트 잔액을 반환합니다.")
  public ResponseEntity<ChargePointAPIResponse> chargePoint (
      @RequestHeader(value = "Authorization", required =false) String accessKey,
      @PathVariable long userId,
      @RequestBody ChargePointAPIRequest request){

    ChargePointCommand command = ChargePointCommand.builder()
        .userId(userId)
        .amount(request.amount()).build();

    int point = chargePointUseCase.execute(command);

    return ResponseEntity.ok(ChargePointAPIResponse.from(point));
  }

  @PatchMapping("/{userId}/use")
  @Operation(summary = "유저 잔액 사용" , description = "지정된 사용자 ID의 계정에 포인트를 충전합니다. 충전 후 업데이트된 총 포인트 잔액을 반환합니다.")
  public ResponseEntity<UsePointAPIResponse> usePoint (
      @RequestHeader(value = "Authorization", required =false) String accessKey,
      @PathVariable long userId,
      @RequestBody UsePointAPIRequest request){

    UsePointCommand command = UsePointCommand.builder()
        .userId(userId)
        .amount(request.amount()).build();

    int point = usePointUseCase.execute(command);

    return ResponseEntity.ok(UsePointAPIResponse.from(point));
  }

  @GetMapping("/{userId}/payments")
  @Operation(summary = "결제 내역 조회" , description = "결제 내역을 조회 합니다. 예약자 ID를 받아 결제 내역를 반환합니다.")
  public ResponseEntity<GetPaymentAPIResponse> getPayment(
      @RequestHeader(value = "Authorization", required =false) String accessKey,
      @PathVariable long userId) {
    GetPaymentCommand command = GetPaymentCommand.builder()
        .userId(userId)
        .build();
    List<PaymentDomain> payments = getPaymentUseCase.execute(command);

    return ResponseEntity.ok(GetPaymentAPIResponse.from(payments));
  }

  @PostMapping
  @Operation(summary = "유저 생성" , description = "새로운 유저를 생성합니다.")
  public ResponseEntity<CreateUserAPIResponse> createUser(
      @RequestHeader(value = "Authorization", required =false) String accessKey) {
    CreateUserCommand command = CreateUserCommand.builder()
        .userId(0)
        .build();

    long createdUserId = createUserUseCase.execute(command);

    return ResponseEntity.status(HttpStatus.CREATED).body(CreateUserAPIResponse.from(createdUserId));
  }
}
