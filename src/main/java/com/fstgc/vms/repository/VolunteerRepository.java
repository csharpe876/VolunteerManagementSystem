package com.fstgc.vms.repository;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Volunteer Repository
 * Handles all database operations for Volunteer entity
 */
public class VolunteerRepository {

    /**
     * Find volunteer by ID
     * @param volunteerId Volunteer ID
     * @return Volunteer object or null if not found
     * @throws SQLException if database error occurs
     */
    public Volunteer findById(int volunteerId) throws SQLException {
        String sql = "SELECT * FROM Volunteer WHERE volunteer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVolunteer(rs);
            }
        }
        return null;
    }

    /**
     * Find volunteer by email
     * @param email Volunteer email
     * @return Volunteer object or null if not found
     * @throws SQLException if database error occurs
     */
    public Volunteer findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Volunteer WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVolunteer(rs);
            }
        }
        return null;
    }

    /**
     * Find all active volunteers
     * @return List of all active volunteers
     * @throws SQLException if database error occurs
     */
    public List<Volunteer> findAll() throws SQLException {
        List<Volunteer> volunteers = new ArrayList<>();
        String sql = "SELECT * FROM Volunteer WHERE status = 'active' ORDER BY first_name, last_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                volunteers.add(mapResultSetToVolunteer(rs));
            }
        }
        return volunteers;
    }

    /**
     * Save a new volunteer
     * @param volunteer Volunteer object to save
     * @return Saved volunteer with generated ID
     * @throws SQLException if database error occurs
     */
    public Volunteer save(Volunteer volunteer) throws SQLException {
        String sql = "INSERT INTO Volunteer (first_name, last_name, email, phone, " +
                    "date_of_birth, address, password_hash, profile_photo_url) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, volunteer.getFirstName());
            stmt.setString(2, volunteer.getLastName());
            stmt.setString(3, volunteer.getEmail());
            stmt.setString(4, volunteer.getPhone());
            stmt.setDate(5, volunteer.getDateOfBirth() != null ? Date.valueOf(volunteer.getDateOfBirth()) : null);
            stmt.setString(6, volunteer.getAddress());
            stmt.setString(7, volunteer.getPasswordHash());
            stmt.setString(8, volunteer.getProfilePhotoUrl());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        volunteer.setVolunteerId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        return volunteer;
    }

    /**
     * Update existing volunteer
     * @param volunteer Volunteer object with updated information
     * @return Updated volunteer
     * @throws SQLException if database error occurs
     */
    public Volunteer update(Volunteer volunteer) throws SQLException {
        String sql = "UPDATE Volunteer SET first_name = ?, last_name = ?, email = ?, " +
                    "phone = ?, date_of_birth = ?, address = ?, profile_photo_url = ?, " +
                    "status = ? WHERE volunteer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, volunteer.getFirstName());
            stmt.setString(2, volunteer.getLastName());
            stmt.setString(3, volunteer.getEmail());
            stmt.setString(4, volunteer.getPhone());
            stmt.setDate(5, volunteer.getDateOfBirth() != null ? Date.valueOf(volunteer.getDateOfBirth()) : null);
            stmt.setString(6, volunteer.getAddress());
            stmt.setString(7, volunteer.getProfilePhotoUrl());
            stmt.setBoolean(8, volunteer.isActive());
            stmt.setInt(9, volunteer.getVolunteerId());
            
            stmt.executeUpdate();
        }
        return volunteer;
    }

    /**
     * Delete volunteer (soft delete by setting status to inactive)
     * @param volunteerId Volunteer ID to delete
     * @return true if deleted successfully
     * @throws SQLException if database error occurs
     */
    public boolean delete(int volunteerId) throws SQLException {
        String sql = "UPDATE Volunteer SET status = 'inactive' WHERE volunteer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, volunteerId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Search volunteers by name
     * @param searchTerm Search term for first or last name
     * @return List of matching volunteers
     * @throws SQLException if database error occurs
     */
    public List<Volunteer> searchByName(String searchTerm) throws SQLException {
        List<Volunteer> volunteers = new ArrayList<>();
        String sql = "SELECT * FROM Volunteer WHERE (first_name LIKE ? OR last_name LIKE ?) " +
                    "AND status = 'active' ORDER BY first_name, last_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                volunteers.add(mapResultSetToVolunteer(rs));
            }
        }
        return volunteers;
    }

    /**
     * Get volunteer count
     * @return Total number of active volunteers
     * @throws SQLException if database error occurs
     */
    public int getVolunteerCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM Volunteer WHERE status = 'active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    /**
     * Map ResultSet to Volunteer object
     * @param rs ResultSet from database query
     * @return Volunteer object
     * @throws SQLException if database error occurs
     */
    private Volunteer mapResultSetToVolunteer(ResultSet rs) throws SQLException {
        Volunteer volunteer = new Volunteer();
        volunteer.setVolunteerId(rs.getInt("volunteer_id"));
        volunteer.setFirstName(rs.getString("first_name"));
        volunteer.setLastName(rs.getString("last_name"));
        volunteer.setEmail(rs.getString("email"));
        volunteer.setPhone(rs.getString("phone"));
        volunteer.setRegistrationDate(rs.getTimestamp("registration_date"));
        volunteer.setActive(rs.getBoolean("is_active"));
        volunteer.setProfilePhotoUrl(rs.getString("profile_photo_url"));
        
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            volunteer.setDateOfBirth(dob.toLocalDate());
        }
        
        volunteer.setAddress(rs.getString("address"));
        volunteer.setPasswordHash(rs.getString("password_hash"));
        
        return volunteer;
    }
}