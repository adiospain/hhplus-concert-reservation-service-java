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
	<img style="width:1500px" src="https://github.com/user-attachments/assets/f57eb5ea-bc4e-423e-a33c-5410d0e91659"/>

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


<details>
<summary style="font-size: 1.5em; font-weight: bold">Reflection</summary>
쉽게 쓰여지는 코드는 없다.

Java에서 제공하는 최신 문법과 lambda식을 활용해 짧게 코드를 작성할 때마다 뿌듯함을 느꼈다.

사람마다 다르겠지만, 나는 간결함보다는 명료함을 추구한다.

사람이 이해하기 쉽고 다루기 쉬운 코드를 작성하는 것을 좋아한다.

글을 쓸 때도 마찬가지로, 이해하기 어렵고 불분명한 문장을 피하고 주어와 술어가 분명한 문장을 추구한다.

사람이 읽기 쉽다는 것은 코드의 확장성과 유지보수 측면에서 효율적인 코드를 의미하며,
이것이 곧 높은 생산성으로 이어진다는 생각이다.

개발자로서의 여정에서 가장 큰 도전 중 하나는 요구사항과 시나리오를 정확히 파악하는 것이었다.

초기에는 기술적인 구현에만 집중한 나머지, 이 부분을 소홀히 했고 그 결과 개발 일정이 지연되는 경험을 했다.


토큰 서비스를 구현할 때의 일이다.

코드를 빠르게 작성하는 데에만 집중했고,

최신 Java 문법과 lambda식을 활용해 간결한 코드를 만들어냈다.

그러나 실제 구현 과정에서 여러 문제에 부딪혔다.


요구사항 이해 부족:
토큰의 상태 변경 로직이 생각보다 복잡했다.

단순히 만료된 토큰을 처리하는 것이 아니라,

다음 토큰을 활성화하는 과정도 필요했음을 뒤늦게 깨달았다.


시나리오 고려 미흡:

다양한 상황(토큰 만료, 연결 해제 등)에 대한 처리가 필요했지만, 초기에는 이를 충분히 고려하지 못했다.

확장성 부족:

처음 작성한 코드는 간결했지만, 새로운 요구사항이 추가될 때마다 큰 변경이 필요했다.


이 경험 이후, 나는 개발 프로세스를 개선했다.

요구사항 분석과 설계에 더 많은 시간을 투자하고,

혼자 개발 하는 것이 아닌 팀원과의 커뮤니케이션을 강화했다.

또한, 다른 개발자들의 관점에서 내 코드의 가독성과 유지보수성을 점검하는 습관을 들였다.

요구사항과 시나리오를 제대로 파악하는 것은 단순한 기술적 능력이 아니었다.

프로젝트 성공을 좌우하는 핵심 능력이었다.


요구사항과 시나리오 파악은 복잡한 과정이다.

1. 다양한 이해관계자들의 니즈를 파악해야 한다.

2. 현재의 요구사항뿐만 아니라 미래의 변경 가능성도 고려해야 한다.

3. 예외 상황과 엣지 케이스를 모두 고려해야 한다.

4. 기술적 제약사항과 비즈니스 요구사항 사이의 균형을 맞춰야 한다.


이러한 복잡한 요소들을 모두 정확히 파악하고 이해하는 것은 매우 어렵다.
그리고 이 과정이 완벽하지 않으면,
그 에 따른 코드 역시 완벽할 수 없다.

결과적으로, 요구사항과 시나리오 파악의 어려움이 코드 작성의 어려움으로 이어지며,

이는 '쉽게 쓰여진 코드'가 존재하기 어려운 근본적인 이유가 된다.


따라서 쉽게 코드를 작성하기 위해서는 요구사항과 시나리오를 정확히 파악하는 능력을 향상시켜야 한다.

이는 개발자로 일하면서 평생 갈고닦아야 할 스킬이라고 생각한다.

</details>

<details>
<summary style="font-size: 1.5em; font-weight: bold">Concurrency Issue</summary>
	
# 동시성 이슈
분산환경에서 여러 스레드가 공유자원에 접근하여 조회 / 수정 / 삭제 할 때 발생하는 문제입니다.
동시성 이슈를 해결 하기 위해 락을 사용하며 각 방법의 복잡도, 성능, 효율성을 비교해봅니다.

- 낙관적 락
- 비관적 락
- 분산락
	- Simple Lock
	- Spin Lock
	- Pub/Sub

# 유즈케이스 고려 대상
- 포인트 조회 / 충전 / 사용
- 좌석 예약

# 공통 테스트 환경
- Java : JDK 17
- DB : H2
- Test Tool : JUnit 5


# 포인트 유즈케이스
- 포인트 조회, 충전, 사용과 같은 작업에서 한 사용자로부터 들어온 중복 요청을 제외하기 위해 낙관적 락을 고려했으나, 다중 환경에서의 접근과 포인트의 정합성을 보장하기 위해 비관적 락을 사용하기로 결정했습니다. 
- 낙관적 락은 데이터 충돌을 감지하고 재시도를 통해 문제를 해결하는 방식으로, 충돌이 빈번하지 않은 상황에서 유리합니다. 그러나 금액과 관련된 작업에서는 데이터의 정확성과 일관성이 
매우 중요하므로, 비관적 락을 통해 모든 트랜잭션이 순차적으로 처리되도록 하여 데이터 무결성을 보장했습니다.


## 구현
- 비관적 락 [소스코드](https://github.com/adiospain/hhplus-concert-reservation-service-java/compare/Step_11_Point-Pessimistic...Step_11_Point-Pessimistic_Review)
- 낙관적 락 [소스코드](https://github.com/adiospain/hhplus-concert-reservation-service-java/compare/Step_11_Point-Optimistic...Step_11_Point-Optimistic_Review)
	- 지수 백오프 전략으로 재시도 로직 구현
	- 테스트 통과할 때까지 최대 재시도 횟수를 늘려가며 최적화

## 테스트 환경

- 비교 대상: 비관적 락 vs 낙관적 락
- 스레드 수: 3333개
- 작업: 포인트 조회, 충전, 사용 (각 1111번의 호출)
- 10분간 1분 간격으로 실행


## 테스트 결과
1. 처리 시간 (평균)
	- 비관적 락
		- getPoint : 1.149 ms
		- chargePoint : 1.121 ms
		- usePoint : 1.298 ms
![point_pessimistic_average](https://github.com/user-attachments/assets/6eca840d-74d3-4917-a373-84f8030a7183)
	- 낙관적 락 
		- getPoint : 3.821ms
		- chargePoint : 2.464ms
		- usePoing : 2.443ms
![point_optimistic_average](https://github.com/user-attachments/assets/ecae6e87-7ad8-4af7-9e38-13ee06e5945c)

## 분석


- 비관적 락
	- 충돌이 빈번한 상황에서 안정적인 성능을 보입니다.
	- 다만 동시에 많은 요청이 들어올 경우 대기 시간이 길어질 수 있으며, 트랜잭션이 오래 지속될 경우, DB 커넥션 풀을 오랫동안 점유하게 됩니다.
 이는 전체적인 처리 시간 증가로 이어질 수 있습니다.
- 낙관적 락
	- 테스트 시 빈번한 충돌 연출로 인해 최대 재시도 횟수를 20번을 하고나서야 테스트 통과할 수 있었습니다.
	- 이로 인해 처리 시간이 지연됐으며 그 결과 비관적 락 보다 처리시간이 길어졌습니다.
  - 락 요청 시 대기를 하지 않아 DB 커넥션 풀을 점유할 필요가 없지만, 재시도 로직 구현시 Application 단에서 오버헤드가 발생합니다.
  - 충전 / 사용 메서드에서 재시도가 초기 시도 횟수의 2배 발생했습니다.
![point_optimistic_method_retry](https://github.com/user-attachments/assets/ff7c3aa0-10c5-4831-899e-8234661c8296)

낙관적 락은 1인당 1매로 제한된 콘서트 티켓팅 시스템에서 효과적일 수 있습니다.  
동시에 여러 사용자가 같은 티켓을 요청하더라도, 먼저 처리된 요청만 성공하고 나머지는 실패로 처리하는 로직을 구현할 때 유용합니다.  

비관적 락은 데이터 충돌이 빈번하게 발생하고, 모든 요청을 처리해야 하는 상황에 적합합니다.  
대표적인 예로 대학의 수강신청 시스템을 들 수 있습니다. 


비관적 락을 사용하면 데이터 일관성을 유지하면서 모든 요청을 순차적으로 처리할 수 있습니다.

그러나 비관적 락의 주요 단점은 데이터베이스 리소스, 특히 커넥션 풀을 과도하게 사용한다는 점입니다. 
일반적으로 데이터베이스 커넥션 풀의 크기는 제한되어 있어(예: 200개), 
비관적 락을 사용하면 이 커넥션들을 장시간 점유하게 됩니다. 이는 시스템의 전반적인 성능 저하로 이어질 수 있으며, 
다른 트랜잭션들이 커넥션을 기다리느라 지연되는 문제를 야기할 수 있습니다.

포인트 조회 / 충전 / 사용의 처리 시간을 고려하면 비관적 락 방식이 가장 효과적인 동시성 제어로 평가됩니다.

## 요청 순서 보장의 중요성
포인트 관련 작업은 순서에 따라 결과가 달라질 수 있습니다. 
예를 들어, 100원 차감 후 200원 증가와 200원 증가 후 100원 차감은 다른 결과를 낳습니다.
비관적 락은 이러한 요구사항을 자연스럽게 충족시키며, 충돌이 빈번한 상황에서 안정적인 성능을 보입니다.

# 예약 유즈케이스
- 같은 좌석을 여러 명이 동시에 예약을 하려고 할 때 한명에게만 좌석을 줄 수 있다는 제약이 있어
낙관적 락을 사용하여 충돌이 발생하면 즉시 예외를 발생시켜 사용자에게 빠르게 응답을 제공하고자 합니다.
- 여러 좌석을 동시에 예약하는 경우 비관적 락은 데드락 상황을 초래할 수 있습니다.

## 구현
- Unique Key
	-  `Reservation` 테이블에 `seat_id`와 `concertSchedule_id`로 유니크 키를 설정하여 중복 예약을 방지했습니다.
- 비관적 락 [소스코드](https://github.com/adiospain/hhplus-concert-reservation-service-java/compare/Step_11_Reservation-Pessimistic...Step_11_Reservation-Pessimistic_Review)
	- `ConcertScheduleSeat` 테이블 조회 시 순차적인 접근을 보장합니다.
- 낙관적 락 [소스코드](https://github.com/adiospain/hhplus-concert-reservation-service-java/compare/Step_11_Reservation-Optimistic...Step_11_Reservation-Optimistic_Review)
	- `ConcertScheduleSeat` 테이블 조회 시, ObjectOptimisticLockingFailureException 예외처리로 사용자에게 미리 중복 예약을 알릴 수 있습니다.
 - Redisson [소스코드](https://github.com/adiospain/hhplus-concert-reservation-service-java/compare/Step_11_Reservation-Redisson...Step_11_Reservation-Redisson_Review)
	 - TTL 5분, 대기 시간 0초, 락 획득 후 락 해제를 하지 않도록 설정합니다.

## 테스트 환경
- 비교 대상: 순수 Unique Key vs 비관적 락 vs 낙관적 락
- 스레드 수: 1000개
- 작업: 동시에 1000명이 하나의 좌석에 대해 예약 요청
- 10분간 1분 간격으로 실행
## 테스트 결과
1. 처리 시간 (평균)
   
   파란선 : 각 실행 처리 시간의 평균값  
   빨간선 : 낙관적 락의 평균 처리 시간  
   
   
	- 유니크 키 : 20.567 ms
		![reservation_uniquekey_average](https://github.com/user-attachments/assets/1c2db284-6c5f-4be4-8a9e-1931f1cfc425)
	- 비관적 락 : 95.457 ms
		![reservation_pessimistic_average](https://github.com/user-attachments/assets/b9b22317-b374-427e-9aee-ece9c774c0fc)
	- 낙관적 락 : 18.046 ms
		 
		![reservation_optimistic_average](https://github.com/user-attachments/assets/f86013c6-9a91-4fb2-bc35-8447aedcd529)
	- 분산락 : 7.936 ms
		![reservation_distributed_average](https://github.com/user-attachments/assets/52df9e28-8728-4fca-94f0-7e84d32c5caa)





## 분석 
- Unique Key (20.567 ms)
	- 세 번째로 빠른 처리 시간을 보여줍니다.
	- 데이터베이스 레벨에서 빠르게 중복을 체크하고 insert하기 때문에 효율적일 수 있습니다.
	- 동시 요청 시 요청이 빠르게 실패하고 반환 될 수 있습니다.
- 비관적 락 (95.457 ms)
	- 가장 느린 처리 시간을 보여줍니다.
	- DB 대기풀을 점유하며 순차적 접근을 용이하게 하지만, 대기 시간이 크게 발생할 수 있습니다.
- 낙관적 락 (18.046 ms)
	- 두 번째로 빠른 처리 시간을 보여줍니다.
	- `ObjectOptimisticLockingFailureException` 처리 과정에서 추가적인 시간이 소요될 수 있습니다.
- 분산락 (7.936 ms)
	- 가장 빠른 처리 시간을 보여줍니다.


콘서트 좌석을 조회하는 순간 낙관적 락을 적용하여 충돌 시 예외처리로 추가적인 트랜잭션 진행 전 중복 예약을 방지할 수 있습니다. 낙관적 락을 사용했을 때 처리 시간이 비약적으로 상승하지 않고 오히려 빠른 성능을 보여주었습니다.

이는 `OPTIMISTIC_FORCE_INCREMENT `옵션으로 인해 조회 시에도 버전 업데이트로,   
전체적인 처리 시간이 여전히 낮게 유지되고 있음을 나타냅니다.

콘서트 좌석 테이블에 별도로 예약여부 필드를 추가하고 싶지 않아
version 필드만으로 상태관리를 했습니다.

예약여부를 나타내는 별도의 필드를 추가하지 않아도, 상태관리를 명확하게 할 수 있으며
낙관적 락의 오버헤드를 줄일 수 있습니다.

현재 구현한 예약 기능으로만 봤을 때, Unique Key과 분산락을 조합 사용하는 것이 최적화된 것으로 보입니다.

순수 Unique Key 방식은 데이터베이스 레벨에서 중복 체크와 삽입을 빠르게 처리할 수 있어, 동시 요청 시에도 효율적으로 동작합니다.  
반면, 비관적 락과 낙관적 락 조합으로 각각의 특성에 따라 처리 시간이 더 길어질 수도 있고 짧아질 수 있습니다.  

# 결론
 이 결과는 특정 환경과 구현 방식에서는 가능할 수 있지만, 일반적인 경우와는 다소 차이가 있습니다.  
정확한 분석을 위해서는 더 자세한 테스트 환경 정보와 구현 세부사항, 그리고 다양한 시나리오에서의 추가 테스트가 필요할 것 같습니다.  

분산락은 Redis 서버와의 통신을 필요로 하므로, 
네트워크 트래픽이 증가할 수 있습니다. 
이는 특히 고부하 상황에서 시스템 성능에 영향을 줄 수 있습니다.

Redis 서버가 다운되거나 네트워크 연결이 끊길 경우, 
전체 시스템의 락 메커니즘이 작동하지 않을 수 있습니다. 
이는 심각한 데이터 일관성 문제를 야기할 수 있습니다.

Redis 서버 장애 시 서버의 유지보수, 모니터링, 스케일링 등 추가적인 인프라 관리가 필요합니다.

성능뿐만 아니라 시스템의 안정성, 확장성, 유지보수성 등 다른 중요한 요소들도 함께 고려해야 합니다.  
</details>

<details>
<summary style="font-size: 1.5em; font-weight: bold">Caching Strategy</summary>

# 캐싱 전략 및 구현 보고서

콘서트 예약 시스템에서 발생하는 다양한 Query에 대한 분석과 캐싱 전략 설계를 다룹니다. 
특히, Redis를 활용하여 시스템 성능을 최적화하고, 대량의 트래픽 발생 시 지연을 최소화하는 방법을 제시합니다.

## Query 분석 및 캐싱 전략 설계
1. 콘서트 조회
   - **Query**: `SELECT * FROM concert` [findAll()]
   - 분석 : 정보 변경이 적으며, 조회 빈도가 높음.
   - 캐싱전략 :
      - 캐시 키 : `allConcerts`
      - TTL : 10분
      - 캐시 로직 :
         - 캐시에서 `allConcerts` 키로 조회
         - 캐시 미스 시 DB에서 조회하고, 결과를 캐시에 저장
         - 스탬피드 현상 방지를 위해 redis 분산락 적용
2. 콘서트 예약가능 날짜 조회
   - **Query**: SELECT cs FROM concert_schedule WHERE concert_id = ? [findAllByConcertId()]
   - 분석: 예약 가능 날짜도 변경이 적으며, 조회 빈도가 높음.
   - 캐싱 전략:
     - 캐시 키: `concert:{concert_id}:concertSchedules`
     - TTL: 10분
     - 캐시 로직:
       - 캐시에서 `concert:{concert_id}:concertSchedules` 키로 조회
       - 캐시 미스 시 DB에서 조회하고, 결과를 캐시에 저장
       - 스탬피드 현상 방지를 위해 redis 분산락 적용
3. 콘서트 좌석 조회
   - 분석: 실시간으로 좌석 선점이 발생하여 잦은 변경이 생김.
   - 캐싱 전략: 캐싱하지 않음.
4. 포인트 / 예약 / 결제
   - 분석: 사용자별 정보로, 실시간 업데이트가 중요하며, 개인정보가 포함될 수 있음.
   - 캐싱 전략: 캐싱하지 않음.
## 테스트 시나리오
1. 전체 콘서트 목록 조회
    - 검증항목
      - 반환된 콘서트 목록의 정확성
      - 첫 실행 시간과 이후 실행 시간의 평균 비교
2. 예약 가능한 콘서트 날짜 조회
    - 검증항목
      - 반환된 콘서트 상세 정보의 정확성
      - 첫 실행 시간과 이후 실행 시간의 평균 비교
## 테스트 결과
1. 전체 콘서트 목록 조회 테스트 결과
    - 첫 실행 시간 : 83.048701 ms
    - 이후 실행 시간 평균 0.156085 ms
    - 성능 향상률 : 99.8121% | [(첫 실행시간 - 평균 실행시간) / 첫 실행시간 * 100]%
2. 예약 가능한 콘서트 날짜 조회 테스트 결과
    - 첫 실행 시간 : 79.825361 ms
    - 이후 실행 시간 평균 0.198505 ms
    - 성능 향상률 : 99.7514% | [(첫 실행시간 - 평균 실행시간) / 첫 실행시간 * 100]%

## 향후 계획
    - 부하 테스트를 통한 시스템 한계 파악
    - 캐시 만료 정책 최적화
    - 실제 사용자 패턴을 반영한 시나리오 기반 테스트 수행

## 결론
캐싱 매커니즘 도입으로 서비스 응답시간이 크게 개선 되었습니다.
향후 추가적인 테스트와 최적화를 통해 서비스의 성능을 더욱 개선할 수 있을 것으로 기대됩니다.
</details>

<details>
<summary style="font-size: 1.5em; font-weight: bold">Waiting Queue</summary>

# 대기열 시스템 설계 및 구현 보고서

대규모 인원을 수용할 수 있는 대기열 시스템을 위해 Redisson 자료구조를 활용한 설계를 진행했습니다.

## 시스템 개요
1. 대기열 토큰: RScoredSortedSet
    - Score : 토큰 생성 시간
    - 장점
        - Score 조회로 효율적인 대기열 순번 확인 (O(log N))
        - 생성 시간 기반의 자연스러운 순차 정렬
    - 단점
      - 요소 추가/제거 시 O(log N)의 시간 복잡도로, 대규모 데이터에서 성능 저하 가능
      - 정렬되는 구조로 메모리 사용량이 상대적으로 높음
      - TTL 기능 없음
2. 활성화된 토큰: RSetCache
    - 장점
        - 개별 토큰의 만료 시간을 TTL로 자동 관리
        - 빠른 활성 상태 확인 및 접근 (O(1))
    - 단점
      - 순서가 보장되지 않아 대기열 순서 관리에 부적합
      - 대기열 위치 확인 기능 제공 안 함
      - 캐시 특성상 메모리 부족 시 데이터 손실 가능성 존재

## 성능 분석
| 작업        | 대기열 토큰 (RScoredSortedSet) | 활성화된 토큰 (RSetCache) |
| --------- | ------------------------- | ------------------- |
| 활성 상태 확인  | O(1)                      | O(1)                |
| 대기열 위치 확인 | O(log N)                  | N/A                 |
| 큐 크기 확인   | O(1)                      | O(1)                |
| 요소 추가/제거  | O(log N)                  | O(1)                |

## 수정 전  (RDBMS 기반)
1. RDBMS로 구현
    - 장점 : 데이터의 일관성, 트랜잭션 관리 용이
    - 단점 : 대규모 동시 접속 시 성능 저하 가능성
1. 토큰 발급 시 Token 테이블에 Token 등록
    - 장점 : 데이터 영구 저장, 복잡한 쿼리 가능
    - 단점 : 디스크 기반의 I/O 작업으로 메모리 기반 I/O 작업 보다 상대적으로 느림
1. 토큰 조회 시 은행창구식으로 Token 활성화
    - 장점 : 간단한 로직
    - 단점 : 동시성 처리에 추가적인 매커니즘 필요
1. 스케줄러로 응답 완료한 토큰 / 만료 토큰 DONE 상태 컬럼 수정으로 관리
    - 장점 : 배치 처리로 효율적인 리소스 사용
    - 단점 : 실시간 처리가 어려움

이러한 단점과 함께 Token 테이블에는 많은 쓰레기 데이터가 생성되고
대기열 도중 이탈한 유저를 일일이 확인 후 직접 제거해줘야 합니다.

대규모 트래픽 처리와 실시간 성능에 최적화하기 위해 수정이 필요합니다.

## 수정 후 (Redis 기반)
1. Redis 자료구조로 구현
    - 장점 : 높은 성능, 대규모 트래픽 처리에 유리
    - 단점 : 데이터 영구성 보장이 어려움, 휘발성 데이터
    ```java
   //토큰 정보 조회 메서드
   @Override
    public Optional<Token> getToken(long userId, String accessKey) {
      String key = TOKEN_KEY_PREFIX + userId + ":" + accessKey;
      RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);
      RSetCache<String> activeQueue = redissonClient.getSetCache(ACTIVE_QUEUE_KEY);

      if (activeQueue.contains(key)){
        return Optional.of(Token.create(userId, accessKey, 0));
      }

      Double score = waitQueue.getScore(key);
      if (score != null){
        int position = waitQueue.rank(key).intValue();
        return Optional.of(Token.create(userId, accessKey, position+1));
      }
      return Optional.empty();
    }
   ```
1. 토큰 발급 시 RScoredSortedSet에 Token 등록
    - 장점 : 효율적인 순서 관리, 빠른 조회 / 삽입
    - 단점 : 복잡한 데이터 관계 표현 어려움
    ```Java
    @Override
    public Token save(Token token) {
     String key = TOKEN_KEY_PREFIX + token.getUserId() + ":" + token.getAccessKey();
     RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);
     queue.add(System.currentTimeMillis(),key);
     return Token.create(token.getUserId(), token.getAccessKey(), queue.rank(key)+1);
    }
    ```
1. N초 마다 M명씩 Token 활성화, 활성화된 토큰은 RSetCache로 이동
    - 장점 : 시스템 부하 조절 가능, 효율적인 리소스 사용
    - 단점 : 즉시 활성화가 필요한 경우 대응 어려움
    ```java
    @Override
    public void activateTokens(){
      RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE_KEY);
      RSetCache<String> activeQueue = redissonClient.getSetCache(ACTIVE_QUEUE_KEY);
      Collection<String> tokensToActive = waitQueue.pollFirst(MAX_ACTIVE_USER);
      for (String token : tokensToActive){
        activeQueue.add(token, TOKEN_TTL_MINUTES, TimeUnit.MINUTES);
      }
    }
    ```
1. RSetCache의 TTL로 응답 완료한 토큰 / 만료 토큰 관리
    - 장점 : 만료 처리, 실시간 정확성
    - 단점 : 만료 시간이 지난 후 지연(lazy) 방식으로 동작하여 별도 스케줄러 필요


Redis 관리를 위해 TokenRedisRepository를 생성했고
토큰 관련 비즈니스 로직을 리팩토링 하면서 로직들이 Service로부터 분산 되었습니다.

특히 RSetCache의 TTL 기능을 사용하여 만료된 토큰을 비동기적으로 처리할 수 있습니다.

1. for-loop을 통해 RSetCache의 요소를 조회할 때, 만료된 토큰은 null을 반환 합니다.
2. null 반환과 동시에 해당 토큰은 백그라운드에서 비동기적으로 삭제 처리 됩니다.

    ```Java
    @Override
      public void touchExpiredTokens() {
        RSetCache<String> activeQueue = redissonClient.getSetCache(ACTIVE_QUEUE_KEY);
        for (String token : activeQueue){
          //별도의 삭제 로직 구현 없이 for-loop 조회만 해도 TTL 만료된 요소 삭제
        }
      }
    ```
### TokenScheduler 작동 프로세스
1. 만료 토큰 처리:
   - touchExpiredTokens 메서드 호출
   - active_queue의 만료된 토큰 자동 제거
2. 토큰 활성화:
   - wait_queue에 있는 토큰들을 active_queue로 이동
   - 각 토큰에 적절한 TTL 설정


Token Entity는 DTO처럼 사용하게 되었으며 Redis에 쌓이는 실질적인 데이터는 Token이 아닌 문자열입니다.


수정 후에는 데이터가 휘발성이 있기 때문에
데이터의 영구성이나 복잡한 비즈니스 로직 처리가 필요한 경우
추가적인 고려사항이 필요합니다.

## 테스트 및 검증

1. **통합 테스트**
    - 여러 사용자가 토큰 조회 하는 동시성 테스트
      ![20240802_054019](https://github.com/user-attachments/assets/938539c8-af13-4e24-9ebc-2eb5a03e85ed)
      ![20240802_054015](https://github.com/user-attachments/assets/70f5268d-3470-4020-9527-88aaeeb4702c)
	### DB 기반 시스템

		- **동시성 수준**: 10명
		- **평균 응답 시간**: 60ms

	### Redis 기반 시스템
	
		- **동시성 수준**: 300명
		- **평균 응답 시간**: 0.50ms

	Redis로 구현한 버전이 DB 구현 버전보다 훨씬 뛰어난 성능을 보여주고 있습니다. 이 결과에 대해 몇 가지 중요한 점을 짚어보겠습니다:

	1. Redis 구현이 300명의 동시 요청을 처리하면서도 DB 구현의 10명 처리 시간보다 훨씬 빠른 것을 보면, 대규모 트래픽 처리에 매우 적합해 보입니다.
	2. 사용자 경험을 크게 향상시킬 수 있는 수준입니다.
	5. 디스크 I/O의 DB 연산보다 Redis의 인메모리 연산이 효율적으로 작동하고 있습니다.
	6. Redis 구현이 동시성 처리에 더 효과적인 것으로 보입니다. 이는 분산 락이나 다른 동시성 제어 메커니즘이 잘 작동하고 있다는 증거일 수 있습니다.
	7. 이 결과는 Redis 구현이 더 많은 동시 사용자를 처리할 수 있는 잠재력을 보여줍니다.


## 결론


대기열 시스템의 설계 및 구현을 RDBMS 기반에서 Redis 기반으로 전환한 결과, 다음과 같은 주요 개선사항과 이점을 얻을 수 있었습니다:

1. **성능 향상**:

    - Redis의 인메모리 특성을 활용하여 I/O 작업 속도가 크게 향상되었습니다.
    - RScoredSortedSet과 RSetCache의 효율적인 데이터 구조로 대기열 관리 및 활성 토큰 처리 성능이 개선되었습니다.

2. **확장성 개선**:

    - 대규모 동시 접속 환경에서도 안정적인 성능을 유지할 수 있게 되었습니다.
    - N초마다 M명씩 토큰을 활성화하는 방식으로 시스템 부하를 효과적으로 조절할 수 있게 되었습니다.

3. **자동화된 데이터 관리**:

    - TTL 기능을 통해 만료된 토큰을 자동으로 제거함으로써 수동 관리의 필요성이 줄어들었습니다.
    - 대기열 이탈 사용자에 대한 별도의 처리가 필요 없어졌습니다.

4. **실시간 처리 개선**:

    - 스케줄러 기반의 배치 처리에서 실시간 처리로 전환되어 시스템의 반응성이 향상되었습니다.

5. **코드 구조 개선**:

    - 토큰 관련 비즈니스 로직이 서비스 계층에서 분리되어 관심사의 분리가 이루어졌습니다.
    - TokenRedisRepository의 도입으로 데이터 접근 로직이 명확해졌습니다.


다만, 새로운 구현 방식에 따른 몇 가지 고려사항도 존재합니다:

1. **데이터 영구성**:

    - Redis의 휘발성 특성으로 인해 중요 데이터의 영구 저장이 필요한 경우 추가적인 백업 전략이 필요합니다.

2. **복잡한 쿼리 처리**:

    - RDBMS에 비해 복잡한 데이터 관계나 쿼리 처리가 제한적일 수 있으므로, 필요에 따라 보조 데이터베이스 사용을 고려해야 합니다.

3. **데이터 일관성**:

    - 분산 환경에서의 데이터 일관성 유지를 위한 추가적인 전략이 필요할 수 있습니다.
</details>
