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