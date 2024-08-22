# 부하테스트

대용량 트래픽에서는 가장 큰 병목현상이 발생하는 곳은 DB connection이 발생할 때 입니다.

하나의 트랜잭션이 너무 길다면, 한 작업이 DB Connection 스레드를 을 오래 붙들고 있게됩니다.


토큰 처리 부하테스트

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
| 콘서트 조회      |            |
| 콘서트 날짜 조회   |            |
| 콘서트 좌석 선택   |            |
| 예약          |            |
| 포인트 조회 / 충전 |            |
| 결제          |            |

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
  
[사진1]
- 최대 연결 수를 늘려도 연결 수가 2002개를 넘어가지 않으며 유의미하게 연결 수가 증가하지 않는데
이를 해결하기 위해 `server.tomcat.accept-count`와 `server.tomcat.threads.max`를 함께 조정하고 최적의 값을 찾아야 합니다.

```java
server.tomcat.threads.max=400
server.tomcat.max-connections=3000
server.tomcat.accept-count=3000
```

- 이를 위해 `server.tomcat.threads.max`를 증가시켜 `server.tomcat.max-connections`을 최대한 활용하게끔 설정합니다.

4. 2차 개선 후

    [사진2]

    |         | 요청 처리량  | TPS        | 평균 응답시간 | P50 응답시간  | P95 응답시간 | P99 응답시간 | 최대 응답시간 | 실패율   |
    | ------- | ------- |------------| ------- | --------- | -------- | -------- | ------- | ----- |
    | 개선 전    | 240,208 | 471.71 / s | 1.19 s  | 271.2 ms  | 4.92 s   | 5.39 s   | 59.99 s | 0.02% |
    | 1차 개선 후 | 230,916 | 453.80 / s | 1.28 s  | 510.42 ms | 4.98 s   | 5.41 s   | 7.02 s  | 0.01% |
    | 2차 개선 후 | 248,268 | 488.00 / s | 1.12 s  | 97.37 ms  | 4.78 s   | 5.42 s   | 7.27 s  | 0.02% |
    
    - 2차 개선 후 요청 처리량이 가장 높아졌으며, 초당 처리 요청 수가 약 3.5% 증가했습니다.
    - 특히 P50 값이 대폭 감소하여 대부분의 요청이 더 빠르게 처리되고 있음을 알 수 있습니다.
    - **1차 개선 후 분석한 문제상황에 대해 재검토가 필요합니다.**

#### 콘서트 조회
