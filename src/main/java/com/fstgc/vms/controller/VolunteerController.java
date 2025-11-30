package com.fstgc.vms.controller;

import com.fstgc.vms.model.Volunteer;
import com.fstgc.vms.service.VolunteerService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Volunteer Controller
 * Handles HTTP requests for volunteer operations
 */
@WebServlet("/api/volunteers/*")
public class VolunteerController extends HttpServlet {
    private VolunteerService volunteerService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        volunteerService = new VolunteerService();
        gson = new Gson();
    }

    /**
     * Handle GET requests
     * GET /api/volunteers - Get all volunteers
     * GET /api/volunteers/{id} - Get volunteer by ID
     * GET /api/volunteers/search?q={term} - Search volunteers
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        String searchQuery = request.getParameter("q");

        try {
            // Search volunteers
            if (searchQuery != null) {
                List<Volunteer> volunteers = volunteerService.searchVolunteers(searchQuery);
                sendJsonResponse(response, HttpServletResponse.SC_OK, volunteers);
                return;
            }

            // Get all volunteers
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Volunteer> volunteers = volunteerService.getAllActiveVolunteers();
                sendJsonResponse(response, HttpServletResponse.SC_OK, volunteers);
                return;
            }

            // Get volunteer by ID
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                try {
                    int volunteerId = Integer.parseInt(pathParts[1]);
                    Volunteer volunteer = volunteerService.getVolunteerById(volunteerId);
                    sendJsonResponse(response, HttpServletResponse.SC_OK, volunteer);
                } catch (NumberFormatException e) {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid volunteer ID format");
                } catch (IllegalArgumentException e) {
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                }
                return;
            }

            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");

        } catch (SQLException e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Database error: " + e.getMessage());
        }
    }

    /**
     * Handle POST requests
     * POST /api/volunteers - Register new volunteer
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Parse request body
            Volunteer volunteer = gson.fromJson(request.getReader(), Volunteer.class);
            
            // Register volunteer
            Volunteer registeredVolunteer = volunteerService.registerVolunteer(volunteer);
            
            sendJsonResponse(response, HttpServletResponse.SC_CREATED, registeredVolunteer);

        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                            "Invalid request body: " + e.getMessage());
        }
    }

    /**
     * Handle PUT requests
     * PUT /api/volunteers/{id} - Update volunteer
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Volunteer ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int volunteerId = Integer.parseInt(pathParts[1]);
                
                // Parse request body
                Volunteer volunteer = gson.fromJson(request.getReader(), Volunteer.class);
                volunteer.setVolunteerId(volunteerId);
                
                // Update volunteer
                Volunteer updatedVolunteer = volunteerService.updateVolunteer(volunteer);
                
                sendJsonResponse(response, HttpServletResponse.SC_OK, updatedVolunteer);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid volunteer ID format");
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                            "Invalid request body: " + e.getMessage());
        }
    }

    /**
     * Handle DELETE requests
     * DELETE /api/volunteers/{id} - Deactivate volunteer
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Volunteer ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int volunteerId = Integer.parseInt(pathParts[1]);
                
                // Deactivate volunteer
                boolean deactivated = volunteerService.deactivateVolunteer(volunteerId);
                
                if (deactivated) {
                    JsonObject response_obj = new JsonObject();
                    response_obj.addProperty("message", "Volunteer deactivated successfully");
                    sendJsonResponse(response, HttpServletResponse.SC_OK, response_obj);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                                    "Failed to deactivate volunteer");
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid volunteer ID format");
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Database error: " + e.getMessage());
        }
    }

    /**
     * Send JSON response
     */
    private void sendJsonResponse(HttpServletResponse response, int statusCode, Object data) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(gson.toJson(data));
    }

    /**
     * Send error response
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        JsonObject error = new JsonObject();
        error.addProperty("error", message);
        response.getWriter().write(gson.toJson(error));
    }
}