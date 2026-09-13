package com.gdgku.attendance;

import java.time.LocalTime;

public class Attendance {
    private Long id;
    private String studentName;
    private LocalTime checkInTime;
    private String status;

    public Attendance() {
    }

    public Attendance(Long id, String studentName, LocalTime checkInTime, String status) {
        this.id = id;
        this.studentName = studentName;
        this.checkInTime = checkInTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
