package com.fstgc.vms.controller;

import com.fstgc.vms.model.Attendance;
import com.fstgc.vms.service.AttendanceService;
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

@WebServlet("/api/attendance/*")
public class AttendanceController extends HttpServlet {
    private AttendanceService attendanceService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        attendanceService = new AttendanceService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        String volunteerId = request.getParameter("volunteerId");
        String eventId = request.getParameter("eventId");

        try {
            // Get attendance by volunteer
            if (volunteerId != null) {
                int volId = Integer.parseInt(volunteerId);
                List<Attendance> attendance = attendanceService.getAttendanceByVolunteer(volId);
                sendJsonResponse(response, HttpServletResponse.SC_OK, attendance);
                return;
            }

            // Get attendance by event
            if (eventId != null) {
                int evtId = Integer.parseInt(eventId);
                List<Attendance> attendance = attendanceService.getAttendanceByEvent(evtId);
                sendJsonResponse(response, HttpServletResponse.SC_OK, attendance);
                return;
            }

            // Get all attendance
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Attendance> attendance = attendanceService.getAllAttendance();
                sendJsonResponse(response, HttpServletResponse.SC_OK, attendance);
                return;
            }

            // Get attendance by ID
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                try {
                    int attendanceId = Integer.parseInt(pathParts[1]);
                    Attendance attendance = attendanceService.getAttendanceById(attendanceId);
                    sendJsonResponse(response, HttpServletResponse.SC_OK, attendance);
                } catch (NumberFormatException e) {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid attendance ID format");
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
            Attendance attendance = gson.fromJson(request.getReader(), Attendance.class);
            Attendance recordedAttendance = attendanceService.recordAttendance(attendance);
            
            sendJsonResponse(response, HttpServletResponse.SC_CREATED, recordedAttendance);

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
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Attendance ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int attendanceId = Integer.parseInt(pathParts[1]);
                
                Attendance attendance = gson.fromJson(request.getReader(), Attendance.class);
                attendance.setAttendanceId(attendanceId);
                
                Attendance updatedAttendance = attendanceService.updateAttendance(attendance);
                sendJsonResponse(response, HttpServletResponse.SC_OK, updatedAttendance);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid attendance ID format");
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
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Attendance ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int attendanceId = Integer.parseInt(pathParts[1]);
                boolean deleted = attendanceService.deleteAttendance(attendanceId);
                
                if (deleted) {
                    JsonObject responseObj = new JsonObject();
                    responseObj.addProperty("message", "Attendance deleted successfully");
                    sendJsonResponse(response, HttpServletResponse.SC_OK, responseObj);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                                    "Failed to delete attendance");
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid attendance ID format");
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