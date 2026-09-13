package com.gdgku.enrollment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [문제: 입력 검증 부재]
 *
 * 계층 분리와 DTO 분리는 잘 되어 있지만, 입력값을 전혀 검증하지 않는다.
 * - 정원이 0 이하인 강의도 그대로 생성된다.
 * - 정원을 초과해도 수강신청이 그대로 성공한다.
 * - 같은 학생이 같은 강의를 여러 번 신청해도 막지 않는다.
 * - 학생 이름 없이 신청하면 NullPointerException이 그대로 터져 500을 반환한다(400이어야 한다).
 *
 * CourseControllerTest의 실패하는 테스트들이 이 문제들을 각각 잡아낸다.
 *
 * 할 일: Bean Validation(@NotBlank, @Positive 등)과 Service의 비즈니스 규칙 검증을 추가해서
 * 정원 초과/중복 신청/빈 값이 4xx로 거부되도록 고치자.
 */
@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public CourseResponse createCourse(@RequestBody CourseRequest request) {
        return courseService.createCourse(request.getName(), request.getCapacity());
    }

    @GetMapping("/{courseId}")
    public CourseResponse getCourse(@PathVariable Long courseId) {
        return courseService.getCourse(courseId);
    }

    @PostMapping("/{courseId}/enrollments")
    public CourseResponse enroll(@PathVariable Long courseId, @RequestBody EnrollRequest request) {
        return courseService.enroll(courseId, request.getStudentName());
    }
}
