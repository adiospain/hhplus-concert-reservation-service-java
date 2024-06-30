# hhplus-concert-reservation-service-java
[ Chapter 2 과제 ] 콘서트 예약 서비스

<details>
<summary style="font-size: 1.5em;">Description</summary>

- `콘서트 예약 서비스`를 구현해 봅니다.
- 대기열 시스템을 구축하고, 예약 서비스는 작업가능한 유저만 수행할 수 있도록 해야합니다.
- 사용자는 좌석예약 시에 미리 충전한 잔액을 이용합니다.
- 좌석 예약 요청시에, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 합니다.
</details>

<details>
<summary style="font-size: 1.5em;">Milestone</summary>
</details>

<details style="margin-left: 20px;">
  <summary style="font-size: 1.5em; font-weight: bold;">Sequence Diagram</summary>
  
  <details>
  <summary style="font-size: 1em;">issueToken</summary>
  
  <!-- Here you can include the details of the issueToken -->
  유저가 서비스 이용시 필요한 대기열 토큰을 발급 받는다.
  ```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant Controller
    participant Service
    participant Repository
    User ->>+ Controller: 대기열 토큰 발급 요청

	participant Controller
	participant Repository
    Controller ->>+ Service: 대기열 토큰 생성
	Service ->>+ Repository: 유저 정보 조회
	Repository ->>- Service: 유저 정보
	break 유효하지 않는 유저
	    Service -->> Controller: INVALID_USER
        note over Service: exception
    end

	Service ->>+ Repository: 대기열 토큰 생성
	Repository ->>- Service: 대기열 토큰
	Service ->>+ Controller: 대기열 토큰 반환
	Controller ->>+ User: 대기열 토큰 반환
```
</details>

<details>
  <summary style="font-size: 1em;">getTokenDetail</summary>
  
  유저가 대기열의 대기순서 및 잔여시간을 확인한다.
- 기본적으로 폴링으로 대기열을 확인하지만, 다른 방안 고려해본다.

```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant Controller
    participant Service
    participant Repository
    User ->>+ Authorization: 대기열 정보 조회 요청
    break 헤더에 토큰 정보 없음
        Authorization -->> User: ACCESS_DENIED
        note over Authorization: exeption
    end
    Authorization ->>+ Controller: 대기열 정보 조회 요청
   
    
    Controller ->>+ Service: 대기열 토큰 조회
    Service ->>+ Repository: 대기열 토큰 조회
    Repository ->>- Service: 대기열 토큰
	Service ->>+ Controller: 대기열 토큰 정보 반환
	Controller ->>+ User: 대기열 토큰 정보 (order ,TTL) 반환

```

</details>

<details>
  <summary style="font-size: 1em;">getConcertDate</summary>
  
  유저가 예약 가능한 날짜 목록을 확인한다.

```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant Controller
    participant Service
    participant Repository
    User ->>+ Authorization: 예약 가능 날짜 조회 요청
    break 유효하지 않는 토큰 (만료, 사용자 불일치)
        Authorization -->> User: ACCESS_DENIED
        note over Authorization: exeption
    end
	Authorization ->>+ Controller: 예약 가능 날짜 조회 요청
    Controller ->>+ Service: 예악 가능 날짜 조회
    Service ->>+ Repository: 예악 가능 날짜 조회
    Repository ->>- Service: 예약 가능 날짜
	Service ->>+ Controller: 예약 가능 날짜 반환
	Controller ->>+ User: 예약 가능 날짜 반환

```

</details>
<details>
  <summary style="font-size: 1em;">getConcertSeat</summary>
  
  유저가 예약 가능한 좌석 목록을 확인한다.
- 날짜 정보를 입력 받아 좌석 정보를 조회한다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant Controller
    participant Service
    participant Repository
    User ->>+ Authorization: 예약 가능 좌석 조회 요청
    break 유효하지 않는 토큰 (만료, 사용자 불일치)
        Authorization -->> User: ACCESS_DENIED
        note over Authorization: exeption
    end
    Authorization ->>+ Controller: 예약 가능 좌석 조회 요청
    Controller ->>+ Service: 예악 가능 좌석 조회
    break 유효하지 않는 날짜
        Service -->> User: INVALID_DATE
        note over Service: exeption
    end
    Service ->>+ Repository: 예악 가능 좌석 조회
    Repository ->>- Service: 예약 가능 좌석
	Service ->>+ Controller: 예약 가능 좌석 반환
	Controller ->>+ User: 예약 가능 좌석 반환

```

</details>

<details>
  <summary style="font-size: 1em;">reserveSeat</summary>
  
  유저가 좌석 예약한다.
- 날짜와 좌석 정보를 입력 받아 좌석을 예약 처리 한다.
- 좌석 예약과 동시에 해당 좌석은 그 유저에게 임시 배정된다.
- 배정 시간 내에 결제가 완료되지 않으면 임시 배정은 해제 된다.
```mermaid
sequenceDiagram
    actor User
    participant ClientServer
    participant Authorization
    participant Controller
    participant Service
    participant Repository
    User ->>+ Authorization: 좌석 예약 요청
    break 유효하지 않는 토큰 (만료, 사용자 불일치)
        Authorization -->> User: ACCESS_DENIED
        note over Authorization: exeption
    end
    Authorization ->>+ Controller: 좌석 예약 요청
    
    Controller ->>+ Service: 좌석 예약 생성
    Service ->>+ Repository: 좌석 임시 배정
    Repository ->>- Service: 임시 배정된 좌석
    Service ->>+ ClientServer: 실시간 배정 좌석 반영
    Service ->>+ Controller: 임시 배정된 좌석 반환
	Controller ->>+ User: 임시 배정된 좌석 반환
```

</details>

<details>
  <summary style="font-size: 1em;">chargePoint</summary>
  
유저가 금액을 충전한다.
- 사용자 식별자와 충전할 금액을 받아 잔액에 추가한다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant Controller
    participant Service
    participant Repository
    User ->>+ Controller: 금액 충전 요청
    Controller ->>+ Service: 금액 충전 및 업데이트
    break 충전 금액이 양수가 아닐 경우
        Service -->> User: INVALID_VALUE
        note over Service: exeption
    end
    Service ->>+ Repository: 금액 업데이트
    Repository ->>- Service: 금액 반환
	Service ->>+ Controller: 금액 반환
	Controller ->>+ User: 금액 반환

```

</details>

<details>
  <summary style="font-size: 1em;">getPoint</summary>
  
유저가 잔액을 조회한다.
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회한다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant Controller
    participant Service
    participant Repository
    User ->>+ Controller: 잔액 조회 요청
    Controller ->>+ Service: 잔액 조회
    Service ->>+ Repository: 잔액 조회
    Repository ->>- Service: 잔액 반환
	Service ->>+ Controller: 잔액 반환
	Controller ->>+ User: 잔액 반환
```

</details>
<details>
  <summary style="font-size: 1em;">createPayment</summary>
  
유저가 임시 배정된 좌석을 결제한다.
- 결제 처리한 후 결제 내역을 생성한다.
- 결제 완료 시 임시 배정됐던 좌석을 유저에게 배정한다.
- 유저의 대기열 토큰을 만료시킨다.
```mermaid
sequenceDiagram
    actor User
    participant Authorization
    participant Controller
    participant Service
    participant Repository
    User ->>+ Controller: 결제 요청
    Controller ->>+ Service: 결제
    Service ->>+ Repository: 콘서트 좌석 비용 조회
    Repository ->>- Service: 콘서트 좌석 비용
    Service ->>+ Repository: 유저 잔액 소비
    Service ->>+ Repository: 결제 내역 생성
    Service ->>+ Repository: 예약 배정 업데이트
    Service ->>+ Repository: 콘서트 좌석 정보 업데이트
    Service ->>+ Repository: 대기열 토큰 삭제
    Repository ->>+ Service: 결제 결과 반환
	Service ->>+ Controller: 결제 결과 반환
	Controller ->>+ User: 결제 결과 반환

```

</details>
</details>