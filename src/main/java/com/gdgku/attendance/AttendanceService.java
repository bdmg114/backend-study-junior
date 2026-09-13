package com.gdgku.attendance;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;   // 추가

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service 
public class AttendanceService {
    private static final LocalTime LATE_CUTOFF = LocalTime.of(9, 10);
    private static final LocalTime ABSENT_CUTOFF = LocalTime.of(9, 30);

    private final List<Attendance> attendances = new ArrayList<>();
    private long nextId = 1L;

    public String getStatusFromTime(LocalTime time){
        String status;
        if (!time.isAfter(LATE_CUTOFF)) {
            status = "ON_TIME";
        } else if (!time.isAfter(ABSENT_CUTOFF)) {
            status = "LATE";
        } else {
            status = "ABSENT";
        }
        return status;
    }

    public Attendance checkIn(Attendance request){
        String status = getStatusFromTime(request.getCheckInTime());

        Attendance attendance = new Attendance(nextId++, request.getStudentName(), request.getCheckInTime(), status);
        attendances.add(attendance);
        return attendance;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public Attendance getAttendance(Long id){
        for (Attendance attendance : attendances) {
            if (attendance.getId().equals(id)) {
                return attendance;
            }
        }
        return null;
    }

    public long countLate() {
        long count = 0;
        for (Attendance attendance : attendances) {
            if ("LATE".equals(attendance.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public Attendance updateCheckInTime(@PathVariable Long id, @RequestBody Attendance request) {
        Attendance attendance = getAttendance(id);
        if (attendance == null) {
            return null;
        }

        attendance.setCheckInTime(request.getCheckInTime());

        String status = getStatusFromTime(request.getCheckInTime());
        attendance.setStatus(status);

        return attendance;
    }
}
