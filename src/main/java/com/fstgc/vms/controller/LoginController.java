package com.fstgc.vms.controller;

import com.fstgc.vms.model.SystemAdmin;
import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.service.VolunteerService;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.fstgc.vms.util.DatabaseConnection;

/**
 * LoginController
 * Handles user authentication for both volunteers and administrators
 */
@WebServlet("/api/login")
public class LoginController extends HttpServlet {
    private final Gson gson = new Gson();
    private final VolunteerService volunteerService = new VolunteerService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Parse JSON request body
            BufferedReader reader = request.getReader();
            Map<String, Object> loginData = gson.fromJson(reader, Map.class);

            String username = (String) loginData.get("username");
            String password = (String) loginData.get("password");
            boolean rememberMe = loginData.containsKey("rememberMe") && 
                                (Boolean) loginData.get("rememberMe");

            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                sendError(response, out, 400, "Username and password are required");
                return;
            }

            // Try to authenticate as admin first
            SystemAdmin admin = authenticateAdmin(username, password);
            if (admin != null) {
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", admin.getAdminId());
                session.setAttribute("userType", "admin");
                session.setAttribute("userRole", admin.getRole());
                session.setAttribute("username", admin.getUsername());

                if (rememberMe) {
                    session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
                } else {
                    session.setMaxInactiveInterval(30 * 60); // 30 minutes
                }

                // Update last login
                updateAdminLastLogin(admin.getAdminId());

                // Send success response
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Login successful");
                
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", admin.getAdminId());
                userData.put("username", admin.getUsername());
                userData.put("email", admin.getEmail());
                userData.put("firstName", admin.getFirstName());
                userData.put("lastName", admin.getLastName());
                userData.put("role", admin.getRole());
                userData.put("userType", "admin");
                
                responseData.put("user", userData);
                responseData.put("token", session.getId());

                response.setStatus(200);
                out.print(gson.toJson(responseData));
                return;
            }

            // Try to authenticate as volunteer
            Volunteer volunteer = authenticateVolunteer(username, password);
            if (volunteer != null) {
                if (!volunteer.isActive()) {
                    sendError(response, out, 403, "Account is inactive. Please contact administrator.");
                    return;
                }

                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", volunteer.getVolunteerId());
                session.setAttribute("userType", "volunteer");
                session.setAttribute("username", volunteer.getEmail());

                if (rememberMe) {
                    session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
                } else {
                    session.setMaxInactiveInterval(30 * 60); // 30 minutes
                }

                // Update last login
                updateVolunteerLastLogin(volunteer.getVolunteerId());

                // Send success response
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Login successful");
                
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", volunteer.getVolunteerId());
                userData.put("email", volunteer.getEmail());
                userData.put("firstName", volunteer.getFirstName());
                userData.put("lastName", volunteer.getLastName());
                userData.put("role", "volunteer");
                userData.put("userType", "volunteer");
                userData.put("totalHours", volunteer.getTotalHours());
                
                responseData.put("user", userData);
                responseData.put("token", session.getId());

                response.setStatus(200);
                out.print(gson.toJson(responseData));
                return;
            }

            // If we reach here, authentication failed
            sendError(response, out, 401, "Invalid username or password");

        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, out, 500, "An error occurred during login: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    /**
     * Authenticate administrator
     */
    private SystemAdmin authenticateAdmin(String username, String password) throws SQLException {
        String query = "SELECT * FROM SystemAdmin WHERE (username = ? OR email = ?) AND account_status = 'active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (BCrypt.checkpw(password, storedHash)) {
                        SystemAdmin admin = new SystemAdmin();
                        admin.setAdminId(rs.getInt("admin_id"));
                        admin.setUsername(rs.getString("username"));
                        admin.setEmail(rs.getString("email"));
                        admin.setFirstName(rs.getString("first_name"));
                        admin.setLastName(rs.getString("last_name"));
                        admin.setRole(rs.getString("role"));
                        return admin;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Authenticate volunteer
     */
    private Volunteer authenticateVolunteer(String username, String password) throws SQLException {
        String query = "SELECT * FROM Volunteer WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
                        Volunteer volunteer = new Volunteer();
                        volunteer.setVolunteerId(rs.getInt("volunteer_id"));
                        volunteer.setFirstName(rs.getString("first_name"));
                        volunteer.setLastName(rs.getString("last_name"));
                        volunteer.setEmail(rs.getString("email"));
                        volunteer.setPhone(rs.getString("phone"));
                        volunteer.setActive(rs.getBoolean("is_active"));
                        volunteer.setTotalHours(rs.getDouble("total_hours"));
                        return volunteer;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Update admin last login timestamp
     */
    private void updateAdminLastLogin(int adminId) {
        String query = "UPDATE SystemAdmin SET last_login = CURRENT_TIMESTAMP WHERE admin_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, adminId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update volunteer last login timestamp
     */
    private void updateVolunteerLastLogin(int volunteerId) {
        String query = "UPDATE Volunteer SET last_login = CURRENT_TIMESTAMP WHERE volunteer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, volunteerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send error response
     */
    private void sendError(HttpServletResponse response, PrintWriter out, int status, String message) {
        response.setStatus(status);
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("success", false);
        errorData.put("message", message);
        out.print(gson.toJson(errorData));
    }
}
