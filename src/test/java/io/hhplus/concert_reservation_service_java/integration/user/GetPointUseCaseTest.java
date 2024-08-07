
package io.hhplus.concert_reservation_service_java.integration.user;


import io.hhplus.concert_reservation_service_java.domain.user.GetPointUseCase;
import io.hhplus.concert_reservation_service_java.domain.user.UserService;
import io.hhplus.concert_reservation_service_java.domain.user.application.port.in.GetPointCommand;
import io.hhplus.concert_reservation_service_java.domain.user.application.useCase.GetPointUseCaseImpl;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.User;
import io.hhplus.concert_reservation_service_java.domain.user.infrastructure.jpa.UserRepository;
import io.hhplus.concert_reservation_service_java.exception.CustomException;
import io.hhplus.concert_reservation_service_java.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class GetPointUseCaseTest {
  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GetPointUseCase useCase;

  private static final int USER_COUNT = 100000; // 10만 명의 사용자
  private static final int QUERY_COUNT = 1000; // 1000번의 조회
  private static final int THREAD_COUNT = 10; // 10개의 스레드로 병렬 처리

  @Test
  @DisplayName("대량의 유저 생성 및 다중 조회 성능 테스트")
  void testLargeScaleUserPointQueries() throws Exception {

    createManyTestUser();

    ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    List<CompletableFuture<Void>> futures = new ArrayList<>();
    Random random = new Random();

    long startTime = System.currentTimeMillis();

    for (int i = 0; i < QUERY_COUNT; i++) {
      long userId = random.nextInt(USER_COUNT) + 1; // 1부터 USER_COUNT 사이의 랜덤 ID
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        GetPointCommand command = GetPointCommand.builder()
            .reserverId(userId)
            .build();
        int point = useCase.execute(command);
        assertNotNull(point);
      }, executorService);
      futures.add(future);
    }

    // 모든 비동기 작업이 완료될 때까지 대기
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    System.out.printf("총 %d개의 조회를 %d ms 동안 처리했습니다. 평균 처리 시간: %.2f ms%n",
        QUERY_COUNT, duration, (float)duration / QUERY_COUNT);

    executorService.shutdown();
  }

  @Test
  @DisplayName("유저 포인트 조회 성공")
  void execute_ShouldReturnPointFromUserService() {
    // Given
    long reserverId = 1L;
    int expectedPoint = 100;

    // Assuming you have a method to create a test user
    createTestUser(reserverId, expectedPoint);

    GetPointCommand command = GetPointCommand.builder()
        .reserverId(reserverId)
        .build();

    // When
    int result = useCase.execute(command);

    // Then
    assertEquals(expectedPoint, result);
  }

  @Test
  @DisplayName("예약자를 찾을 수 없을 때 - CustomException")
  void execute_WhenUserServiceThrowsException_ShouldThrowException() {
    // Given
    long reserverId = 1L;
    GetPointCommand command = GetPointCommand.builder()
        .reserverId(reserverId)
        .build();

    // Assuming the user does not exist
    // When
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(CustomException.class)
        .satisfies(thrown -> {
          CustomException exception = (CustomException) thrown;
          assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        });
  }

  // Helper method to create a test user
  private void createTestUser(long reserverId, int point) {
    User user = User.builder()
        .id(reserverId)
        .point(point)
        .build();
    userRepository.save(user);
  }

  private void createManyTestUser() {
    List<User> users = new ArrayList<>();
    Random random = new Random();

    for (long i = 1; i <= USER_COUNT; i++) {
      User user = User.builder()
          .id(i)
          .point(random.nextInt(10000)) // 0부터 9999 사이의 랜덤 포인트
          .build();
      users.add(user);

      // 메모리 효율성을 위해 1000개씩 벌크 저장
      if (i % 1000 == 0) {
        userRepository.saveAll(users);
        users.clear();
      }
    }

    // 남은 사용자들 저장
    if (!users.isEmpty()) {
      userRepository.saveAll(users);
    }
  }

}