package io.hhplus.concert_reservation_service_java.presentation.controller.reservation.dto.req;

public record CreateReservationAPIRequest (
    long userId,
    long concertScheduleId,
    long seatId
){

}
