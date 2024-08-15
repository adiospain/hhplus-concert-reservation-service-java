# Kafka를 활용한 이벤트 주도 설계 및 Transactional Outbox 패턴

## 결제 서비스 로직
1. **결제 요청 검증** : 결제 요청이 올바른지 확인합니다.
2. **유저 포인트 차감** : 결제에 필요한 만큼 유저의 포인트를 차감합니다.
3. **결제 정보 저장** : 결제 정보를 데이터베이스에 저장합니다.
4. **토큰 만료** : 결제 완료 시 사용한 토큰을 만료 시킵니다.

결제 정보 저장하는 작업이 오래 걸리거나
실패할 경우, 전체 결제 서비스 트랜잭션에 영향을 미치게 됩니다.
이는 결제 처리의 주요 로직에 부가 로직이 영향을 미치는 상황이 초래됩니다.

또한 결제 정보 외의 토큰 관련 로직의 개입으로 인해
부가 로직이 결체 서비스 로직에 영향을 줄 수 있습니다.

## Trasactional Outbox 패턴
결제 정보 저장의 실패가 결제 서비스 로직에 영향을 주지 않게 하기 위해
Trasactional Outbox 패턴을 적용합니다.

1. **데이터베이스 트랜잭션 내 이벤트 저장** 
    - 주문 정보와 함께 Outbox 테이블에 발행할 이벤트를 저장합니다. 데이터의 일관성을 유지하기 위해
    서비스 로직과 같은 트랜잭션 내에서 커밋 되기 전 작업을 처리 합니다.
2. **비동기 프로세스를 통한 이벤트 발행**
   - `ApplicationEventPublisher`와 `ApplicationEventListener`를 이벤트 발행 시 `PaymentOutbox`를 생성합니다.
   - Kafka 브로커를 통해 `PaymentOutbox`가 전달 되면 해당 outbox를 마킹합니다.
   - 마킹된 outbox는 처리가 완료된 이벤트로 간주하여 Outbox 테이블에서 삭제함으로써 중복 전송을 방지합니다.

## 이벤트 처리 시퀀스 다이어그램
 ```mermaid
sequenceDiagram
	participant UseCase
	participant EventPublisher
	participant EventListener
	participant MessageSender
	participant KafkaMessageProducer
	
break 트랜잭션 커밋 전
  UseCase ->>+ EventPublisher: 메서드 실행
  note over EventPublisher: EventPublisher.execute()
  note over EventPublisher: ApplicationEventPublisher.publishEvent()
  EventPublisher ->>+ EventListener: 애플리케이션 이벤트 발행
	  note over EventListener: @TransactionalEventListener(phase = BEFORE_COMMIT)
	  note over EventListener: EventListener.createOutbox()
	  note over EventListener: CustomEvent.createOutboxMessage()
	  note over EventListener: OutboxManager.create()
  EventListener ->>+ OutboxManager : outbox 생성
  OutboxManager ->>+ Repository : 개별 outbox 테이블에 outbox 생성
end 

break 트랜잭션 커밋 후
note over EventPublisher: EventPublisher.execute()
  note over EventPublisher: ApplicationEventPublisher.publishEvent()
  EventPublisher ->>+ EventListener: 애플리케이션 이벤트 발행
  	  note over EventListener: @TransactionalEventListener(phase = AFTER_COMMIT)
	  note over EventListener: EventListener.sendMessage()
	  note over EventListener: MessageSender.send()
	  EventListener ->>+ MessageSender: 메시지 전송
	  note over MessageSender: KafkaMessageProducer.send()
	  MessageSender ->>+ KafkaMessageProducer: 카프카 토픽 생산
   
end 
```

 ```mermaid
sequenceDiagram
	participant KafkaMessageConsumer
	participant OutboxManager
	participant Repository

break 트랜잭션 커밋 후

	  KafkaMessageConsumer ->>+ OutboxManager: 메시지 발행 검증
    note over OutboxManager: OutboxManager.markComplete()
	  OutboxManager ->>+ Repository : 개별 outbox 테이블에 outbox 마크
    note over Repository: Repository.markComplete()
end
```
 ```mermaid
sequenceDiagram
	participant OutboxScheduler
	participant Repository

break 스케줄러 : 작업 완료된 outbox 삭제

	  OutboxScheduler ->>+ Repository : 개별 outbox 테이블에 outbox 마크
    note over Repository: Repository.deleteCompleted()
end
```

## 개선 후 결제 서비스 로직 다이어그램
