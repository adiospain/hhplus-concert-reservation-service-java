```mermaid
erDiagram

	user{
		bigint id PK "고유값"
		int point "보유 잔액"
	}
	payment {
		bigint id PK "고유 id"
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
        bigint concert_id PK,FK "복합키 : 콘서트 id + 고유 id"
		bigint id PK "복합키 : 콘서트 id + 고유 id"
		datetime start_at "날짜 정보"
		int capacity "수용 인원"
	}
	seat{
        bigint concert_schedule_id PK,FK "복합키 : 콘서트 날짜 id + 고유 id"
		bigint id PK "복합키 : 콘서트 날짜 id + 고유 id"
		
		string name "좌석번호"
		int price "좌석 가격"
		bool status "임시 배정 여부"
	}
	reservation{
	    bigint id PK "고유 id"
		bigint seat_id FK "좌석 id"
		bigint user_id FK "예약한 유저"
		datetime expire_at "만료 시간"
        datetime  reserved_date "예약 시점의 날짜"
		int reserved_price "예약 시점의 가격"
	}
	

	user ||--o{ payment : makes
	user ||--o{ reservation : makes
	concert ||--o{ concert_schedule : contains
	concert_schedule ||--o{ seat : contains
	reservation ||--o| payment : has
	user ||--o| token : owns
	seat ||--o| reservation : taken
```