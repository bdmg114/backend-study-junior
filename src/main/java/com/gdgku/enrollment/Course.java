package com.gdgku.enrollment;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private Long id;
    private String name;
    private int capacity;
    private List<String> enrolledStudents = new ArrayList<>();

    public Course() {
    }

    public Course(Long id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
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
        return enrolledStudents.size();
    }
}
