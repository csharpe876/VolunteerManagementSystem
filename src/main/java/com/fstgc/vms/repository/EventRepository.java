package com.fstgc.vms.repository;

import com.fstgc.vms.model.Event;
import com.fstgc.vms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Event Repository
 * Handles all database operations for Event entity
 */
public class EventRepository {

    public Event findById(int eventId) throws SQLException {
        String sql = "SELECT * FROM Event WHERE event_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEvent(rs);
            }
        }
        return null;
    }

    public List<Event> findAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event ORDER BY event_date DESC, start_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }

    public List<Event> findByStatus(String status) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event WHERE status = ? ORDER BY event_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }

    public List<Event> findUpcoming() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event WHERE event_date >= CURDATE() AND status = 'published' " +
                    "ORDER BY event_date ASC, start_time ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }

    public List<Event> findByDateRange(Date startDate, Date endDate) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event WHERE event_date BETWEEN ? AND ? " +
                    "ORDER BY event_date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }

    public Event save(Event event) throws SQLException {
        String sql = "INSERT INTO Event (title, description, event_date, start_time, end_time, " +
                    "location, capacity, event_type, status, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, event.getEventDate());
            stmt.setTime(4, event.getStartTime());
            stmt.setTime(5, event.getEndTime());
            stmt.setString(6, event.getLocation());
            stmt.setInt(7, event.getCapacity());
            stmt.setString(8, event.getEventType());
            stmt.setString(9, event.getStatus());
            stmt.setObject(10, event.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        event.setEventId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return event;
    }

    public Event update(Event event) throws SQLException {
        String sql = "UPDATE Event SET title = ?, description = ?, event_date = ?, " +
                    "start_time = ?, end_time = ?, location = ?, capacity = ?, " +
                    "event_type = ?, status = ? WHERE event_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, event.getEventDate());
            stmt.setTime(4, event.getStartTime());
            stmt.setTime(5, event.getEndTime());
            stmt.setString(6, event.getLocation());
            stmt.setInt(7, event.getCapacity());
            stmt.setString(8, event.getEventType());
            stmt.setString(9, event.getStatus());
            stmt.setInt(10, event.getEventId());
            
            stmt.executeUpdate();
        }
        return event;
    }

    public boolean updateRegistrationCount(int eventId, int increment) throws SQLException {
        String sql = "UPDATE Event SET current_registrations = current_registrations + ? " +
                    "WHERE event_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, increment);
            stmt.setInt(2, eventId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean delete(int eventId) throws SQLException {
        String sql = "UPDATE Event SET status = 'cancelled' WHERE event_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, eventId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Event> searchByTitle(String searchTerm) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM Event WHERE title LIKE ? ORDER BY event_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                events.add(mapResultSetToEvent(rs));
            }
        }
        return events;
    }

    public int getEventCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM Event WHERE status = 'published'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setTitle(rs.getString("title"));
        event.setDescription(rs.getString("description"));
        event.setEventDate(rs.getDate("event_date"));
        event.setStartTime(rs.getTime("start_time"));
        event.setEndTime(rs.getTime("end_time"));
        event.setLocation(rs.getString("location"));
        event.setCapacity(rs.getInt("capacity"));
        event.setCurrentRegistrations(rs.getInt("current_registrations"));
        event.setEventType(rs.getString("event_type"));
        event.setStatus(rs.getString("status"));
        event.setCreatedBy(rs.getObject("created_by", Integer.class));
        event.setCreatedDate(rs.getTimestamp("created_date"));
        return event;
    }
}