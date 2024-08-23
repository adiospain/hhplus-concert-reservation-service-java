# 장애 대응

- 높은 동시성으로 인해 사용자가 토큰 재발급 후, 재발급 전의 토큰을 조회하는 상황이 발생하게 됩니다.
    - 문제상황 (401, 404 오류)
        - 사용자 A가 여러 기기 또는 탭에서 동시에 서비스를 이용 중입니다.
        - 기기 1에서 토큰 재발급이 발생했습니다.
        - 기기 2에서는 아직 이전 토큰을 사용하고 있습니다.
        - 기기 2에서 이전 토큰으로 대기열에 재진입합니다.
      - 해결
        - try-catch 블록 도입 : rank 조회 시 발생할 수 있는 `NullPointerException`을 포착하여 처리합니다.
         - Null Rank 처리 로직 변경 : rank가 null인 경우, 해당 토큰을 activeToken으로 간주하여 순번이 0인 Token 객체를 반환합니다.
  - 문제상황
     - 모든 커넥션이 사용 중이고 적시에 커넥션이 반환되지 않습니다.
     - 해결
      - 커넥션 최대 풀 크기 증가 : 서버 리소스를 고려하여 `spring.datasource.hikari.maximum-pool-size`를 조절합니다.
  - 문제상황
    - 최대 커넥션 풀 크기를 20으로 설정해도 적시에 사용가능한 커넥션이 없어 타임아웃이 발생합니다.
    - 해결
      - 커넥션 타임아웃 증가 : 서버 리소스를 고려하여 `spring.datasource.hikari.connection-timeout`를 조절합니다.
     
- 이벤트 비동기 처리 후 `TransactionSynchronization.afterCompletion`에서 `Exception` 발생합니다.
    - 문제상황
        - 트랜잭션 내에서 이벤트가 발생하면, @TransactionalEventListener가 이를 감지합니다.
        - 기본값으로 @TransactionalEventListener는 커밋된 직후, `TransactionPhase.AFTER_COMMIT` 단계에서 동작합니다.
        - 그러나 실제로는 afterCommit()이 아닌 afterCompletion()에서 이벤트가 처리됩니다.
        - `TransactionSynchronizationUtils.invokeAfterCompletion` 메소드가 호출되어 등록된 모든 TransactionSynchronization 객체의 afterCompletion() 메소드를 실행합니다.
        - afterCompletion() 메소드 내에서 발생하는 예외는 캐치되어 로그로만 남고 전파되지 않습니다.
    - 해결 방안 및 개선 전략:
      -  명시적 에러 핸들링
        ```java
        
        @TransactionalEventListener
        public void handleEvent(MyEvent event) {
            try {
                // 이벤트 처리 로직
            } catch (Exception e) {
                // 예외 로깅
                log.error("Event processing failed", e);
                // 필요한 경우 추가적인 에러 처리 로직
                notify(e);
            }
        }
        ```
