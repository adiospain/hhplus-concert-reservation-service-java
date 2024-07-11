package io.hhplus.concert_reservation_service_java.presentation.controller.reserver;

import io.hhplus.concert_reservation_service_java.application.reserver.port.in.ChargePointCommand;
import io.hhplus.concert_reservation_service_java.application.reserver.port.in.GetPointCommand;

import io.hhplus.concert_reservation_service_java.application.token.port.in.IssueTokenUseCommand;

import io.hhplus.concert_reservation_service_java.domain.user.ChargePointUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.IssueTokenUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.GetPointUseCase;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.TokenDTO;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.req.ChargePointAPIRequest;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.res.ChargePointAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.res.IssueTokenAPIResponse;
import io.hhplus.concert_reservation_service_java.presentation.controller.reserver.dto.res.GetPointAPIResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class ReserverController {

  private final IssueTokenUseCase issueTokenUseCase;
  private final GetPointUseCase getPointUseCase;
  private final ChargePointUseCase chargePointUseCase;

  @PostMapping("/{userId}/token")
  @Operation(summary = "유저 토큰 발급", description = "지정된 사용자 ID에 대한 새로운 토큰을 발급합니다. 이 토큰은 대기열 관리 및 서비스 접근 권한 부여에 사용됩니다.")
  public ResponseEntity<IssueTokenAPIResponse> issueToken (@PathVariable long userId){
    IssueTokenUseCommand command = IssueTokenUseCommand.builder()
        .userId(userId)
        .build();
    TokenDTO token = issueTokenUseCase.execute(command);
    return ResponseEntity.ok(IssueTokenAPIResponse.from(token));
  }

  @GetMapping("/{userId}/point")
  @Operation(summary = "유저 잔액 조회", description = "지정된 사용자 ID의 현재 포인트 잔액을 조회합니다.")
  public ResponseEntity<GetPointAPIResponse> getPoint (@PathVariable long userId){
    GetPointCommand command = GetPointCommand.builder()
        .userId(userId)
        .build();
    int point = getPointUseCase.execute(command);
    return ResponseEntity.ok(GetPointAPIResponse.from(point));
  }

  @PatchMapping("/{userId}/charge")
  @Operation(summary = "유저 잔액 충전", description = "지정된 사용자 ID의 계정에 포인트를 충전합니다. 충전 후 업데이트된 총 포인트 잔액을 반환합니다.")
  public ResponseEntity<ChargePointAPIResponse> chargePoint (@PathVariable long userId,
      @RequestBody ChargePointAPIRequest request){

    ChargePointCommand command = ChargePointCommand.builder()
        .userId(userId)
        .amount(request.amount()).build();

    int point = chargePointUseCase.execute(command);

    return ResponseEntity.ok(ChargePointAPIResponse.from(point));
  }
}
