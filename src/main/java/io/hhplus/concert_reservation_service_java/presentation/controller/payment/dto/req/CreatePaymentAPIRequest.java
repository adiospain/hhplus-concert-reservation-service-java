package io.hhplus.concert_reservation_service_java.presentation.controller.payment.dto.req;


public record CreatePaymentAPIRequest (
    long reserverId,
    long reservationId){

}
