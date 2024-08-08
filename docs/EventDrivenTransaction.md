# 이벤트 주도 설계와 인덱싱을 통한 DB 읽기 성능 최적화

이벤트 주도 설계를 통해 트랜잭션 범위를 축소하면
각 작은 트랜잭션에서 수행되는 DB 작업의 효율성이 더욱 중요해집니다.
적절한 인덱싱을 통해 이러한 트랜잭션의 읽기 성능을 향상 시키고자 합니다.

## 트랜잭션 범위
- 트랜잭션의 범위와 소요시간이 커지게 되면 DB 커넥션을 오래 지속하게 되어 시스템 성능 저하를 일으킵니다.
  - 트랜잭션이 중도 실패할 경우 이전에 성공한 트랜잭션을 복구하거나 보상 작업을 수행해야 합니다.
  - 긴 트랜잭션을 짧은 단위로 분리하기 위해 `이벤트 주도 설계`가 필요합니다.

## 이벤트 주도 설계
- 트랜잭션 큰 작업을 작은 단위로 분리하여 비동기적으로 처리하여 `트랜잭션 범위`를 줄입니다.
  - 잘게 나눠진 트랜잭션 단위를 더욱 극대화하기 위해 `인덱싱`을 통한 빠른 데이터 읽기 성능 향상이 필요합니다.

## 인덱싱
효과적인 인덱싱을 위해 다음 사항들을 고려합니다.
- **데이터 중복이 적은 컬럼 선택**: 유니크한 값을 가진 컬럼에 인덱스 적용
  - **데이터 변경이 적은 컬럼 우선**: 삽입, 수정이 빈번하지 않은 컬럼 선택
  - **자주 조회되는 컬럼 우선**: WHERE 절에 자주 사용되는 컬럼에 인덱스 적용
  - **적정 수의 인덱스 유지**: 테이블당 3~4개의 인덱스로 제한하여 오버헤드 방지

인덱싱은 별도의 데이터 구조로 저장되어 추가 공간을 차지 합니다.
INSERT, UPDATE, DELETE 작업 시 인덱스도 함께 수정 해야 하므로 쓰기 작업의 성능이 저하됩니다.

따라서 인덱싱은 데이터의 특성, 쿼리 패턴, 시스템 리소스 등을 종합적으로 고려하여
적절한 설정으로 데이터베이스의 성능을 향상시켜야 합니다.

## CRUD 매트릭스

시스템의 기능 요구사항을 명확히 하고 모든 Entity에 대해 어떤 작업이 자주 수행되는지 파악하기 위해
CRUD 매트릭스를 작성합니다.

<table> 

<tr> <th colspan="20">범례: 단순CRUD (단), 복합키 (복), 낙관적락(낙), 비관적락 (비), 레디스 캐시 (캐), 레디스 분산락 (분) </th></tr> 
<tr class="separator">
<td colspan="20"></td>
<tr> <th>기능</th> 
<th colspan="4">user</th> 

<th colspan="4">concert / concert_schedule</th> 
<th colspan="4">concert_schedule_seat</th> 
<th colspan="4">reservation(복합키)</th> 
<th colspan="3">payment</th> </tr>

<tr> <td></td> 
<td>C</td><td>R</td><td>U</td><td>D</td> 
<td>C</td><td>R</td><td>U</td><td>D</td> 
<td>C</td><td>R</td><td>U</td><td>D</td> 
<td>C</td><td>R</td><td>U</td><td>D</td> 
<td>C</td><td>R</td><td>U</td> </tr>

<tr> 
<td>유저 포인트 조회</td> 
<td></td><td>비</td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td> 
</tr> 

<tr> 
<td>유저 포인트 사용/충전</td> 
<td></td><td>비</td><td>비</td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td> 
</tr> 

<tr class="separator">
<td colspan="20"></td>

<tr> 
<td>콘서트 목록 조회</td> 
<td></td><td></td><td></td><td></td> 
<td></td><td>캐+분</td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td>
</tr> 

<tr> 
<td>콘서트 날짜 조회</td> 
<td></td><td></td><td></td><td></td> 
<td></td><td>캐+분</td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td> 
</tr> 

<tr> 
<td>예약 가능 콘서트 좌석 조회</td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td>캐</td><td></td><td></td> 
<td></td><td>복</td><td></td><td></td> 
<td></td><td></td><td></td> 
</tr> 

<tr class="separator">
<td colspan="20"></td>

<tr> 
<td>예약</td> 
<td></td><td>비</td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td>분</td><td></td><td></td> 
<td>복</td><td></td><td></td><td></td> 
<td></td><td></td><td></td> 
</tr> 

<tr> 
<td>결제</td> 
<td></td><td>비</td><td>비</td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td></td><td></td><td></td> 
<td></td><td>복</td><td></td><td></td> 
<td>O</td><td></td><td></td> 
</tr> 

</table>

### 분석
- 유저 포인트 조회/사용/충전
    - 유저 포인트는 각 사용자 고유의 id만으로 빠른 조회가 가능하므로 별도의 인덱싱이 필요 없습니다.
    - 유저 포인트는 자주 변경되는 데이터이기 때문에 인덱스 갱신 비용이 증가하여 성능저하가 우려됩니다.
        - 포인트 사용 내역을 별도 테이블로 관리하여 유저 테이블 부하 감소를 고려할 수 있습니다.

  - 콘서트 목록 / 날짜 / 좌석 조회
      - 콘서트 관련 테이블은 한 번 등록되면 자주 변경되지 않습니다.
      - 인덱스 유지 비용이 상대적으로 낮으므로 **적극적으로 인덱싱**을 적용할 수 있습니다.

  - 예약
      - 이미 복합키를 사용하여 조회 성능이 일정 수준 최적화 되어 있습니다.
          - 예약상태에 따른 `status` 필드 인덱스 추가를 검토 합니다.
          - 사용자별 예약 조회를 위한 `userId` 필드 인덱스 추가도 고려할 수 있습니다.

  - 결제
      - `userId`, `concertScheduleId`, `seatId`를 인덱싱하도록 고려할 수 있습니다.
      - 인덱스 추가로 인한 저장 공간이 증가하며 최적의 조건을 찾아야 합니다.

결제 로직과 예약 로직 순으로
먼저 인덱싱 개선 후 이벤트 주도 설계를 해보도록 합니다.

### 최적화 해보기
#### 1단계 : 결제 로직

```java
@Override
  @Transactional
  public PaymentDomain execute(CreatePaymentCommand command) {
  
      //1. 예약 정보를 조회 검증한다.
      Reservation reservation = reservationService.getReservationToPay(command.getReservationId());

      //2. 유저 조회 검증과 동시에 결제 차감한다.
      User user = userService.usePoint(command.getUserId(), reservation.getReservedPrice());

      //3. 결제를 생성한다.
      Payment payment = paymentService.createPayment(
          user.getId(),
          reservation.getConcertScheduleId(), reservation.getSeatId(),
          reservation.getReservedPrice());

      //4. 좌석의 상태를 결제 완료 상태로 변경한다.
      reservationService.saveToPay(reservation);
      
      //5. 활성화 토큰을 만료 시킨다.
      tokenService.expireToken(command.getUserId(), command.getAccessKey());
      
      //6. 외부 API 호출한다.
      dataPaltformClient.send("PAYMENT_CREATED", payment);
      return paymentMapper.of(payment, reservation, user);
  }
```

##### 인덱싱 개선 :
1. reservation 테이블 인덱스 최적화 :
    - 복합키 (`seatId` & `concertScheduleId`)로 인덱스 했습니다.
   2. user/reserver 테이블 인덱스 최적화 :
       - 유저 포인트가 자주 변경된다면, 인덱스도 그만큼 자주 갱신되어 쓰기 성능이 저하될 수 있습니다.
       - 인덱스 설정을 하지 않는 것이 바람직하다고 판단됩니다.

##### 트랜잭션 개선 :
트랜잭션 마지막에 외부 API 호출 기능을 추가 한다면, 외부 API의 결과가 내 서비스와 강결합되지 않도록
도메인간의 책임 분리와 이벤트 기반 로직 처리가 필요합니다.

```java
@Transactional
  public PaymentDomain execute(CreatePaymentCommand command) {
  
      //1. 예약 정보를 조회 검증한다.
      Reservation reservation = reservationService.getReservationToPay(command.getReservationId());

      //2. 유저 조회 검증과 동시에 결제 차감한다.
      User user = userService.usePoint(command.getUserId(), reservation.getReservedPrice());

      //3. 결제를 생성한다.
      Payment payment = paymentService.createPayment(
          user.getId(),
          reservation.getConcertScheduleId(), reservation.getSeatId(),
          reservation.getReservedPrice());

      //4. 좌석의 상태를 결제 완료 상태로 변경한다.
      reservationService.saveToPay(reservation);
      
      //5. 활성화 토큰을 만료 시킨다.
      tokenService.expireToken(command.getUserId(), command.getAccessKey());
      
      //6. 외부 API 호출한다.
      eventPublisher.publish(new CreatePayment(payment));
      return paymentMapper.of(payment, reservation, user);
  }
```


인덱싱 외에 table 카디널리티 개선

Step 16
결제 or 좌석예약

시간 체크 : 분당 쿼리를 쏠 수 있는 클라이언트

쿼리 DSL 필요하겠구나

예약 -> 결제시 예약 테이블의 생성 시간


인메모리 대신 mysql