#!/bin/bash
# Number of times to run the test
RUN_COUNT=10
INTERVAL=60
GRADLE_COMMAND="./gradlew clean test --rerun-tasks --info --tests 'io.hhplus.concert_reservation_service_java.integration.useCase.user.CreateReservationUseCaseIntegrationTest.동시에_여러_사용자가_같은_좌석_예약_요청시_하나만_성공해야함'"

for ((i=1; i<=RUN_COUNT; i++))
do
    echo "$i번째 반복 실행..."
    eval $GRADLE_COMMAND

    if [ $i -lt $RUN_COUNT ]; then
        echo "분산락 키 리셋 시작"
        docker exec -it 61d71c7f7f46 redis-cli FLUSHDB
        echo "분산락 키 리셋 종료"
        next_run=$((i + 1))
        echo "다음 $next_run번째 실행을 위해 $INTERVAL초를 기다립니다.."
        sleep $INTERVAL
    fi
done

echo "모든 실행 완료"