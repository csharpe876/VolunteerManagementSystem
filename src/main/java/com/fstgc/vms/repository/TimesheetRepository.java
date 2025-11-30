package com.fstgc.vms.repository;

import com.fstgc.vms.model.Timesheet;
import com.fstgc.vms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Timesheet Repository
 * Handles all database operations for Timesheet entity
 */
public class TimesheetRepository {

    public Timesheet findById(int timesheetId) throws SQLException {
        String sql = "SELECT * FROM Timesheet WHERE timesheet_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, timesheetId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTimesheet(rs);
            }
        }
        return null;
    }

    public List<Timesheet> findByVolunteer(int volunteerId) throws SQLException {
        List<Timesheet> timesheets = new ArrayList<>();
        String sql = "SELECT * FROM Timesheet WHERE volunteer_id = ? " +
                    "ORDER BY period_start_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                timesheets.add(mapResultSetToTimesheet(rs));
            }
        }
        return timesheets;
    }

    public List<Timesheet> findByStatus(String status) throws SQLException {
        List<Timesheet> timesheets = new ArrayList<>();
        String sql = "SELECT * FROM Timesheet WHERE approval_status = ? " +
                    "ORDER BY period_start_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                timesheets.add(mapResultSetToTimesheet(rs));
            }
        }
        return timesheets;
    }

    public List<Timesheet> findPendingApprovals() throws SQLException {
        return findByStatus("pending");
    }

    public List<Timesheet> findAll() throws SQLException {
        List<Timesheet> timesheets = new ArrayList<>();
        String sql = "SELECT * FROM Timesheet ORDER BY period_start_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                timesheets.add(mapResultSetToTimesheet(rs));
            }
        }
        return timesheets;
    }

    public Timesheet save(Timesheet timesheet) throws SQLException {
        String sql = "INSERT INTO Timesheet (volunteer_id, period_start_date, period_end_date, " +
                    "total_hours, approval_status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, timesheet.getVolunteerId());
            stmt.setDate(2, timesheet.getPeriodStartDate());
            stmt.setDate(3, timesheet.getPeriodEndDate());
            stmt.setBigDecimal(4, timesheet.getTotalHours());
            stmt.setString(5, timesheet.getApprovalStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        timesheet.setTimesheetId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return timesheet;
    }

    public Timesheet update(Timesheet timesheet) throws SQLException {
        String sql = "UPDATE Timesheet SET total_hours = ?, approved_hours = ?, " +
                    "approval_status = ?, approved_by = ?, approval_date = ?, " +
                    "rejection_reason = ? WHERE timesheet_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, timesheet.getTotalHours());
            stmt.setBigDecimal(2, timesheet.getApprovedHours());
            stmt.setString(3, timesheet.getApprovalStatus());
            stmt.setObject(4, timesheet.getApprovedBy());
            stmt.setTimestamp(5, timesheet.getApprovalDate());
            stmt.setString(6, timesheet.getRejectionReason());
            stmt.setInt(7, timesheet.getTimesheetId());
            
            stmt.executeUpdate();
        }
        return timesheet;
    }

    public boolean approve(int timesheetId, int approvedBy) throws SQLException {
        String sql = "UPDATE Timesheet SET approval_status = 'approved', " +
                    "approved_by = ?, approval_date = NOW(), approved_hours = total_hours " +
                    "WHERE timesheet_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, approvedBy);
            stmt.setInt(2, timesheetId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean reject(int timesheetId, int rejectedBy, String reason) throws SQLException {
        String sql = "UPDATE Timesheet SET approval_status = 'rejected', " +
                    "approved_by = ?, approval_date = NOW(), rejection_reason = ? " +
                    "WHERE timesheet_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rejectedBy);
            stmt.setString(2, reason);
            stmt.setInt(3, timesheetId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean delete(int timesheetId) throws SQLException {
        String sql = "DELETE FROM Timesheet WHERE timesheet_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, timesheetId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private Timesheet mapResultSetToTimesheet(ResultSet rs) throws SQLException {
        Timesheet timesheet = new Timesheet();
        timesheet.setTimesheetId(rs.getInt("timesheet_id"));
        timesheet.setVolunteerId(rs.getInt("volunteer_id"));
        timesheet.setPeriodStartDate(rs.getDate("period_start_date"));
        timesheet.setPeriodEndDate(rs.getDate("period_end_date"));
        timesheet.setTotalHours(rs.getBigDecimal("total_hours"));
        timesheet.setApprovedHours(rs.getBigDecimal("approved_hours"));
        timesheet.setApprovalStatus(rs.getString("approval_status"));
        timesheet.setApprovedBy(rs.getObject("approved_by", Integer.class));
        timesheet.setApprovalDate(rs.getTimestamp("approval_date"));
        timesheet.setRejectionReason(rs.getString("rejection_reason"));
        timesheet.setCreatedDate(rs.getTimestamp("created_date"));
        return timesheet;
    }
}