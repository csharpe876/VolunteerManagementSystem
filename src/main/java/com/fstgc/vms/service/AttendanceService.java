package com.fstgc.vms.service;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.repository.AttendanceRepository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Attendance Service Layer
 * Contains business logic for attendance operations
 */
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public AttendanceService() {
        this.attendanceRepository = new AttendanceRepository();
    }

    public Attendance checkIn(int volunteerId, int eventId, Timestamp checkInTime) throws SQLException {
        // Check if attendance already exists
        Attendance existing = attendanceRepository.findByVolunteerAndEvent(volunteerId, eventId);
        if (existing != null) {
            throw new IllegalArgumentException("Volunteer has already checked in for this event");
        }
        
        Attendance attendance = new Attendance(volunteerId, eventId, checkInTime);
        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(int attendanceId, Timestamp checkOutTime) throws SQLException {
        Attendance attendance = attendanceRepository.findById(attendanceId);
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance record not found");
        }
        
        if (attendance.getCheckOutTime() != null) {
            throw new IllegalArgumentException("Volunteer has already checked out");
        }
        
        if (checkOutTime.before(attendance.getCheckInTime())) {
            throw new IllegalArgumentException("Check-out time cannot be before check-in time");
        }
        
        attendance.setCheckOutTime(checkOutTime);
        attendance.calculateHoursWorked();
        
        return attendanceRepository.update(attendance);
    }

    public Attendance recordAttendance(Attendance attendance) throws SQLException {
        validateAttendance(attendance);
        
        // Check for duplicates
        Attendance existing = attendanceRepository.findByVolunteerAndEvent(
            attendance.getVolunteerId(), 
            attendance.getEventId()
        );
        
        if (existing != null) {
            throw new IllegalArgumentException("Attendance already recorded for this volunteer and event");
        }
        
        // Calculate hours if both check-in and check-out are provided
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            attendance.calculateHoursWorked();
        }
        
        return attendanceRepository.save(attendance);
    }

    public Attendance updateAttendance(Attendance attendance) throws SQLException {
        validateAttendance(attendance);
        
        Attendance existing = attendanceRepository.findById(attendance.getAttendanceId());
        if (existing == null) {
            throw new IllegalArgumentException("Attendance record not found");
        }
        
        // Recalculate hours if times changed
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            attendance.calculateHoursWorked();
        }
        
        return attendanceRepository.update(attendance);
    }

    public List<Attendance> getAttendanceByVolunteer(int volunteerId) throws SQLException {
        return attendanceRepository.findByVolunteer(volunteerId);
    }

    public List<Attendance> getAttendanceByEvent(int eventId) throws SQLException {
        return attendanceRepository.findByEvent(eventId);
    }

    public Attendance getAttendanceById(int attendanceId) throws SQLException {
        Attendance attendance = attendanceRepository.findById(attendanceId);
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance record not found");
        }
        return attendance;
    }

    public List<Attendance> getAllAttendance() throws SQLException {
        return attendanceRepository.findAll();
    }

    public boolean deleteAttendance(int attendanceId) throws SQLException {
        Attendance attendance = attendanceRepository.findById(attendanceId);
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance record not found");
        }
        return attendanceRepository.delete(attendanceId);
    }

    public Double getTotalHoursByVolunteer(int volunteerId) throws SQLException {
        return attendanceRepository.getTotalHoursByVolunteer(volunteerId);
    }

    public int getEventCountByVolunteer(int volunteerId) throws SQLException {
        return attendanceRepository.getEventCountByVolunteer(volunteerId);
    }

    public double calculateAttendanceRate(int volunteerId) throws SQLException {
        List<Attendance> allAttendance = attendanceRepository.findByVolunteer(volunteerId);
        if (allAttendance.isEmpty()) {
            return 0.0;
        }
        
        long presentCount = allAttendance.stream()
            .filter(Attendance::isPresent)
            .count();
        
        return (double) presentCount / allAttendance.size() * 100;
    }

    private void validateAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance object cannot be null");
        }
        
        if (attendance.getVolunteerId() <= 0) {
            throw new IllegalArgumentException("Valid volunteer ID is required");
        }
        
        if (attendance.getEventId() <= 0) {
            throw new IllegalArgumentException("Valid event ID is required");
        }
        
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            if (attendance.getCheckOutTime().before(attendance.getCheckInTime())) {
                throw new IllegalArgumentException("Check-out time cannot be before check-in time");
            }
        }
    }
}