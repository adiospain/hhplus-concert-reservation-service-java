package io.hhplus.concert_reservation_service_java.domain;

import jakarta.persistence.Entity;
import java.util.List;


public class Concert {
  private long id;
  private String name;
  private List<ConcertSchedule> concertSchedules;
}
