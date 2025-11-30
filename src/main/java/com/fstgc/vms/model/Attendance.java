package com.fstgc.vms.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Duration;

/**
 * Attendance Entity Model
 * Represents attendance record for a volunteer at an event
 */
public class Attendance {
    private int attendanceId;
    private int volunteerId;
    private int eventId;
    private Timestamp checkInTime;
    private Timestamp checkOutTime;
    private String status; // 'present', 'absent', 'late', 'excused'
    private BigDecimal hoursWorked;
    private String notes;
    private Integer recordedBy;

    // Constructors
    public Attendance() {
        this.status = "present";
        this.hoursWorked = BigDecimal.ZERO;
    }

    public Attendance(int volunteerId, int eventId) {
        this();
        this.volunteerId = volunteerId;
        this.eventId = eventId;
    }

    public Attendance(int volunteerId, int eventId, Timestamp checkInTime) {
        this(volunteerId, eventId);
        this.checkInTime = checkInTime;
    }

    // Getters and Setters
    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Timestamp getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Timestamp checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Timestamp getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Timestamp checkOutTime) {
        this.checkOutTime = checkOutTime;
        if (checkInTime != null && checkOutTime != null) {
            calculateHoursWorked();
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(BigDecimal hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Integer recordedBy) {
        this.recordedBy = recordedBy;
    }

    // Utility Methods
    public void calculateHoursWorked() {
        if (checkInTime != null && checkOutTime != null) {
            long milliseconds = checkOutTime.getTime() - checkInTime.getTime();
            double hours = milliseconds / (1000.0 * 60 * 60);
            this.hoursWorked = BigDecimal.valueOf(hours).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public boolean isCheckedOut() {
        return checkOutTime != null;
    }

    public boolean isPresent() {
        return "present".equals(status);
    }

    public boolean isLate() {
        return "late".equals(status);
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", volunteerId=" + volunteerId +
                ", eventId=" + eventId +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                ", hoursWorked=" + hoursWorked +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return attendanceId == that.attendanceId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(attendanceId);
    }
}