package com.fstgc.vms.repository;

import com.fstgc.vms.model.Award;
import com.fstgc.vms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Award Repository
 * Handles all database operations for Award entity
 */
public class AwardRepository {

    public Award findById(int awardId) throws SQLException {
        String sql = "SELECT * FROM Award WHERE award_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, awardId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAward(rs);
            }
        }
        return null;
    }

    public List<Award> findByVolunteer(int volunteerId) throws SQLException {
        List<Award> awards = new ArrayList<>();
        String sql = "SELECT * FROM Award WHERE volunteer_id = ? ORDER BY date_earned DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                awards.add(mapResultSetToAward(rs));
            }
        }
        return awards;
    }

    public List<Award> findByTier(String tier) throws SQLException {
        List<Award> awards = new ArrayList<>();
        String sql = "SELECT * FROM Award WHERE badge_tier = ? ORDER BY date_earned DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tier);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                awards.add(mapResultSetToAward(rs));
            }
        }
        return awards;
    }

    public List<Award> findFeatured() throws SQLException {
        List<Award> awards = new ArrayList<>();
        String sql = "SELECT * FROM Award WHERE is_featured = TRUE ORDER BY date_earned DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                awards.add(mapResultSetToAward(rs));
            }
        }
        return awards;
    }

    public boolean checkIfAwarded(int volunteerId, int criteriaId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM Award " +
                    "WHERE volunteer_id = ? AND criteria_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            stmt.setInt(2, criteriaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    public Award save(Award award) throws SQLException {
        String sql = "INSERT INTO Award (volunteer_id, badge_name, badge_description, " +
                    "criteria_id, badge_tier, badge_icon_url, is_featured) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, award.getVolunteerId());
            stmt.setString(2, award.getBadgeName());
            stmt.setString(3, award.getBadgeDescription());
            stmt.setObject(4, award.getCriteriaId());
            stmt.setString(5, award.getBadgeTier());
            stmt.setString(6, award.getBadgeIconUrl());
            stmt.setBoolean(7, award.isFeatured());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        award.setAwardId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return award;
    }

    public Award update(Award award) throws SQLException {
        String sql = "UPDATE Award SET is_featured = ?, badge_description = ? " +
                    "WHERE award_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, award.isFeatured());
            stmt.setString(2, award.getBadgeDescription());
            stmt.setInt(3, award.getAwardId());
            
            stmt.executeUpdate();
        }
        return award;
    }

    public boolean delete(int awardId) throws SQLException {
        String sql = "DELETE FROM Award WHERE award_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, awardId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public int getAwardCountByVolunteer(int volunteerId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM Award WHERE volunteer_id = ?";
        
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

    public int getAwardCountByTier(String tier) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM Award WHERE badge_tier = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tier);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    private Award mapResultSetToAward(ResultSet rs) throws SQLException {
        Award award = new Award();
        award.setAwardId(rs.getInt("award_id"));
        award.setVolunteerId(rs.getInt("volunteer_id"));
        award.setBadgeName(rs.getString("badge_name"));
        award.setBadgeDescription(rs.getString("badge_description"));
        award.setCriteriaId(rs.getObject("criteria_id", Integer.class));
        award.setDateEarned(rs.getTimestamp("date_earned"));
        award.setBadgeTier(rs.getString("badge_tier"));
        award.setBadgeIconUrl(rs.getString("badge_icon_url"));
        award.setFeatured(rs.getBoolean("is_featured"));
        return award;
    }
}