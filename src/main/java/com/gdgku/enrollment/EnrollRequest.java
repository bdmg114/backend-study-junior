package com.gdgku.enrollment;

public class EnrollRequest {

    private String studentName;

    public EnrollRequest() {
    }

    public EnrollRequest(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
