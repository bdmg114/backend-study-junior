package com.gdgku.enrollment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * [입력 검증 부재 문제] CourseController는 계층 분리와 DTO 분리는 잘 되어 있지만,
 * 입력값을 전혀 검증하지 않는다. 아래 실패하는 테스트들이 그 결과로 실제 발생하는
 * 문제(정원 초과, 중복 신청, 잘못된 정원, 필수값 누락 시 500)를 각각 재현한다.
 *
 * 이 테스트들을 통과시키려면 Bean Validation과 Service의 비즈니스 규칙 검증을 추가해야 한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class CourseControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private CourseRequest courseRequest(String name, int capacity) {
        CourseRequest request = new CourseRequest();
        request.setName(name);
        request.setCapacity(capacity);
        return request;
    }

    private Long createCourse(String name, int capacity) {
        return restTemplate.postForEntity("/courses", courseRequest(name, capacity), CourseResponse.class)
                .getBody().getId();
    }

    private CourseResponse getCourse(Long courseId) {
        return restTemplate.getForEntity("/courses/" + courseId, CourseResponse.class).getBody();
    }

    // 응답이 정상(200 + Course)일 수도, 에러(4xx/5xx + 에러 바디)일 수도 있으므로
    // 안전하게 String으로 받아 상태 코드만으로 검증한다.
    private ResponseEntity<String> enroll(Long courseId, String studentName) {
        return restTemplate.postForEntity(
                "/courses/" + courseId + "/enrollments", new EnrollRequest(studentName), String.class);
    }

    @Test
    void 정원_내에서는_정상적으로_수강신청된다() {
        Long courseId = createCourse("스프링 스터디", 3);

        ResponseEntity<String> response = enroll(courseId, "Alice");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, getCourse(courseId).getEnrolledCount());
    }

    @Test
    void 정원을_초과한_수강신청은_거부되어야_한다() {
        Long courseId = createCourse("스프링 스터디", 1);
        enroll(courseId, "Alice");

        ResponseEntity<String> secondResponse = enroll(courseId, "Bob");

        assertTrue(secondResponse.getStatusCode().is4xxClientError(),
                "정원(1명)을 초과했는데 응답이 " + secondResponse.getStatusCode() + " 였습니다.");
    }

    @Test
    void 같은_학생의_중복_수강신청은_거부되어야_한다() {
        Long courseId = createCourse("스프링 스터디", 10);
        enroll(courseId, "Alice");

        ResponseEntity<String> secondResponse = enroll(courseId, "Alice");

        assertTrue(secondResponse.getStatusCode().is4xxClientError(),
                "이미 신청한 학생인데 응답이 " + secondResponse.getStatusCode() + " 였습니다.");
    }

    @Test
    void 정원이_0이하인_강의는_생성할_수_없어야_한다() {
        ResponseEntity<CourseResponse> response =
                restTemplate.postForEntity("/courses", courseRequest("정원 없는 강의", 0), CourseResponse.class);

        assertTrue(response.getStatusCode().is4xxClientError(),
                "정원이 0인 강의 생성이 " + response.getStatusCode() + " 로 성공했습니다.");
    }

    @Test
    void 학생_이름_없이_신청하면_500이_아니라_400을_반환해야_한다() {
        Long courseId = createCourse("스프링 스터디", 10);

        ResponseEntity<String> response = enroll(courseId, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
