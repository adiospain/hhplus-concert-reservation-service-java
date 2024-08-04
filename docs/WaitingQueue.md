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