# 부하테스트

## 배경
대용량 트래픽 환경에서 시스템 성능의 주요 병목점은 주로 데이터베이스 연결(DB connection)에서 발생합니다. 이는 시스템의 전반적인 성능과 확장성에 중대한 영향을 미칩니다. 
또한, 웹 애플리케이션 서버(예: Tomcat)의 스레드 관리도 중요한 요소입니다.

## 문제점
긴 트랜잭션 시간은 DB 연결 자원의 비효율적 사용을 초래합니다. 단일 작업이 DB 연결을 장시간 점유하면, 다른 요청들의 대기 시간이 증가하고 전체 시스템 처리량이 감소하게 됩니다.
Tomcat의 스레드 풀은 동시에 처리할 수 있는 요청의 수를 제한합니다. 스레드 수가 부족하면 요청 처리가 지연되고, 과도하게 많으면 시스템 리소스를 낭비하게 되고 동시성 문제를 초래할 수 있습니다.


## 테스트 목적
- 시스템의 최대 처리 용량 확인
- 성능 병목 지점 식별
- 업데이트 후 성능 영향 평가

## 테스트 환경
- 하드웨어 구성
    1. CPU
        - 모델 이름 :
       ```
       lscpu | grep "Model name"
       Model name: AMD Ryzen 5 5600X 6-Core Processor
       ```
        - 물리적 CPU 개수 :
       ```
       lscpu | grep "Socket(s):"
       Socket(s): 1
       ```
        - 코어 당 스레드 개수 :
       ```
       lscpu | grep "Thread(s) per core:"
       Thread(s) per core: 2
       ```
        - 총 논리 CPU 수 :
       ```
       lscpu | grep "^CPU(s):"
       CPU(s): 12
       ```
        - 기본 클럭 속도 :
       ```
       lscpu | grep "CPU MHz:"
       CPU MHz: 2514.226
       ```
        - 최대 클럭 속도 :
       ```
       lscpu | grep "CPU max MHz:"
       CPU max MHz: 3700.0000
       ```
    1. Memory
        - 메모리 정보 :
          총 물리적 메모리 : 16GB  (16,290,780 kB)
- 소프트웨어 구성
    - JDK 17
    - Spring Boot 3.3.1
    - Docker mySQL:8.0
    - Docker redis
    - Docker zookeeper:3.4.6
    - Docker kafka:2.13-2.7.0
- 테스트 도구 구성
    - K6
    - Docker prometheus:latest
    - Docker grafana:latest
    - Docker elasticsearch:latest
    - Docker logstash:latest
    - Docker kibana:latest


| 엔드포인트       | Target TPS |
| ----------- | ---------- |
| 대기열 진입 / 조회 | 400 / s    |
| 콘서트 조회      |   400 / s         |
| 콘서트 날짜 조회   |    400 / s        |
| 콘서트 좌석 선택   | 400 / s           |
| 예약          |      400 / s      |
| 포인트 조회 / 충전 |  400 / s          |
| 결제          |   400 / s         |

## 테스트 단계
1. 평시트래픽 시뮬레이션 (1분)
    - 가상사용자 : 500
    - 목적 : 일반적인 운영 상황에서의 시스템 성능 평가
2. 부하 테스트 (1분)
    - 가상사용자 : 500으로 유지
    - 목적 : 중간 수준의 부하에서 시스템의 안정성 확인
3. 내구성 테스트 (2분)
    - 가상사용자 : 500으로 유지
    - 목적 : 지속적인 중간 부하에서의 시스템 성능 및 안정성 평가
4. 스트레스 테스트 (2분)
    - 가상사용자 : 500에서 2000으로 증가
    - 목적 : 높은 부하 상황에서의 시스템 한계 및 성능 저하 지점 파악
5. 피크 테스트 (2분)
    - 가상사용자 : 2000으로 유지
    - 목적 : 최대 부하 상황에서의 시스템 동작 및 성능 평가
6. 부하 감소 (1분)
    - 가상사용자 : 2000에서 0으로 감소
    - 목적 : 부하 감소 시 시스템의 회복 능력 평가

## 관찰 지표
- 응답 시간 (평균, P50, P95, P99)
- 초당 처리 요청 수 (TPS)
- 실패율
- 시스템 리소스 사용률 (CPU, 메모리)

## 테스트 설정
```application.propeties
server.tomcat.threads.max=300
server.tomcat.max-connections=2000
server.tomcat.accept-count=3000
```

1. 스레드 최대 수 설정
    - 동시에 처리할 수 있는 요청의 수를 제한합니다.
2. 연결 수 분석
    - 최대 연결 수(2000) + 대기열 수용 한도(3000) = 총 5000개 동시 연결 처리 가능
    - 최대 연결 수(2000)가 최대 가상 사용자 수와 일치하여, 피크 부하 시 모든 요청을 즉시 처리할 수 있을 것으로 예상됩니다.
## API 테스트
### 테스트 시나리오
```test.js 
export const options = {
  scenarios: {
    token_scenario: {
      exec: 'token_scenario',
      executor: 'ramping-vus',
      startVUs: 500,
      stages: [
         { duration: '1m', target: 500}, //평시 트래픽
        { duration: '1m', target: 500 },  // 부하테스트 Ramp up to 500 VUs over 1 minute 
        { duration: '2m', target: 500 },   // 내구성테스트 Stay at 500 VUs for 2 minute 
        { duration: '2m', target: 2000 },  // 스트레스테스트 Stay at 2000 VUs for 2 minute 
        { duration: '1m', target: 2000 }, // 피크테스트 Ramp up to 2000 VUs over 1 minute 
        { duration: '1m', target: 0 },    // 부하감소 Ramp down to 0 VUs over 1 minute 
      ],
    },
```
#### 대기열 진입 / 조회
```

function checkToken(userId, token){
  const tokenUrl = `${API_BASE_URL}/api/users/` + userId + `/token`;

  const tokenPayload = JSON.stringify({ userId: userId});
  const tokenParams = {
    headers: {
      'accept': '*/*',
      'Authorization': token,
      'Content-Type': 'application/json',
    },
  };
  const tokenQueryResponse = http.get(tokenUrl, tokenParams, 2147483647);

  check(tokenQueryResponse, {
    'tokenQuery status is 200': (r) => r.status === 200,
  });
  const responseBody = JSON.parse(tokenQueryResponse.body);
  return responseBody.order;
}

function issueToken(userId){
  const tokenUrl = `${API_BASE_URL}/api/users/` + userId + `/token`;

  const tokenPayload = JSON.stringify({ userId: userId});
  const tokenParams = { headers: { 'Content-Type': 'application/json' } };
  const tokenQueryResponse = http.post(tokenUrl, tokenPayload, tokenParams, 2147483647);

  check(tokenQueryResponse, {
    'tokenQuery status is 200': (r) => r.status === 200,
  });
  const processing = tokenQueryResponse.timings.duration - tokenQueryResponse.timings.waiting;
  processingTime.add(processing);
  const responseBody = JSON.parse(tokenQueryResponse.body);
  return responseBody.accessKey;
}

export function token_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);

  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    sleep(1); //1초 간격으로 풀링
  }
}
```


##### 테스트 결과
![20240823_003230](https://github.com/user-attachments/assets/adb6280c-7cc4-4c98-9131-3cc0e3f7f88f)
![20240823_003248](https://github.com/user-attachments/assets/62e38227-70d2-4345-a9dc-57018399495d)
![20240823_010435](https://github.com/user-attachments/assets/00c1fc38-07a0-4828-a387-0e42d0e07abc)


1. 테스트 규모
    - 총 요청 수 : 240,208
    - TPS : 471.71 요청/초
2. 성공률
    - 99.97%의 체크 통과
    - HTTP 요청 실패율: 0.02% (54건 실패)
3. 응답 시간
    - avg : 1.19초
    - P50 : 271.2 ms
    - P95 : 4.92 s
    - P99 : 5.39 s
    - 최대 응답 시간 : 59.99초

##### 테스트 분석
1. 고려 사항
    - 1분의 최대 응답시간으로 타임아웃이 발생하여 성능 저하 발생
    - 대부분의 시간이 네트워크 지연보다는 서버 측 처리 병목 현상일 가능성 높음
    - `Tomcat Current Connections` 지표를 확인하여 스트레스테스트 구간에서 톰캣 최대 연결 수 도달
2. 개선 사항
    - `server.tomcat.accept-count` 대신 `server.tomcat.max-connections` 증가
3. 1차 개선 후 결과

|         | 요청 처리량  | TPS        | 평균 응답시간 | P50 응답시간  | P95 응답시간 | P99 응답시간 | 최대 응답시간 | 실패율   |
|---------| ------- | ---------- | ------- | --------- | -------- | -------- | ------- | ----- |
| 개선 전    | 240,208 | 471.71 / s | 1.19 s  | 271.2 ms  | 4.92 s   | 5.39 s   | 59.99 s | 0.02% |
| 1차 개선 후 | 230,916 | 453.80 / s | 1.28 s  | 510.42 ms | 4.98 s   | 5.41 s   | 7.02 s  | 0.01% |

- 실패율이 감소했고 최대 응답시간이 감소했습니다.
- 더 많은 동시 요청을 처리할 수 있었지만, 개별 요청의 처리 시간이 늘어났습니다.
- 더 많은 동시 연결을 처리하면서 DB 풀에 대한 부하가 증가할 수 있습니다.
- 높은 동시성으로 인해 사용자가 토큰 재발급 후, 재발급 전의 토큰을 조회하는 상황이 발생하게 됩니다.
    - 문제상황 (401, 404 오류)
        - 사용자 A가 여러 기기 또는 탭에서 동시에 서비스를 이용 중입니다.
        - 기기 1에서 토큰 재발급이 발생했습니다.
        - 기기 2에서는 아직 이전 토큰을 사용하고 있습니다.
        - 기기 2에서 이전 토큰으로 대기열에 재진입합니다.
    - 해결
        - 그레이스 기간 설정 : 새 토큰 발급 후 짧은 시간 동안 이전 토큰도 유효하게 처리합니다.

  ![사진1](https://github.com/user-attachments/assets/809346a0-28e1-4303-98fb-68f5cb069de6)
- 최대 연결 수를 늘려도 연결 수가 2002개를 넘어가지 않으며 유의미하게 연결 수가 증가하지 않는데
이를 해결하기 위해 `server.tomcat.accept-count`와 `server.tomcat.threads.max`를 함께 조정하고 최적의 값을 찾아야 합니다.

```java
server.tomcat.threads.max=400
server.tomcat.max-connections=3000
server.tomcat.accept-count=3000
```

- 이를 위해 `server.tomcat.threads.max`를 증가시켜 `server.tomcat.max-connections`을 최대한 활용하게끔 설정합니다.

4. 2차 개선 후

    ![사진2](https://github.com/user-attachments/assets/e67a4b2c-ddbd-40b1-9815-a46511eba28d)


    |         | 요청 처리량  | TPS        | 평균 응답시간 | P50 응답시간  | P95 응답시간 | P99 응답시간 | 최대 응답시간 | 실패율   |
    | ------- | ------- |------------| ------- | --------- | -------- | -------- | ------- | ----- |
    | 개선 전    | 240,208 | 471.71 / s | 1.19 s  | 271.2 ms  | 4.92 s   | 5.39 s   | 59.99 s | 0.02% |
    | 1차 개선 후 | 230,916 | 453.80 / s | 1.28 s  | 510.42 ms | 4.98 s   | 5.41 s   | 7.02 s  | 0.01% |
    | 2차 개선 후 | 248,268 | 488.00 / s | 1.12 s  | 97.37 ms  | 4.78 s   | 5.42 s   | 7.27 s  | 0.02% |
    
    - 2차 개선 후 요청 처리량이 가장 높아졌으며, 초당 처리 요청 수가 약 3.5% 증가했습니다.
    - 특히 P50 값이 대폭 감소하여 대부분의 요청이 더 빠르게 처리되고 있음을 알 수 있습니다.
    - **1차 개선 후 분석한 문제상황에 대해 재검토가 필요합니다.**

#### 콘서트 조회
```java
function concertRetrieve(token){

  const tokenUrl = `${API_BASE_URL}/api/concerts`;

  const tokenParams = {
    headers: {
      'accept': '*/*',
      'Authorization': token,
      'Content-Type': 'application/json',
    },
  };
  const concertRetrieveResponse = http.get(tokenUrl, tokenParams, 2147483647);

  check(concertRetrieveResponse, {
    'checkToken status is 200': (r) => r.status === 200,
  });

  if (concertRetrieveResponse.status === 401) {
    status401Counter.add(1);
  } else if (concertRetrieveResponse.status === 404) {
    status404Counter.add(1);
  } else if (concertRetrieveResponse.status === 500) {
    status500Counter.add(1);
  } else if (concertRetrieveResponse.status !== 200) {
    otherStatusCounter.add(1);
  }

  const responseBody = JSON.parse(concertRetrieveResponse.body);
  return responseBody.concerts;
}

export function concert_retrieve_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);

  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      concertRetrieve(token);
      break;
    }
    sleep(1);
  }
}
```

##### 테스트 결과
![사진3](https://github.com/user-attachments/assets/6df6d469-1583-484e-a227-c5a64658e0b6)


1. 테스트 규모
    - 총 요청 수 : 424,468
    - TPS : 884.29 요청/초
2. 성공률
    - 2.99%의 체크 통과
    - HTTP 요청 실패율: 97.00% (411,739건 실패)
3. 응답 시간
    - avg : 2.45초
    - P50 : 271.2 ms
    - P95 : 9.83 s
    - P99 : 15.64 s
    - 최대 응답 시간 : 1분 14초
   
##### 테스트 분석
1. 고려 사항
    - 500 오류의 원인을 파악하고 해결해야 합니다. 
    - 401, 404 오류의 원인을 분석하고 적절한 처리 방안을 마련해야 합니다.
2. 개선 사항

![이슈1](https://github.com/user-attachments/assets/850dc73a-47b7-43a5-8683-91f3169458bc)


    - 문제상황
       - 401, 404 오류  
          - `wait_queue`에서 요소를 조회하는 순간 `TokenScheduler`에서 `active_queue`로 요소를 이동시켜 rank 조회 시 null이 반환되는 경우가 발생합니다.
          - null에 대해 intValue() 메소드를 호출하여 `NullPointerException` 발생하며 서버가 죽습니다.

    - 해결
      - try-catch 블록 도입 : rank 조회 시 발생할 수 있는 `NullPointerException`을 포착하여 처리합니다.
       - Null Rank 처리 로직 변경 : rank가 null인 경우, 해당 토큰을 activeToken으로 간주하여 순번이 0인 Token 객체를 반환합니다.
   

![이슈2](https://github.com/user-attachments/assets/06ece123-e8d2-434b-aa75-b179d01a2fd2)


    - 문제상황
      - HikariCP 커넥션 풀
        - 모든 커넥션이 사용 중이고 적시에 커넥션이 반환되지 않습니다.
    - 해결
      - 커넥션 최대 풀 크기 증가 : 서버 리소스를 고려하여 `spring.datasource.hikari.maximum-pool-size`를 조절합니다.

![이슈3](https://github.com/user-attachments/assets/cdd89c9a-8ca1-48cb-bdc7-2f6c7bb6feb2)

![사진7](https://github.com/user-attachments/assets/73e0e6e6-9360-4f9a-9436-adaa9ac33581)


    - 문제상황
        - HikariCP 커넥션 풀
            - 최대 커넥션 풀 크기를 20으로 설정해도 적시에 사용가능한 커넥션이 없어 타임아웃이 발생합니다.
        - 해결
            - 커넥션 타임아웃 증가 : 서버 리소스를 고려하여 `spring.datasource.hikari.connection-timeout`를 조절합니다.

#### 콘서트 날짜 조회
```java
export function concertSchedule_retrieve_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);


  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
  concertRetrieve(token);
  sleep(randomIntBetween(1, 5)); //선택시간
  concertScheduleRetrieve(token);
}

function concertRetrieve(token){

  const tokenUrl = `${API_BASE_URL}/api/concerts`;

  const tokenParams = {
      headers: {
    'accept': '*/*',
        'Authorization': token,
        'Content-Type': 'application/json',
  },
  };
  const concertRetrieveResponse = http.get(tokenUrl, tokenParams, 2147483647);

  check(concertRetrieveResponse, {
      'concertRetrieve status is 200': (r) => r.status === 200,
  });

  if (concertRetrieveResponse.status === 401) {
    status401Counter.add(1);
  } else if (concertRetrieveResponse.status === 404) {
    status404Counter.add(1);
  } else if (concertRetrieveResponse.status === 500) {
    status500Counter.add(1);
  } else if (concertRetrieveResponse.status !== 200) {
    otherStatusCounter.add(1);
  }

  const responseBody = JSON.parse(concertRetrieveResponse.body);
}
```
##### 테스트 결과

![사진8](https://github.com/user-attachments/assets/61ae6349-b87c-4dc5-868e-aa1ac7221800)
![사진9](https://github.com/user-attachments/assets/1666a059-fa51-4008-b533-2de03da901c3)
![사진10](https://github.com/user-attachments/assets/06e09782-51c9-4de2-a4d3-d0af4982fb9a)

1. 테스트 규모
    - 총 요청 수 : 47,516
    - TPS : 467.95 / s
2. API 별 성공률
   - issueToken: 99.99% (9,412 성공 / 1 실패)
   - checkToken: 99.75% (21,535 성공 / 54 실패)
   - getConcerts: 99.52% (8,518 성공 / 41 실패)
   - getAvailableConcertSchedules: 99.98% (8,051 성공 / 2 실패)
3. 응답 시간
    - avg : 9.42 s
    - P50 : 7.31 s
    - P95 : 24.83 s
    - P99 : 28.73 s
    - 최대 응답 시간 : 1분 14초

##### 테스트 분석
  1. 고려사항
     - 평군 응답 시간이 다소 높아 개선이 필요합니다.

#### 콘서트 좌석 조회
콘서트 -> 콘서트 날짜 -> 콘서트 좌석 조회, 총 3번의 API의 호출로
성능 저하를 우려하여 `server.tomcat.threads.max`의 값을 500으로 설정합니다.
```java
function concertScheduleSeatRetrieve(token){

  const concertId = 1;
  const concertScheduleId = concertId;
  const url = `${API_BASE_URL}/api/concerts/` + concertId + `/schedules/` + concertScheduleId + `/seats/available`;

  const params = {
    headers: {
      'accept': '*/*',
      'Authorization': token,
      'Content-Type': 'application/json',
    },
  };
  const response = http.get(url, params, 2147483647);

  check(response, {
    'concertScheduleSeatRetrieve status is 200': (r) => r.status === 200,
  });

  if (response.status === 401) {
    status401Counter.add(1);
    return -1;
  } else if (response.status === 404) {
    status404Counter.add(1);
    return -2;
  } else if (response.status === 500) {
    status500Counter.add(1);
    return -3;
  } else if (response.status !== 200) {
    otherStatusCounter.add(1);
    return -4;
  }

  const responseBody = JSON.parse(response.body);
  return 0;
}

export function concertScheduleSeat_retrieve_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);


  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
  concertRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleSeatRetrieve(token);
}
```

##### 테스트 결과
![사진11](https://github.com/user-attachments/assets/c4a73e68-6584-454d-bbd4-fa6758764323)
![사진12](https://github.com/user-attachments/assets/3426be19-2607-4968-8e60-1cebcedf2ca2)
![사진13](https://github.com/user-attachments/assets/4336e886-3f56-4a56-8864-21ba14202ecf)
1. 테스트 규모
    - 총 요청 수 : 51,846
    - TPS : 102.30 / s
2. API 별 성공률
    - issueToken: 100% 성공
    - checkToken: 99.79% 성공 (19,243 성공 / 41 실패)
    - concertRetrieve: 98.89% 성공 (8,314 성공 / 93 실패)
    - concertScheduleRetrieve: 100% 성공
    - concertScheduleSeatRetrieve: 100% 성공
3. 응답 시간
    - avg : 8.69 s
    - P50 : 6.36 s
    - P95 : 23.62 s
    - P99 : 30.09 s

##### 테스트 분석
1. 고려사항
    - 전반적인 응답 시간이 이전 테스트보다 약간 개선되었습니다.

#### 예약
```java
function reservation(userId, concertScheduleId,  seatId, token){


  const reservationUrl = `${API_BASE_URL}/api/reservations`;
  const reservationPayload = JSON.stringify({
    userId: userId,
    concertScheduleId: concertScheduleId,
    seatId: seatId
  });
  const reservationParams = { headers: { 'Content-Type': 'application/json', 'Authorization': token } };
  const response = http.post(reservationUrl, reservationPayload, reservationParams);
  check(response, {
    'reservation status is 200': (r) => r.status === 200,
  });

  if (response.status === 401) {
    status401Counter.add(1);
    return -1;
  } else if (response.status === 404) {
    status404Counter.add(1);
    return -2;
  } else if (response.status === 500) {
    const responseBody = JSON.parse(response.body);
    if (responseBody.code === 'LOCK_ACQUISITION_FAIL'){
      lockFailCounter.add(1);
    }
    else{
      status500Counter.add(1);
    }
    return -3;
  } else if (response.status !== 200) {
    otherStatusCounter.add(1);
    return -4;
  }
}

export function reservation_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);


  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
  concertRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleSeatRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  reservation(userId,
      randomIntBetween(1, 5), // 동시에 같은 좌석 조회하는 상황 연출하기 위한 범위 축소
      randomIntBetween(1,100),
      token);
}
```

##### 테스트 결과

![사진14](https://github.com/user-attachments/assets/5fabb48c-ac2e-4c81-a89f-0aa2a1d74df8)
![사진15](https://github.com/user-attachments/assets/26bb9151-15c6-4922-9d18-5eec56eb1fc2)
![사진16](https://github.com/user-attachments/assets/de3271b5-b8c6-4265-9354-9034f0efe8a0)

1. 테스트 규모
    - 총 요청 수 : 52,302
    - TPS : 103.46 / s
2. API 별 성공률
    - issueToken: 99% (8,147 성공, 4 실패)
    - checkToken: 99% (14,606 성공, 30 실패)
    - concertRetrieve: 98% (7,859 성공, 93 실패)
    - concertScheduleRetrieve: 99% (7,596 성공, 4 실패)
    - concertScheduleSeatRetrieve: 99% (7,159 성공, 4 실패)
    - reservation: 7% (500 성공, 6,300 실패)
3. 응답 시간
    - avg : 8.57 s
    - P50 : 5.79 s
    - P95 : 25.33 s
    - P99 : 29.25 s

##### 테스트 분석
1. 고려사항
    - `reservations` 엔드포인트의 실패 횟수는 동시에 하나의 좌석을 예약할 때 발생하는 실패 요청입니다.
        `lock_fail` 값에 나타납니다.
    - 95%의 요청이 25.33초 이내에 처리되고 있어, 많은 사용자가 긴 대기 시간을 경험할 것으로 예상됩니다.
    - 토큰 활성화 빈도를 낮추거나 활성화 토큰의 개수를 줄여 부하를 단계적으로 완화해야 합니다.

#### 포인트 조회 / 충전
![사진17](https://github.com/user-attachments/assets/eb4c6acd-4bb5-4ad8-bdb3-c416b14336dc)
![사진18](https://github.com/user-attachments/assets/cac2e03d-105e-424f-8b60-bdd851590a82)
![사진19](https://github.com/user-attachments/assets/30cc2b70-0b6b-435a-bf04-ba2d4b3a8bff)

1. 테스트 규모
    - 총 요청 수 : 161,114
    - TPS : 321.40 / s
2. API 별 성공률
    - issueToken: 100% (모든 요청 성공)
    - checkToken: 99% (117,665 성공, 38 실패)
    - pointRetrieve: 99% (14,380 성공, 2 실패)
    - pointCharge: 100% (모든 요청 성공)
3. 응답 시간
    - avg : 2.26 s
    - P50 : 149.42ms
    - P95 : 14.4 s
    - P99 :  18.49 s
    - 최대 응답 시간: 23.93 s

##### 테스트 분석
1. 고려사항
    - HTTP 404 오류(40건)만 발생하고 있어, 서버 내부 오류는 없는 것으로 보입니다.
    - 404 오류의 원인을 파악하여 추가적인 개선이 가능합니다.
    - 토큰 활성화 빈도를 낮추거나 활성화 토큰의 개수를 줄여 부하를 단계적으로 완화해야 합니다.

#### 결제

콘서트 -> 콘서트 날짜 -> 콘서트 좌석 조회 -> 예약 -> 결제, 총 6번의 API의 호출로
성능 저하를 우려하여 `server.tomcat.threads.max`의 값을 600으로 설정합니다.
`spring.datasource.hikari.maximum-pool-size`의 값을 30으로 설정합니다.

```java

function payment(token, userId, reservationId){
  const url = `${API_BASE_URL}/api/payments`;
  const payload = JSON.stringify({
    userId: userId,
    reservationId: reservationId
  });
  const params = { headers: { 'Content-Type': 'application/json', 'Authorization': token } };
  const response = http.post(url, payload, params);
  check(response, {
    'payment status is 200': (r) => r.status === 200,
  });

  if (response.status === 401) {
    status401Counter.add(1);
    return -1;
  } else if (response.status === 404) {
    status404Counter.add(1);
    return -2;
  } else if (response.status === 500) {
    const responseBody = JSON.parse(response.body);
    if (responseBody.code === 'LOCK_ACQUISITION_FAIL'){
      lockFailCounter.add(1);
    }
    else{
      status500Counter.add(1);
    }
    return -3;
  } else if (response.status !== 200) {
    otherStatusCounter.add(1);
    return -4;
  }
}

export function payment_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);


  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
  concertRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleSeatRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  const reservationId = reservation(userId,
      randomIntBetween(1, 99999),
      randomIntBetween(1,100),
      token);
  if (Math.random() < 0.5){ // 충전 할 수도 있고 안할 수도 있음
    pointRetrieve(token, userId);
    sleep(1); //선택시간
    pointCharge(token, userId, randomIntBetween(1,100000));
  }
  sleep(1);
  if (reservationId !== null){
    payment(token, userId, reservationId);
  }
}
```

##### 테스트 결과
![사진20](https://github.com/user-attachments/assets/967a3c53-9927-4649-b64c-62294ff7e794)
![사진21](https://github.com/user-attachments/assets/c958d38c-e90b-4a3c-90d1-c53b6c43a1af)
![사진22](https://github.com/user-attachments/assets/685d1890-4ea3-4bce-8879-f75b3ed83a00)

1. 테스트 규모
    - 총 요청 수 :  66,298
    - TPS : 132.06 / s
2. API 별 성공률
    - issueToken: 100% (모든 요청 성공)
    - checkToken: 99% (16,983 성공, 45 실패)
    - concertRetrieve: 98% (7,383 성공, 92 실패)
    - concertScheduleRetrieve: 100% (모든 요청 성공)
    - concertScheduleSeatRetrieve: concertScheduleSeatRet
    - reservation: 99% (6889 성공, 4 실패 중 3 락 획득 실패)
    - payment : 99% (6393 성공, 4 실패)
3. 응답 시간
    - avg : 6.63 s
    - P50 : 4.18 s
    - P95 : 19.72 s
    - P99 : 24.23 s
    - 최대 응답 시간: 37.11 s

##### 테스트 분석
1. 고려사항
    - 404 오류(49건)의 원인을 파악하고, 클라이언트 요청과 서버의 라우팅을 재검토해야 합니다.
    - 500 오류(93건)의 원인을 파악하고 해결해야 합니다.
