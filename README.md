# hhplus-concert-reservation-service-java
[ Chapter 2 과제 ] 콘서트 예약 서비스

<details>
<summary style="font-size: 1.5em; font-weight: bold">Description</summary>

- `콘서트 예약 서비스`를 구현해 봅니다.
- 대기열 시스템을 구축하고, 예약 서비스는 작업가능한 유저만 수행할 수 있도록 해야합니다.
- 사용자는 좌석예약 시에 미리 충전한 잔액을 이용합니다.
- 좌석 예약 요청시에, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 합니다.
</details>




<details>
<summary style="font-size: 1.5em; font-weight: bold">Milestone</summary>

https://github.com/users/adiospain/projects/10

</details>

<details>
  <summary style="font-size: 1.5em; font-weight: bold">Sequence Diagram</summary>


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
</details>


<details>
<summary style="font-size: 1.5em; font-weight: bold">Flow Chart</summary>

- 콘서트 날짜 테이블 행에 비관적 락을 걸며 수용 인원을 확인하여 예약 가능한 날짜인지 확인 합니다.
- 수용 인원 수만큼 대기열을 진입하게 하고 수용 인원을 감소해줍니다.
- 이미 예약된 좌석을 선택한 유저의 수를 합산하여 수용 인원에 더해줍니다. 

```mermaid
    flowchart TD
    ConcertView[콘서트 조회] --> ConcertSelect((좌석 선택)) 
    ConcertSelect --> CheckWaiting1{대기번호가 활성 상태 인가?}
    CheckWaiting1 --> |Yes| ConcertDateView[예약 가능한 날짜 조회]
    CheckWaiting1 --> |No| CheckExpire1{토큰이 만료 되었는가?}
    CheckExpire1 --> |Yes| RenewToken1[토큰 재발급] --> ConcertSelect
    CheckExpire1 --> |No| CheckWaiting1
    
    ConcertDateView[날짜 조회] --> CheckWaiting2-1{예약 가능한 날짜인가?}
    CheckWaiting2-1 --> |Yes| CheckWaiting2-2{대기번호가 활성 상태 인가?}
    CheckWaiting2-1 --> |No| ConcertSelect
    CheckExpire2{토큰이 만료 되었는가?} --> |Yes| RenewToken2[토큰 재발급] --> ConcertSelect
    CheckExpire2 --> |No| CheckWaiting2-2
    CheckWaiting2-2 --> |Yes| ConcertSeatView[좌석 조회]
    CheckWaiting2-2 --> |No| CheckExpire2

    ConcertSeatView --> CheckWaiting3-1{임시 배정되지 않은 좌석인가?}
    CheckWaiting3-1 --> |Yes| CheckWaiting3-2{대기번호가 활성 상태 인가?}
    CheckWaiting3-1 --> |No| ConcertSelect
    CheckExpire3{토큰이 만료 되었는가?} --> |Yes| RenewToken3[토큰 재발급]--> ConcertSelect
    CheckExpire3{토큰이 만료 되었는가?} --> |No| CheckWaiting3-2
    CheckWaiting3-2 --> |Yes| createPayment[결제]
    CheckWaiting3-2 --> |No| CheckExpire3
    
    createPayment --> checkPayment1{유저 잔액 >= 콘서트 좌석 비용 ?}
    checkPayment1 --> |Yes| completePayment((결제 완료))
    checkPayment1 --> |No| exceptionPayment[잔액부족]
    exceptionPayment --> chargePoint[잔액 충전]
    chargePoint --> CheckWaiting3-2
```

</details>

<details>
<summary style="font-size: 1.5em; font-weight: bold">ERD</summary>

```mermaid
erDiagram

	user{
		bigint id PK "고유값"
		int point "보유 잔액"
	}
	payment {
		bigint id PK "고유값"
		bigint user_id FK "결제한 유저 id"
		bigint reservation_id FK "결제 대상"
		enum status "처리 결과"
	}
	token {
		bigint id PK "고유값"
		bigint user_id FK "소유자 id"
		enum status "상태"
		datetime expire_at "만료 시간"
	}
	concert{
		bigint id PK "고유값"
		string name "이름"
	}
	concert_schedule{
		bigint id PK "고유값"
		bigint concert_id FK "콘서트 id"
		datetime start_at "날짜 정보"
		int capacity "수용 인원"
	}
	seat{
		bigint id PK "고유값"
		string name "좌석번호"
		int price "좌석 가격"	
	}
	reservation{
		bigint concert_schedule PK "복합키 : 날짜 + 좌석"
		bigint seat_id PK "복합키 : 날짜 + 좌석"
		bigint user_id FK "예약한 유저"
		datetime expire_at "만료 시간"
		reservation_id prev "이전 예약 복합키"
	}
	

	user ||--o{ payment : makes
	user ||--o{ reservation : makes
	concert ||--o{ concert_schedule : contains
	concert_schedule ||--o{ seat : contains
	reservation ||--o| payment : has
	user ||--o| token : owns
	seat ||--o| reservation : taken
```

</details>

<details>
  <summary style="font-size: 1.5em; font-weight: bold">API Documentation</summary>

- 클라이언트에서 토큰을 헤더에 담아 요청을 보낸다고 가정 합니다.
- 토큰은 유저Id 대기 순서, 만료 시간 정보가 인코딩 되어 있습니다.
- 단 과제를 위해 헤더에 토큰이 없어도 DB에 토큰 정보를 참조하도록 설계합니다.

<details style="margin-left: 20px;">
<summary style="font-size: 1em; font-weight: bold;">User</summary>
<details style="margin-left: 30px;">
<summary style="font-size: 1em">POST /api/users/{userId}/token - 유저 토큰 발급</summary>
- Response

```
[
	{
		"token": string
	}
]
```

- Statuse code
    - 200: OK. 발급 완료
    - 400: Bad Request. 유효하지 않은 유저ID
    - 401: Unauthorized. 유효하지 않거나 만료된 토큰
    - 403: Forbidden: 허가되지 않은 접근
</details>
<details style="margin-left: 30px;">
<summary>GET /api/users/{userId}/point - 유저 잔액 조회</summary>

- Request
```
[
	header{
		"token" : string
	}
]
```
- Response
```
[
	{
		"point": number
	}
]

```
- Status code
    - 200: OK. 조회 완료
    - 400: Bad Request. 유효하지 않은 유저ID
    - 401: Unauthorized. 유효하지 않거나 만료된 토큰
    - 403: Forbidden: 허가되지 않은 접근
</details>
<details style="margin-left: 30px;">
<summary>POST /api/users/{userId}/charge - 유저 잔액 충전</summary>

- Request
```
[
	header{
		"token": string
	}
	body{
		"amount": number
	}
]	
```
- Response
```
[
	{
		"point": number
		"status": boolean
	}
]
```
- Status code
    - 200: OK. 충전 완료
    - 400: Bad Request. 유효하지 않은 유저ID / 충전 값
    - 401: Unauthorized. 유효하지 않거나 만료된 토큰
    - 403: Forbidden: 허가되지 않은 접근
		
</details>
</details>

<details style="margin-left: 20px;">
<summary style="font-size: 1em; font-weight: bold">Concert</summary>

<details style="margin-left: 30px;">
<summary>GET /api/concerts?available={true}&page={pageNum}&pageSize={pageSize} - 콘서트 목록 조회</summary>

- Request

```
[
	header{
		"token": string
	}
]
```

- Response
- 
```
[
	[
		{
			"concertId": number,
			"name": string
		}
	]
]
```

- Status code
  - 200: OK. 조회 완료
  - 400: Bad Request. 유효하지 않은 유저ID
  - 401: Unauthorized. 유효하지 않거나 만료된 토큰
  - 403: Forbidden: 허가되지 않은 접근
</details>

<details style="margin-left: 30px;">
<summary>GET /api/concerts/{concertId} - 콘서트 상세 조회</summary>

- Request
```
[
	header{
		"token": string
	}
]
```

- Response
```
[
	{
		"concertId": number,
		"name": string,
		"concertSchedule": [
			{
				"concertScheduleId": number,
				"open_at": date,
				"seat": number
			}
		]
	}
]
```

- Status code

    - 200: OK. 조회 완료
    - 400: Bad Request. 유효하지 않은 유저ID
    - 401: Unauthorized. 유효하지 않거나 만료된 토큰
    - 403: Forbidden: 허가되지 않은 접근
</details>

<details style="margin-left: 30px;">
<summary>GET /api/concerts/{concertId}/schedules?available=true - 콘서트 예약 가능 날짜 조회</summary>
- Request

```
[
	header{
		"token": string
	}
]
```

- Response
```
[
	"concertSchedule": 
		[
			{
				"concertScheduleId": number,
				"open_at": date,
				"seats": number
			}
		]
]
```
- Status code
    - 200: OK. 조회 완료
    - 400: Bad Request. 유효하지 않은 유저ID
    - 401: Unauthorized. 유효하지 않거나 만료된 토큰
    - 403: Forbidden: 허가되지 않은 접근
</details>

<details style="margin-left: 30px;">
<summary>GET /api/concerts/{concertId}/schedules/{concertScheduleId}/seats?available={true} - 콘서트 예약 가능 좌석 조회</summary>

- Request
```
[
	header{
		"token": string
	}
]
```

- Response
```
[
	"seats": 
		[
			"seatId": number,
			"name": string,
			"reserved": boolean,
			"price": number
		]
]
```

- Status code
    - 200: OK. 조회 완료
    - 400: Bad Request. 유효하지 않은 유저ID / 콘서트 관련 ID
    - 401: Unauthorized. 유효하지 않거나 만료된 토큰
    - 403: Forbidden: 허가되지 않은 접근
</details>

</details>
<details style="margin-left: 20px;">
<summary style="font-size: 1em; font-weight: bold">Reservation</summary>
<details style="margin-left: 30px;">
<summary style="font-size: 1em; font-weight: bold">POST /api/reservation - 좌석 예약</summary>

- Request
```
[
	header{
		"token": string
	}
	body{
		"concertId": number,
		"concertScheduleId": number,
		"seatId": number
	}
]
```

- Response
```
[
	{
		"status": boolean
	}
]
```

- Status code
    - 200: OK. 예약 완료
    - 400: Bad Request. 유효하지 않은 유저ID / 콘서트 관련 ID
    - 401: Unauthorized. 유효하지 않거나 만료된 토큰
    - 403: Forbidden: 허가되지 않은 접근
</details>
</details>

<details style="margin-left: 20px;">
<summary style="font-size: 1em; font-weight: bold">Payment</summary>
<details style="margin-left: 30px;">
<summary style="font-size: 1em; font-weight: bold">POST /api/payments?concertId={concertId}&concertScheduleId={concertScheduleId}&seatId={seatId} - 결제</summary>

- Request
```
[
	header{
		"token": string
	}
	body{
		"price": number
	}
]
```

- Response
```
[
	{
		"paymentId": number,
		"status": enum, //예외 케이스 처리 (잔액 부족)
		"price": number,
		"point": number
	}
]
```
- Status code
    - 200: OK. 결제 완료
    - 400: Bad Request. 유효하지 않은 유저ID / 콘서트 관련 ID / 결제금액
    - 401: Unauthorized. 유효하지 않거나 만료된 토큰
    - 403: Forbidden: 허가되지 않은 접근
</details>

</details>

</details>
<details>
  <summary style="font-size: 1.5em; font-weight: bold">Swagger</summary>
	<img style="width:1500px" src="https://github.com/user-attachments/assets/09b28415-6b43-47a8-a993-a9a117fe2eeb"/>

 <img style="width:1500px" src="https://github.com/user-attachments/assets/9e881887-01fc-4c97-b001-e9b83f6070a4"/>
 
<img style="width:1500px" src="https://github.com/user-attachments/assets/ee52bff1-714f-460c-b309-8d29bab88fac"/>

<img style="width:1500px" src="https://github.com/user-attachments/assets/1049ffdb-45f6-432e-93a4-d7d93bf71acd"/>
<img style="width:1500px" src="https://github.com/user-attachments/assets/6f8f2d3f-5020-4ba6-bd0f-ec4da7728d0d"/>
<img style="width:1500px" src="https://github.com/user-attachments/assets/3e2cef66-564b-47d4-b491-eb3c7d8831ad"/>

<img style="width:1500px" src="https://github.com/user-attachments/assets/c591fec0-97ba-484e-bd87-4ed1dde9c094"/>






<img style="width:1500px" src="https://github.com/user-attachments/assets/06b5b679-ee64-4822-b7d2-fdea4a3f8a98"/>
<img style="width:1500px" src="https://github.com/user-attachments/assets/6a714b29-febf-4a6a-a95a-b4382dbbf75a"/>






<img style="width:1500px" src="https://github.com/user-attachments/assets/874c4887-ec69-4fb4-8568-24e2a45021bd"/>


</details>
