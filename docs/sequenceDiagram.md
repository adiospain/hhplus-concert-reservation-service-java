
  <details>
  <summary style="font-size: 1em; margin-left: 20px;">issueToken -유저 토큰 발급</summary>

  <!-- Here you can include the details of the issueToken -->

유저가 서비스 이용시 필요한 대기열 토큰을 발급 받는다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant UserService
    participant TokenService
  User ->>+ UserService: 유저 정보 조회
  break 유효하지 않는 유저
    UserService -->>- User: INVALID_USER
    note over UserService: exception
  end
    User ->>+ TokenService: 토큰 발급 요청
  note over TokenService: 토큰 생성
    TokenService ->>- User: 토큰 반환
```
</details>

<details>
  <summary style="font-size: 1em;margin-left: 20px;">getTokenDetail - 대기열 토큰 조회 </summary>

유저가 대기열의 대기순서 및 잔여시간을 확인한다.

- 기본적으로 폴링으로 대기열을 확인하지만, 다른 방안 (실시간)을 고려해본다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant DB
    participant Token
    
    User ->>+ Authorization: 대기열 정보 조회 요청
    Authorization ->>+ Token: 대기열 토큰 조회
    Token ->>- User: 대기열 토큰 정보 반환
```
</details>

<details>
  <summary style="font-size: 1em;margin-left: 20px;">getConcertDate - 콘서트 예약 가능 날짜 조회</summary>

유저가 예약 가능한 날짜 목록을 확인한다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant ConcertService
    User ->>+ Authorization: 대기열 검증 요청
  break 유효하지 않는 토큰 (만료, 사용자 불일치)
    Authorization -->> User: 예외처리
    note over Authorization: Bad Request
  end
    Authorization ->>- User: 대기열 반환
    
    User ->>+ ConcertService: 예약 가능 날짜 조회
    ConcertService ->>- User: 수용 인원이 양수인 날짜 반환
```

</details>
<details>
  <summary style="font-size: 1em;margin-left: 20px;">getConcertSeat - 콘서트 예약 가능 좌석 조회</summary>

유저가 예약 가능한 좌석 목록을 확인한다.
- 날짜 정보를 입력 받아 좌석 정보를 조회한다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant SeatService
    participant ReservationService
    User ->>+ Authorization: 대기열 검증 요청
    break 유효하지 않는 토큰 (만료, 사용자 불일치)
    Authorization -->> User: 예외처리
    note over Authorization: Bad Request
    end
  Authorization ->>- User: 대기열 반환
  User ->>+ SeatService: 날짜별 좌석 조회
  SeatService ->>+ ReservationService: 이미 배정 / 결제 완료된 좌석 조회
  ReservationService ->>- User: 배정 / 결제 되지 않은 좌석 반환 
  
```

</details>

<details>
  <summary style="font-size: 1em;margin-left: 20px;">reserveSeat - 좌석 예약</summary>

유저가 좌석 예약한다.
- 날짜와 좌석 정보를 입력 받아 좌석을 예약 처리 한다.
- 좌석 예약과 동시에 해당 좌석은 그 유저에게 임시 배정된다.
- 배정 시간 내에 결제가 완료되지 않으면 임시 배정은 해제 된다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant ReservationService
    participant ConcertService
    User ->>+ Authorization: 대기열 검증 요청
    break 유효하지 않는 토큰 (만료, 사용자 불일치)
      Authorization -->> User: 예외처리
      note over Authorization: Bad Request
    end
    User ->>+ ReservationService: 좌석 예약 요청
    note over ReservationService: 예약 생성
  break 이미 존재하는 예약 id (날짜, 좌석)
    ReservationService -->>+ User: 예외처리
    note over ReservationService: Bad Request
  end
    ReservationService ->>+ ConcertService: 예약된 콘서트 날짜의 수용 인원 감소
    note over ConcertService: 수용 인원 감소 
    ConcertService ->>- User: 좌석 예약 결과 반환
```

</details>

<details>
  <summary style="font-size: 1em;margin-left: 20px;">chargePoint - 유저 잔액 충전</summary>

유저가 금액을 충전한다.
- 사용자 식별자와 충전할 금액을 받아 잔액에 추가한다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant UserService
  User ->>+ Authorization: 대기열 검증 요청
  break 유효하지 않는 토큰 (만료, 사용자 불일치)
    Authorization -->> User: 예외처리
    note over Authorization: Bad Request
  end
    User ->>+ UserService: 포인트 충전 요청
  note over UserService: 포인트 충전
  UserService ->>- User: 충전 후 포인트와 결과 반환
```

</details>

<details>
  <summary style="font-size: 1em;margin-left: 20px;">getPoint - 유저 잔액 조회</summary>

유저가 잔액을 조회한다.
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회한다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant UserService
    User ->>+ Authorization: 대기열 검증 요청
    break 유효하지 않는 토큰 (만료, 사용자 불일치)
      Authorization -->> User: 예외처리
      note over Authorization: Bad Request
    end
    User ->>+ UserService: 잔액 조회 요청
    UserService ->>- User: 잔액 반환
```

</details>

<details>
  <summary style="font-size: 1em;margin-left: 20px;">createPayment - 결제</summary>

유저가 임시 배정된 좌석을 결제한다.
- 결제 처리한 후 결제 내역을 생성한다.
- 결제 완료 시 임시 배정됐던 좌석을 유저에게 배정한다.
- 유저의 대기열 토큰을 만료시킨다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant ReservationService
    participant PaymentService
    
  break 유효하지 않는 토큰 (만료, 사용자 불일치)
    Authorization -->> User: 예외처리
    note over Authorization: Bad Request
  end
    User ->>+ ReservationService: 결제 요청
  note over ReservationService: 예약 조회
    ReservationService ->>+ PaymentService: 임시 배정(예약)한 좌석 결제
    PaymentService ->>+ User: 결제 결과 반환
    
```
</details>