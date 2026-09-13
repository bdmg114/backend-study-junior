package com.gdgku.attendance;

import com.gdgku.attendance.Attendance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * [계층 분리 문제] AttendanceController는 "지각 판정" 로직을 check-in API와
 * 관리자 정정(PUT) API에 각각 따로 구현하고 있다.
 * 아래 checkIn과_정정API는_같은_체크인_시각에_대해_같은_상태를_내려야한다() 테스트는
 * 두 API가 같은 체크인 시각에 대해 서로 다른 상태를 내려주는 실제 버그를 재현한다.
 *
 * 이 테스트를 통과시키려면 지각 판정 규칙을 하나의 Service로 뽑아내
 * 두 API가 같은 로직을 공유하도록 리팩터링해야 한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class AttendanceControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<Attendance> checkInResponse(String studentName, LocalTime checkInTime) {
        Attendance request = new Attendance();
        request.setStudentName(studentName);
        request.setCheckInTime(checkInTime);
        return restTemplate.postForEntity("/attendance/check-in", request, Attendance.class);
    }

    private Attendance checkIn(String studentName, LocalTime checkInTime) {
        return checkInResponse(studentName, checkInTime).getBody();
    }

    @Test
    void 정시_체크인은_ON_TIME으로_기록된다() {
        ResponseEntity<Attendance> response = checkInResponse("Alice", LocalTime.of(9, 0));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ON_TIME", response.getBody().getStatus());
    }

    @Test
    void 너무_늦게_체크인하면_ABSENT로_기록된다() {
        Attendance attendance = checkIn("Bob", LocalTime.of(9, 40));

        assertEquals("ABSENT", attendance.getStatus());
    }

    @Test
    void checkIn과_정정API는_같은_체크인_시각에_대해_같은_상태를_내려야한다() {
        LocalTime boundaryTime = LocalTime.of(9, 10); // 지각 기준 경계값

        Attendance checkedIn = checkIn("Charlie", boundaryTime);

        Attendance updateRequest = new Attendance();
        updateRequest.setCheckInTime(boundaryTime);
        ResponseEntity<Attendance> updateResponse = restTemplate.exchange(
                "/attendance/" + checkedIn.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Attendance.class
        );

        // 같은 09:10 체크인인데 check-in API와 정정 API의 판정이 달라서는 안 된다.
        assertEquals(checkedIn.getStatus(), updateResponse.getBody().getStatus(),
                "check-in 시점 상태(" + checkedIn.getStatus() + ")와 정정 후 상태("
                        + updateResponse.getBody().getStatus() + ")가 달라졌습니다.");
    }
}
