package com.fstgc.vms.controller;

import com.fstgc.vms.model.Announcement;
import com.fstgc.vms.service.AnnouncementService;
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

@WebServlet("/api/announcements/*")
public class AnnouncementController extends HttpServlet {
    private AnnouncementService announcementService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        announcementService = new AnnouncementService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        String priority = request.getParameter("priority");
        String audience = request.getParameter("audience");

        try {
            // Get active announcements
            if (pathInfo != null && pathInfo.equals("/active")) {
                List<Announcement> announcements = announcementService.getActiveAnnouncements();
                sendJsonResponse(response, HttpServletResponse.SC_OK, announcements);
                return;
            }

            // Get announcements by priority
            if (priority != null) {
                List<Announcement> announcements = announcementService.getAnnouncementsByPriority(priority);
                sendJsonResponse(response, HttpServletResponse.SC_OK, announcements);
                return;
            }

            // Get announcements by audience
            if (audience != null) {
                List<Announcement> announcements = announcementService.getAnnouncementsForUser(audience);
                sendJsonResponse(response, HttpServletResponse.SC_OK, announcements);
                return;
            }

            // Get all announcements
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Announcement> announcements = announcementService.getAllAnnouncements();
                sendJsonResponse(response, HttpServletResponse.SC_OK, announcements);
                return;
            }

            // Get announcement by ID
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                try {
                    int announcementId = Integer.parseInt(pathParts[1]);
                    Announcement announcement = announcementService.getAnnouncementById(announcementId);
                    sendJsonResponse(response, HttpServletResponse.SC_OK, announcement);
                } catch (NumberFormatException e) {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid announcement ID format");
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Announcement announcement = gson.fromJson(request.getReader(), Announcement.class);
            Announcement createdAnnouncement = announcementService.createAnnouncement(announcement);
            
            sendJsonResponse(response, HttpServletResponse.SC_CREATED, createdAnnouncement);

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

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Announcement ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int announcementId = Integer.parseInt(pathParts[1]);
                
                Announcement announcement = gson.fromJson(request.getReader(), Announcement.class);
                announcement.setAnnouncementId(announcementId);
                
                Announcement updatedAnnouncement = announcementService.updateAnnouncement(announcement);
                sendJsonResponse(response, HttpServletResponse.SC_OK, updatedAnnouncement);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid announcement ID format");
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

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Announcement ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int announcementId = Integer.parseInt(pathParts[1]);
                boolean deleted = announcementService.deleteAnnouncement(announcementId);
                
                if (deleted) {
                    JsonObject responseObj = new JsonObject();
                    responseObj.addProperty("message", "Announcement deleted successfully");
                    sendJsonResponse(response, HttpServletResponse.SC_OK, responseObj);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                                    "Failed to delete announcement");
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid announcement ID format");
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Database error: " + e.getMessage());
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int statusCode, Object data) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(gson.toJson(data));
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        JsonObject error = new JsonObject();
        error.addProperty("error", message);
        response.getWriter().write(gson.toJson(error));
    }
}