package com.fstgc.vms.repository;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Attendance Repository
 * Handles all database operations for Attendance entity
 */
public class AttendanceRepository {

    public Attendance findById(int attendanceId) throws SQLException {
        String sql = "SELECT * FROM Attendance WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, attendanceId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAttendance(rs);
            }
        }
        return null;
    }

    public Attendance findByVolunteerAndEvent(int volunteerId, int eventId) throws SQLException {
        String sql = "SELECT * FROM Attendance WHERE volunteer_id = ? AND event_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            stmt.setInt(2, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAttendance(rs);
            }
        }
        return null;
    }

    public List<Attendance> findByVolunteer(int volunteerId) throws SQLException {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT * FROM Attendance WHERE volunteer_id = ? ORDER BY check_in_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                attendanceList.add(mapResultSetToAttendance(rs));
            }
        }
        return attendanceList;
    }

    public List<Attendance> findByEvent(int eventId) throws SQLException {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT * FROM Attendance WHERE event_id = ? ORDER BY check_in_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                attendanceList.add(mapResultSetToAttendance(rs));
            }
        }
        return attendanceList;
    }

    public List<Attendance> findAll() throws SQLException {
        List<Attendance> attendanceList = new ArrayList<>();
        String sql = "SELECT * FROM Attendance ORDER BY check_in_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                attendanceList.add(mapResultSetToAttendance(rs));
            }
        }
        return attendanceList;
    }

    public Attendance save(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO Attendance (volunteer_id, event_id, check_in_time, " +
                    "check_out_time, status, hours_worked, notes, recorded_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, attendance.getVolunteerId());
            stmt.setInt(2, attendance.getEventId());
            stmt.setTimestamp(3, attendance.getCheckInTime());
            stmt.setTimestamp(4, attendance.getCheckOutTime());
            stmt.setString(5, attendance.getStatus());
            stmt.setBigDecimal(6, attendance.getHoursWorked());
            stmt.setString(7, attendance.getNotes());
            stmt.setObject(8, attendance.getRecordedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        attendance.setAttendanceId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return attendance;
    }

    public Attendance update(Attendance attendance) throws SQLException {
        String sql = "UPDATE Attendance SET check_in_time = ?, check_out_time = ?, " +
                    "status = ?, hours_worked = ?, notes = ? WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, attendance.getCheckInTime());
            stmt.setTimestamp(2, attendance.getCheckOutTime());
            stmt.setString(3, attendance.getStatus());
            stmt.setBigDecimal(4, attendance.getHoursWorked());
            stmt.setString(5, attendance.getNotes());
            stmt.setInt(6, attendance.getAttendanceId());
            
            stmt.executeUpdate();
        }
        return attendance;
    }

    public boolean delete(int attendanceId) throws SQLException {
        String sql = "DELETE FROM Attendance WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, attendanceId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public Double getTotalHoursByVolunteer(int volunteerId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(hours_worked), 0) as total_hours " +
                    "FROM Attendance WHERE volunteer_id = ? AND status = 'present'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total_hours");
            }
        }
        return 0.0;
    }

    public int getEventCountByVolunteer(int volunteerId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM Attendance " +
                    "WHERE volunteer_id = ? AND status = 'present'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("attendance_id"));
        attendance.setVolunteerId(rs.getInt("volunteer_id"));
        attendance.setEventId(rs.getInt("event_id"));
        attendance.setCheckInTime(rs.getTimestamp("check_in_time"));
        attendance.setCheckOutTime(rs.getTimestamp("check_out_time"));
        attendance.setStatus(rs.getString("status"));
        attendance.setHoursWorked(rs.getBigDecimal("hours_worked"));
        attendance.setNotes(rs.getString("notes"));
        attendance.setRecordedBy(rs.getObject("recorded_by", Integer.class));
        return attendance;
    }
}