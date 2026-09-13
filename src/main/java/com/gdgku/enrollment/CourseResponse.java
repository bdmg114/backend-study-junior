package com.gdgku.enrollment;

import java.util.List;

public class CourseResponse {

    private Long id;
    private String name;
    private int capacity;
    private List<String> enrolledStudents;
    private int enrolledCount;

    public CourseResponse() {
    }

    public CourseResponse(Course course) {
        this.id = course.getId();
        this.name = course.getName();
        this.capacity = course.getCapacity();
        this.enrolledStudents = course.getEnrolledStudents();
        this.enrolledCount = course.getEnrolledCount();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(int enrolledCount) {
        this.enrolledCount = enrolledCount;
    }
}
