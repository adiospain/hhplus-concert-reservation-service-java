package io.hhplus.concert_reservation_service_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class ConcertReservationServiceJavaApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConcertReservationServiceJavaApplication.class, args);
  }
}