package io.hhplus.concert_reservation_service_java.domain.payment.event;

import io.hhplus.concert_reservation_service_java.domain.common.DataPlatformClient;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentEvent {

  @Component
  public class Publisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public Publisher(ApplicationEventPublisher applicationEventPublisher) {
      this.applicationEventPublisher = applicationEventPublisher;
    }

      public void success (PaymentSuccessEvent event){
      applicationEventPublisher.publishEvent(event);
    }
  }

  @Component
  public class Listener {
    private final DataPlatformClient dataPlatformClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Listener(DataPlatformClient dataPlatformClient,
        KafkaTemplate<String, Object> kafkaTemplate) {
      this.dataPlatformClient = dataPlatformClient;
      this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentSuccessEvent event) {
      try{
        kafkaTemplate.send("payment-topic", String.valueOf(event.getPayment()));
        dataPlatformClient.send("PAYMENT_CREATED", event.getPayment());
      } catch (Exception e) {
        throw new CustomException(ErrorCode.THIRD_PARTY_ISSUE);
      }
    }
  }
}
