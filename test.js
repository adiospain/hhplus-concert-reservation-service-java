import http from 'k6/http';
import { check, sleep, group} from 'k6';
import { Trend, Counter } from 'k6/metrics';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

const processingTime = new Trend ('processing_time');
const status401Counter = new Counter ('status_401');
const status404Counter = new Counter('status_404');
const status500Counter = new Counter('status_500');
const otherStatusCounter = new Counter('status_other');
const lockFailCounter = new Counter('lock_fail');

export const options = {
  scenarios: {
    // token_scenario: {
    //   exec: 'token_scenario',
    //   executor: 'ramping-vus',
    //   startVUs: 500,
    //   stages: [
    //     {duration: '1m', target: 500}, //평시 트래픽
    //     {duration: '1m', target: 500},  // Ramp up to 500 VUs over 1 minute 부하테스트
    //     {duration: '2m', target: 500},   // Stay at 500 VUs for 2 minute 내구성테스트
    //     {duration: '2m', target: 2000},  // Stay at 2000 VUs for 2 minute 스트레스테스트
    //     {duration: '1m', target: 2000}, // Ramp up to 2000 VUs over 1 minute 피크테스트
    //     {duration: '1m', target: 0},    // Ramp down to 0 VUs over 1 minute 부하감소
    //   ],
    // },
    // concert_retrieve_scenario: {
    //     exec: 'concert_retrieve_scenario',
    //   executor: 'ramping-vus',
    //   startVUs: 500,
    //   stages: [
    //       {duration: '1m', target: 500}, //평시 트래픽
    //       {duration: '1m', target: 500},  // Ramp up to 500 VUs over 1 minute 부하테스트
    //       {duration: '2m', target: 500},   // Stay at 500 VUs for 2 minute 내구성테스트
    //       {duration: '2m', target: 2000},  // Stay at 2000 VUs for 2 minute 스트레스테스트
    //       {duration: '1m', target: 2000}, // Ramp up to 2000 VUs over 1 minute 피크테스트
    //       {duration: '1m', target: 0},    // Ramp down to 0 VUs over 1 minute 부하감소
    //     ],
    // },
    // concertSchedule_retrieve_scenario: {
    //   exec: 'concertSchedule_retrieve_scenario',
    //   executor: 'ramping-vus',
    //   startVUs: 5,
    //   stages: [
    //     { duration: '1m', target: 5}, //평시 트래픽
    //     { duration: '1m', target: 5 },  // Ramp up to 500 VUs over 1 minute 부하테스트
    //     { duration: '2m', target: 5 },   // Stay at 500 VUs for 2 minute 내구성테스트
    //     { duration: '2m', target: 2 },  // Stay at 2000 VUs for 2 minute 스트레스테스트
    //     { duration: '1m', target: 2 }, // Ramp up to 2000 VUs over 1 minute 피크테스트
    //     { duration: '1m', target: 0 },    // Ramp down to 0 VUs over 1 minute 부하감소
    //   ],
    // },
    // concertScheduleSeat_retrieve_scenario: {
    //   exec: 'concertScheduleSeat_retrieve_scenario',
    //   executor: 'ramping-vus',
    //   startVUs: 500,
    //   stages: [
    //     { duration: '1m', target: 500}, //평시 트래픽
    //     { duration: '1m', target: 500 },  // Ramp up to 500 VUs over 1 minute 부하테스트
    //     { duration: '2m', target: 500 },   // Stay at 500 VUs for 2 minute 내구성테스트
    //     { duration: '2m', target: 2000 },  // Stay at 2000 VUs for 2 minute 스트레스테스트
    //     { duration: '1m', target: 2000 }, // Ramp up to 2000 VUs over 1 minute 피크테스트
    //     { duration: '1m', target: 0 },    // Ramp down to 0 VUs over 1 minute 부하감소
    //   ],
    // },
    // reservation_scenario: {
    //   exec: 'reservation_scenario',
    //   executor: 'ramping-vus',
    //   startVUs: 500,
    //   stages: [
    //     { duration: '1m', target: 500}, //평시 트래픽
    //     { duration: '1m', target: 500 },  // Ramp up to 500 VUs over 1 minute 부하테스트
    //     { duration: '2m', target: 500 },   // Stay at 500 VUs for 2 minute 내구성테스트
    //     { duration: '2m', target: 2000 },  // Stay at 2000 VUs for 2 minute 스트레스테스트
    //     { duration: '1m', target: 2000 }, // Ramp up to 2000 VUs over 1 minute 피크테스트
    //     { duration: '1m', target: 0 },    // Ramp down to 0 VUs over 1 minute 부하감소
    //   ],
    // },
    // point_scenario: {
    //   exec: 'point_scenario',
    //   executor: 'ramping-vus',
    //   startVUs: 500,
    //   stages: [
    //     { duration: '1m', target: 500}, //평시 트래픽
    //     { duration: '1m', target: 500 },  // Ramp up to 500 VUs over 1 minute 부하테스트
    //     { duration: '2m', target: 500 },   // Stay at 500 VUs for 2 minute 내구성테스트
    //     { duration: '2m', target: 2000},  // Stay at 2000 VUs for 2 minute 스트레스테스트
    //     { duration: '1m', target: 2000 }, // Ramp up to 2000 VUs over 1 minute 피크테스트
    //     { duration: '1m', target: 0 },    // Ramp down to 0 VUs over 1 minute 부하감소
    //   ],
    // },
    payment_scenario: {
      exec: 'payment_scenario',
      executor: 'ramping-vus',
      startVUs: 500,
      stages: [
        { duration: '1m', target: 500}, //평시 트래픽
        { duration: '1m', target: 500 },  // Ramp up to 500 VUs over 1 minute 부하테스트
        { duration: '2m', target: 500 },   // Stay at 500 VUs for 2 minute 내구성테스트
        { duration: '2m', target: 2000},  // Stay at 2000 VUs for 2 minute 스트레스테스트
        { duration: '1m', target: 2000 }, // Ramp up to 2000 VUs over 1 minute 피크테스트
        { duration: '1m', target: 0 },    // Ramp down to 0 VUs over 1 minute 부하감소
      ],
    }
  }
  // The following section contains configuration options for execution of this
  // test script in Grafana Cloud.
  //
  // See https://grafana.com/docs/grafana-cloud/k6/get-started/run-cloud-tests-from-the-cli/
  // to learn about authoring and running k6 test scripts in Grafana k6 Cloud.
  //
  // cloud: {
  //   // The ID of the project to which the test is assigned in the k6 Cloud UI.
  //   // By default tests are executed in default project.
  //   projectID: "",
  //   // The name of the test in the k6 Cloud UI.
  //   // Test runs with the same name will be grouped.
  //   name: "script.js"
  // },

  // Uncomment this section to enable the use of Browser API in your tests.
  //
  // See https://grafana.com/docs/k6/latest/using-k6-browser/running-browser-tests/ to learn more
  // about using Browser API in your test scripts.
  //
  // scenarios: {
  //   // The scenario name appears in the result summary, tags, and so on.
  //   // You can give the scenario any name, as long as each name in the script is unique.
  //   ui: {
  //     // Executor is a mandatory parameter for browser-based tests.
  //     // Shared iterations in this case tells k6 to reuse VUs to execute iterations.
  //     //
  //     // See https://grafana.com/docs/k6/latest/using-k6/scenarios/executors/ for other executor types.
  //     executor: 'shared-iterations',
  //     options: {
  //       browser: {
  //         // This is a mandatory parameter that instructs k6 to launch and
  //         // connect to a chromium-based browser, and use it to run UI-based
  //         // tests.
  //         type: 'chromium',
  //       },
  //     },
  //   },
  // }
};

const API_BASE_URL  = 'http://localhost:17002'

export function setup() {
  //유저 생성
  const numberOfUsers = 500;
  const url = `${API_BASE_URL}/api/users`;
}

// The function that defines VU logic.
//
// See https://grafana.com/docs/k6/latest/examples/get-started-with-k6/ to learn more
// about authoring k6 scripts.
//

function payment(token, userId, reservationId){
  const url = `${API_BASE_URL}/api/payments`;
  const payload = JSON.stringify({
    userId: userId,
    reservationId: reservationId
  });
  const params = { headers: { 'Content-Type': 'application/json', 'Authorization': token } };
  const response = http.post(url, payload, params);
  check(response, {
    'payment status is 200': (r) => r.status === 200,
  });

  if (response.status === 401) {
    status401Counter.add(1);
    return -1;
  } else if (response.status === 404) {
    status404Counter.add(1);
    return -2;
  } else if (response.status === 500) {
    const responseBody = JSON.parse(response.body);
    if (responseBody.code === 'LOCK_ACQUISITION_FAIL'){
      lockFailCounter.add(1);
    }
    else{
      status500Counter.add(1);
    }
    return -3;
  } else if (response.status !== 200) {
    otherStatusCounter.add(1);
    return -4;
  }
}

export function payment_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);


  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
  concertRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleSeatRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  const reservationId = reservation(userId,
      //1,
      randomIntBetween(1, 99999),
      //9,
      randomIntBetween(1,100),
      token);
  if (Math.random() < 0.5){ // 충전 할 수도 있고 안할 수도 있음
    pointRetrieve(token, userId);
    sleep(1); //선택시간
    pointCharge(token, userId, randomIntBetween(1,100000));
  }
  sleep(1);
  if (reservationId !== null){
    payment(token, userId, reservationId);
  }
}

function pointCharge(token, userId, amount){


  const url = `${API_BASE_URL}/api/users/` + userId + `/charge`;
  const payload = JSON.stringify({
    amount : amount
  });
  const params = { headers: { 'Content-Type': 'application/json', 'Authorization': token } };
  const response = http.patch(url, payload, params);
  check(response, {
    'pointCharge status is 200': (r) => r.status === 200,
  });

  if (response.status === 401) {
    status401Counter.add(1);
    return -1;
  } else if (response.status === 404) {
    status404Counter.add(1);
    return -2;
  } else if (response.status === 500) {
    const responseBody = JSON.parse(response.body);
    if (responseBody.code === 'LOCK_ACQUISITION_FAIL'){
      lockFailCounter.add(1);
    }
    else{
      status500Counter.add(1);
    }
    return -3;
  } else if (response.status !== 200) {
    otherStatusCounter.add(1);
    return -4;
  }
}

function pointRetrieve(token, userId){


  const url = `${API_BASE_URL}/api/users/` + userId + `/point`;
  const params = { headers: { 'Content-Type': 'application/json', 'Authorization': token } };
  const response = http.get(url, params);
  check(response, {
    'pointRetrieve status is 200': (r) => r.status === 200,
  });

  if (response.status === 401) {
    status401Counter.add(1);
    return -1;
  } else if (response.status === 404) {
    status404Counter.add(1);
    return -2;
  } else if (response.status === 500) {
    const responseBody = JSON.parse(response.body);
    if (responseBody.code === 'LOCK_ACQUISITION_FAIL'){
      lockFailCounter.add(1);
    }
    else{
      status500Counter.add(1);
    }
    return -3;
  } else if (response.status !== 200) {
    otherStatusCounter.add(1);
    return -4;
  }
}

export function point_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);


  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
  pointRetrieve(token, userId);
  sleep(randomIntBetween(1, 2)); //선택시간
  pointCharge(token, userId, randomIntBetween(50000, 100000));
}

function reservation(userId, concertScheduleId,  seatId, token){


  const reservationUrl = `${API_BASE_URL}/api/reservations`;
  const reservationPayload = JSON.stringify({
    userId: userId,
    concertScheduleId: concertScheduleId,
    seatId: seatId
  });
  const reservationParams = { headers: { 'Content-Type': 'application/json', 'Authorization': token } };
  const response = http.post(reservationUrl, reservationPayload, reservationParams);
  check(response, {
    'reservation status is 200': (r) => r.status === 200,
  });

  if (response.status === 401) {
    status401Counter.add(1);
    return -1;
  } else if (response.status === 404) {
    status404Counter.add(1);
    return -2;
  } else if (response.status === 500) {
    const responseBody = JSON.parse(response.body);
    if (responseBody.code === 'LOCK_ACQUISITION_FAIL'){
      lockFailCounter.add(1);
    }
    else{
      status500Counter.add(1);
    }
    return -3;
  } else if (response.status !== 200) {
    otherStatusCounter.add(1);
    return -4;
  }
  const responseBody = JSON.parse(response.body);
  return responseBody.reservation.id;
}

export function reservation_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);


  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
  concertRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleSeatRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  reservation(userId,
      //1,
      randomIntBetween(1, 5),
      //9,
      randomIntBetween(1,100),
      token);
}

function concertScheduleSeatRetrieve(token){

  const concertId = 1;
  const concertScheduleId = concertId;
  const url = `${API_BASE_URL}/api/concerts/` + concertId + `/schedules/` + concertScheduleId + `/seats/available`;

  const params = {
    headers: {
      'accept': '*/*',
      'Authorization': token,
      'Content-Type': 'application/json',
    },
  };
  const response = http.get(url, params, 2147483647);

  check(response, {
    'concertScheduleSeatRetrieve status is 200': (r) => r.status === 200,
  });

  if (response.status === 401) {
    status401Counter.add(1);
    return -1;
  } else if (response.status === 404) {
    status404Counter.add(1);
    return -2;
  } else if (response.status === 500) {
    status500Counter.add(1);
    return -3;
  } else if (response.status !== 200) {
    otherStatusCounter.add(1);
    return -4;
  }

  const responseBody = JSON.parse(response.body);
  return 0;
}

export function concertScheduleSeat_retrieve_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);


  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
  concertRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleRetrieve(token);
  sleep(randomIntBetween(1, 2)); //선택시간
  concertScheduleSeatRetrieve(token);
}

function concertScheduleRetrieve(token){

  const concertId = randomIntBetween(1, 99999);
  const url = `${API_BASE_URL}/api/concerts/` + concertId + `/schedules/available`;

  const params = {
    headers: {
      'accept': '*/*',
      'Authorization': token,
      'Content-Type': 'application/json',
    },
  };
  const response = http.get(url, params, 2147483647);

  check(response, {
    'concertScheduleRetrieve status is 200': (r) => r.status === 200,
  });

  if (response.status === 401) {
    status401Counter.add(1);
    return -1;
  } else if (response.status === 404) {
    status404Counter.add(1);
    return -2;
  } else if (response.status === 500) {
    status500Counter.add(1);
    return -3;
  } else if (response.status !== 200) {
    otherStatusCounter.add(1);
    return -4;
  }

  const responseBody = JSON.parse(response.body);
  return 0;
}


export function concertSchedule_retrieve_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);


  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
  concertRetrieve(token);
  sleep(randomIntBetween(1, 5)); //선택시간
  concertScheduleRetrieve(token);
}

function concertRetrieve(token){

  const tokenUrl = `${API_BASE_URL}/api/concerts`;

  const tokenParams = {
    headers: {
      'accept': '*/*',
      'Authorization': token,
      'Content-Type': 'application/json',
    },
  };
  const concertRetrieveResponse = http.get(tokenUrl, tokenParams, 2147483647);

  check(concertRetrieveResponse, {
    'concertRetrieve status is 200': (r) => r.status === 200,
  });

  if (concertRetrieveResponse.status === 401) {
    status401Counter.add(1);
  } else if (concertRetrieveResponse.status === 404) {
    status404Counter.add(1);
  } else if (concertRetrieveResponse.status === 500) {
    status500Counter.add(1);
  } else if (concertRetrieveResponse.status !== 200) {
    otherStatusCounter.add(1);
  }

  const responseBody = JSON.parse(concertRetrieveResponse.body);
}

export function concert_retrieve_scenario() {

  // userId를 1부터 100000 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);

  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
  concertRetrieve(token);
}

export function token_scenario() {

  // userId를 1부터 500 사이에서 랜덤으로 생성
  let userId = randomIntBetween(1, 100000);

  const token = issueToken(userId);

  while (true){
    let order = checkToken(userId, token);
    if (order === 0) {
      break;
    }
    if (order < 0){
      break;
    }
    sleep(1);
  }
}

function checkToken(userId, token){
  const tokenUrl = `${API_BASE_URL}/api/users/` + userId + `/token`;

  if (token === null){
    return -1;
  }

  const tokenPayload = JSON.stringify({ userId: userId});
  const tokenParams = {
    headers: {
      'accept': '*/*',
      'Authorization': token,
      'Content-Type': 'application/json',
    },
  };
  const tokenQueryResponse = http.get(tokenUrl, tokenParams, 2147483647);

  check(tokenQueryResponse, {
    'checkToken status is 200': (r) => r.status === 200,
  });

  if (tokenQueryResponse.status === 401) {
    status401Counter.add(1);
    return -1;
  } else if (tokenQueryResponse.status === 404) {
    status404Counter.add(1);
    return -1;
  } else if (tokenQueryResponse.status === 500) {
    status500Counter.add(1);
    return -1;
  } else if (tokenQueryResponse.status !== 200) {
    otherStatusCounter.add(1);
    return -1;
  }

  const responseBody = JSON.parse(tokenQueryResponse.body);
  if (responseBody.order !== null){
    return responseBody.order;
  }
  else {
    return -1;
  }

}

function issueToken(userId){
  const tokenUrl = `${API_BASE_URL}/api/users/` + userId + `/token`;

  const tokenPayload = JSON.stringify({ userId: userId});
  const tokenParams = { headers: { 'Content-Type': 'application/json' } };
  const tokenQueryResponse = http.post(tokenUrl, tokenPayload, tokenParams, 2147483647);

  check(tokenQueryResponse, {
    'issueToken status is 200': (r) => r.status === 200,
  });
  const processing = tokenQueryResponse.timings.duration - tokenQueryResponse.timings.waiting;
  processingTime.add(processing);
  const responseBody = JSON.parse(tokenQueryResponse.body);
  return responseBody.accessKey;
}



export default function () {
  group('Waiting Token', function () {
    // 토큰 발급
    const userId = Math.floor(Math.random() * 100000) + 1; // 랜덤한 유저 ID 생성

    const tokenUrl = `${API_BASE_URL}/api/users/` + userId + `/token`;

    const tokenPayload = JSON.stringify({ userId: userId});
    const tokenParams = { headers: { 'Content-Type': 'application/json' } };
    const tokenQueryResponse = http.post(tokenUrl, tokenPayload, tokenParams);

    check(tokenQueryResponse, {
      'tokenQuery status is 200': (r) => r.status === 200,
    });


    // 예약

    if (tokenQueryResponse.status === 200){
      const responseBody = JSON.parse(tokenQueryResponse.body);
      const reservationUrl = `${API_BASE_URL}/api/reservation/`;
      const reservationPayload = JSON.stringify({ userId: userId, concertScheduleId: 2, seatId: 1});
      const reservationParams = { headers: { 'Content-Type': 'application/json', 'Authorization': responseBody.accessKey } };
      const reservationQueryResponse = http.post(reservationUrl, reservationPayload, reservationParams);
      console.log('Access Key:', responseBody.accessKey);
      check(reservationQueryResponse, {
        'reservationQuery status is 200': (r) => r.status === 200,
      });
    }


  });
}