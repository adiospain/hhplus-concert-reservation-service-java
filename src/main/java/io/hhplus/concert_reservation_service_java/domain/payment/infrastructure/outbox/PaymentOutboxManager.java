package io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox;

import io.hhplus.concert_reservation_service_java.domain.common.outbox.Outbox;
import io.hhplus.concert_reservation_service_java.domain.common.outbox.OutboxManager;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.event.PaymentEvent;
import io.hhplus.concert_reservation_service_java.domain.payment.infrastructure.outbox.jpa.PaymentOutbox;

public interface PaymentOutboxManager extends OutboxManager {

}
