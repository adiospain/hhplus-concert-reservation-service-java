package io.hhplus.concert_reservation_service_java.presentation.dto.res;

import java.time.LocalDateTime;
import org.springframework.cglib.core.Local;

public record IssueTokenAPIResponse(long id, LocalDateTime expireAt) {

}
