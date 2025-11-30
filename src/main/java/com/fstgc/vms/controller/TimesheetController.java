package com.fstgc.vms.controller;

import com.fstgc.vms.model.Timesheet;
import com.fstgc.vms.service.TimesheetService;
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

@WebServlet("/api/timesheets/*")
public class TimesheetController extends HttpServlet {
    private TimesheetService timesheetService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        timesheetService = new TimesheetService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        String volunteerId = request.getParameter("volunteerId");
        String status = request.getParameter("status");

        try {
            // Get timesheets by volunteer
            if (volunteerId != null) {
                int volId = Integer.parseInt(volunteerId);
                List<Timesheet> timesheets = timesheetService.getTimesheetsByVolunteer(volId);
                sendJsonResponse(response, HttpServletResponse.SC_OK, timesheets);
                return;
            }

            // Get pending timesheets
            if (pathInfo != null && pathInfo.equals("/pending")) {
                List<Timesheet> timesheets = timesheetService.getPendingTimesheets();
                sendJsonResponse(response, HttpServletResponse.SC_OK, timesheets);
                return;
            }

            // Get timesheets by status
            if (status != null) {
                List<Timesheet> timesheets = timesheetService.getTimesheetsByStatus(status);
                sendJsonResponse(response, HttpServletResponse.SC_OK, timesheets);
                return;
            }

            // Get all timesheets
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Timesheet> timesheets = timesheetService.getAllTimesheets();
                sendJsonResponse(response, HttpServletResponse.SC_OK, timesheets);
                return;
            }

            // Get timesheet by ID
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                try {
                    int timesheetId = Integer.parseInt(pathParts[1]);
                    Timesheet timesheet = timesheetService.getTimesheetById(timesheetId);
                    sendJsonResponse(response, HttpServletResponse.SC_OK, timesheet);
                } catch (NumberFormatException e) {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid timesheet ID format");
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Timesheet ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            
            // Approve timesheet
            if (pathParts.length >= 3 && pathParts[2].equals("approve")) {
                int timesheetId = Integer.parseInt(pathParts[1]);
                
                JsonObject requestBody = gson.fromJson(request.getReader(), JsonObject.class);
                int approvedBy = requestBody.get("approvedBy").getAsInt();
                
                timesheetService.approveTimesheet(timesheetId, approvedBy);
                
                JsonObject responseObj = new JsonObject();
                responseObj.addProperty("message", "Timesheet approved successfully");
                sendJsonResponse(response, HttpServletResponse.SC_OK, responseObj);
                return;
            }
            
            // Reject timesheet
            if (pathParts.length >= 3 && pathParts[2].equals("reject")) {
                int timesheetId = Integer.parseInt(pathParts[1]);
                
                JsonObject requestBody = gson.fromJson(request.getReader(), JsonObject.class);
                int rejectedBy = requestBody.get("rejectedBy").getAsInt();
                String reason = requestBody.get("reason").getAsString();
                
                timesheetService.rejectTimesheet(timesheetId, rejectedBy, reason);
                
                JsonObject responseObj = new JsonObject();
                responseObj.addProperty("message", "Timesheet rejected");
                sendJsonResponse(response, HttpServletResponse.SC_OK, responseObj);
                return;
            }
            
            // Update timesheet
            if (pathParts.length == 2) {
                int timesheetId = Integer.parseInt(pathParts[1]);
                
                Timesheet timesheet = gson.fromJson(request.getReader(), Timesheet.class);
                timesheet.setTimesheetId(timesheetId);
                
                Timesheet updatedTimesheet = timesheetService.updateTimesheet(timesheet);
                sendJsonResponse(response, HttpServletResponse.SC_OK, updatedTimesheet);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid timesheet ID format");
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
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Timesheet ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int timesheetId = Integer.parseInt(pathParts[1]);
                boolean deleted = timesheetService.deleteTimesheet(timesheetId);
                
                if (deleted) {
                    JsonObject responseObj = new JsonObject();
                    responseObj.addProperty("message", "Timesheet deleted successfully");
                    sendJsonResponse(response, HttpServletResponse.SC_OK, responseObj);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                                    "Failed to delete timesheet");
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid timesheet ID format");
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