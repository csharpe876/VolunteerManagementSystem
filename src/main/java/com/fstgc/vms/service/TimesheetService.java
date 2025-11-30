package com.fstgc.vms.service;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.model.Timesheet;
import com.fstgc.vms.repository.AttendanceRepository;
import com.fstgc.vms.repository.TimesheetRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Timesheet Service Layer
 * Contains business logic for timesheet operations
 */
public class TimesheetService {
    private final TimesheetRepository timesheetRepository;
    private final AttendanceRepository attendanceRepository;

    public TimesheetService() {
        this.timesheetRepository = new TimesheetRepository();
        this.attendanceRepository = new AttendanceRepository();
    }

    public Timesheet generateTimesheet(int volunteerId, Date startDate, Date endDate) throws SQLException {
        // Get all attendance records for the volunteer in the period
        List<Attendance> attendanceRecords = attendanceRepository.findByVolunteer(volunteerId);
        
        // Filter by date range and calculate total hours
        BigDecimal totalHours = BigDecimal.ZERO;
        for (Attendance attendance : attendanceRecords) {
            Date checkInDate = new Date(attendance.getCheckInTime().getTime());
            if (!checkInDate.before(startDate) && !checkInDate.after(endDate)) {
                if (attendance.getHoursWorked() != null) {
                    totalHours = totalHours.add(attendance.getHoursWorked());
                }
            }
        }
        
        // Create timesheet
        Timesheet timesheet = new Timesheet(volunteerId, startDate, endDate);
        timesheet.setTotalHours(totalHours);
        
        return timesheetRepository.save(timesheet);
    }

    public Timesheet getTimesheetById(int timesheetId) throws SQLException {
        Timesheet timesheet = timesheetRepository.findById(timesheetId);
        if (timesheet == null) {
            throw new IllegalArgumentException("Timesheet not found");
        }
        return timesheet;
    }

    public List<Timesheet> getTimesheetsByVolunteer(int volunteerId) throws SQLException {
        return timesheetRepository.findByVolunteer(volunteerId);
    }

    public List<Timesheet> getPendingTimesheets() throws SQLException {
        return timesheetRepository.findPendingApprovals();
    }

    public List<Timesheet> getTimesheetsByStatus(String status) throws SQLException {
        return timesheetRepository.findByStatus(status);
    }

    public List<Timesheet> getAllTimesheets() throws SQLException {
        return timesheetRepository.findAll();
    }

    public boolean approveTimesheet(int timesheetId, int approvedBy) throws SQLException {
        Timesheet timesheet = timesheetRepository.findById(timesheetId);
        if (timesheet == null) {
            throw new IllegalArgumentException("Timesheet not found");
        }
        
        if (!timesheet.isPending()) {
            throw new IllegalArgumentException("Timesheet is not pending approval");
        }
        
        return timesheetRepository.approve(timesheetId, approvedBy);
    }

    public boolean rejectTimesheet(int timesheetId, int rejectedBy, String reason) throws SQLException {
        Timesheet timesheet = timesheetRepository.findById(timesheetId);
        if (timesheet == null) {
            throw new IllegalArgumentException("Timesheet not found");
        }
        
        if (!timesheet.isPending()) {
            throw new IllegalArgumentException("Timesheet is not pending approval");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }
        
        return timesheetRepository.reject(timesheetId, rejectedBy, reason);
    }

    public Timesheet updateTimesheet(Timesheet timesheet) throws SQLException {
        Timesheet existing = timesheetRepository.findById(timesheet.getTimesheetId());
        if (existing == null) {
            throw new IllegalArgumentException("Timesheet not found");
        }
        
        if (existing.isApproved()) {
            throw new IllegalArgumentException("Cannot update an approved timesheet");
        }
        
        return timesheetRepository.update(timesheet);
    }

    public boolean deleteTimesheet(int timesheetId) throws SQLException {
        Timesheet timesheet = timesheetRepository.findById(timesheetId);
        if (timesheet == null) {
            throw new IllegalArgumentException("Timesheet not found");
        }
        
        if (timesheet.isApproved()) {
            throw new IllegalArgumentException("Cannot delete an approved timesheet");
        }
        
        return timesheetRepository.delete(timesheetId);
    }
}