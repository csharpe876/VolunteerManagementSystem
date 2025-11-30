package com.fstgc.vms.repository;

import com.fstgc.vms.model.Announcement;
import com.fstgc.vms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Announcement Repository
 * Handles all database operations for Announcement entity
 */
public class AnnouncementRepository {

    public Announcement findById(int announcementId) throws SQLException {
        String sql = "SELECT * FROM Announcement WHERE announcement_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, announcementId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAnnouncement(rs);
            }
        }
        return null;
    }

    public List<Announcement> findAll() throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE is_deleted = FALSE " +
                    "ORDER BY published_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
        }
        return announcements;
    }

    public List<Announcement> findActive() throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE is_deleted = FALSE " +
                    "AND (expiry_date IS NULL OR expiry_date > NOW()) " +
                    "ORDER BY priority DESC, published_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
        }
        return announcements;
    }

    public List<Announcement> findByPriority(String priority) throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE priority = ? AND is_deleted = FALSE " +
                    "ORDER BY published_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, priority);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
        }
        return announcements;
    }

    public List<Announcement> findByTargetAudience(String targetAudience) throws SQLException {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM Announcement WHERE (target_audience = ? OR target_audience = 'all') " +
                    "AND is_deleted = FALSE AND (expiry_date IS NULL OR expiry_date > NOW()) " +
                    "ORDER BY priority DESC, published_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, targetAudience);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
        }
        return announcements;
    }

    public Announcement save(Announcement announcement) throws SQLException {
        String sql = "INSERT INTO Announcement (title, message, expiry_date, priority, " +
                    "target_audience, event_id, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getMessage());
            stmt.setTimestamp(3, announcement.getExpiryDate());
            stmt.setString(4, announcement.getPriority());
            stmt.setString(5, announcement.getTargetAudience());
            stmt.setObject(6, announcement.getEventId());
            stmt.setObject(7, announcement.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        announcement.setAnnouncementId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return announcement;
    }

    public Announcement update(Announcement announcement) throws SQLException {
        String sql = "UPDATE Announcement SET title = ?, message = ?, expiry_date = ?, " +
                    "priority = ?, target_audience = ? WHERE announcement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getMessage());
            stmt.setTimestamp(3, announcement.getExpiryDate());
            stmt.setString(4, announcement.getPriority());
            stmt.setString(5, announcement.getTargetAudience());
            stmt.setInt(6, announcement.getAnnouncementId());
            
            stmt.executeUpdate();
        }
        return announcement;
    }

    public boolean delete(int announcementId) throws SQLException {
        String sql = "UPDATE Announcement SET is_deleted = TRUE WHERE announcement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, announcementId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public int getActiveAnnouncementCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM Announcement " +
                    "WHERE is_deleted = FALSE AND (expiry_date IS NULL OR expiry_date > NOW())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    private Announcement mapResultSetToAnnouncement(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(rs.getInt("announcement_id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setMessage(rs.getString("message"));
        announcement.setPublishedDate(rs.getTimestamp("published_date"));
        announcement.setExpiryDate(rs.getTimestamp("expiry_date"));
        announcement.setPriority(rs.getString("priority"));
        announcement.setTargetAudience(rs.getString("target_audience"));
        announcement.setEventId(rs.getObject("event_id", Integer.class));
        announcement.setCreatedBy(rs.getObject("created_by", Integer.class));
        announcement.setDeleted(rs.getBoolean("is_deleted"));
        return announcement;
    }
}