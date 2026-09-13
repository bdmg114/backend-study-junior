package com.gdgku.enrollment;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CourseService {

    private final Map<Long, Course> courses = new HashMap<>();
    private long nextCourseId = 1L;

    public CourseResponse createCourse(String name, int capacity) {
        // 이름이 비어 있거나 정원이 0 이하여도 그대로 생성된다.
        Course course = new Course(nextCourseId++, name, capacity);
        courses.put(course.getId(), course);
        return new CourseResponse(course);
    }

    public CourseResponse getCourse(Long courseId) {
        return new CourseResponse(courses.get(courseId));
    }

    public CourseResponse enroll(Long courseId, String studentName) {
        Course course = courses.get(courseId);
        // 정원 초과 여부, 중복 신청 여부를 전혀 확인하지 않는다.
        // studentName이 null이면(요청에 값이 없으면) 여기서 NullPointerException이 발생한다.
        course.getEnrolledStudents().add(studentName.trim());
        return new CourseResponse(course);
    }
}
