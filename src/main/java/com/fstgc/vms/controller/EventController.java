package com.fstgc.vms.controller;

import com.fstgc.vms.model.Event;
import com.fstgc.vms.service.EventService;
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
 * Event Controller
 * Handles HTTP requests for event operations
 */
@WebServlet("/api/events/*")
public class EventController extends HttpServlet {
    private EventService eventService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        eventService = new EventService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        String status = request.getParameter("status");
        String searchQuery = request.getParameter("q");

        try {
            // Search events
            if (searchQuery != null) {
                List<Event> events = eventService.searchEvents(searchQuery);
                sendJsonResponse(response, HttpServletResponse.SC_OK, events);
                return;
            }

            // Filter by status
            if (status != null) {
                List<Event> events = eventService.getEventsByStatus(status);
                sendJsonResponse(response, HttpServletResponse.SC_OK, events);
                return;
            }

            // Get upcoming events
            if (pathInfo != null && pathInfo.equals("/upcoming")) {
                List<Event> events = eventService.getUpcomingEvents();
                sendJsonResponse(response, HttpServletResponse.SC_OK, events);
                return;
            }

            // Get all events
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Event> events = eventService.getAllEvents();
                sendJsonResponse(response, HttpServletResponse.SC_OK, events);
                return;
            }

            // Get event by ID
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                try {
                    int eventId = Integer.parseInt(pathParts[1]);
                    Event event = eventService.getEventById(eventId);
                    sendJsonResponse(response, HttpServletResponse.SC_OK, event);
                } catch (NumberFormatException e) {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid event ID format");
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

        String pathInfo = request.getPathInfo();

        try {
            // Register for event
            if (pathInfo != null && pathInfo.contains("/register/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3) {
                    int eventId = Integer.parseInt(pathParts[2]);
                    eventService.registerVolunteer(eventId);
                    
                    JsonObject responseObj = new JsonObject();
                    responseObj.addProperty("message", "Successfully registered for event");
                    sendJsonResponse(response, HttpServletResponse.SC_OK, responseObj);
                    return;
                }
            }

            // Create new event
            Event event = gson.fromJson(request.getReader(), Event.class);
            Event createdEvent = eventService.createEvent(event);
            
            sendJsonResponse(response, HttpServletResponse.SC_CREATED, createdEvent);

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
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Event ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            
            // Publish event
            if (pathParts.length >= 3 && pathParts[2].equals("publish")) {
                int eventId = Integer.parseInt(pathParts[1]);
                eventService.publishEvent(eventId);
                
                JsonObject responseObj = new JsonObject();
                responseObj.addProperty("message", "Event published successfully");
                sendJsonResponse(response, HttpServletResponse.SC_OK, responseObj);
                return;
            }
            
            // Update event
            if (pathParts.length == 2) {
                int eventId = Integer.parseInt(pathParts[1]);
                
                Event event = gson.fromJson(request.getReader(), Event.class);
                event.setEventId(eventId);
                
                Event updatedEvent = eventService.updateEvent(event);
                sendJsonResponse(response, HttpServletResponse.SC_OK, updatedEvent);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid event ID format");
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
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Event ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int eventId = Integer.parseInt(pathParts[1]);
                boolean cancelled = eventService.cancelEvent(eventId);
                
                if (cancelled) {
                    JsonObject responseObj = new JsonObject();
                    responseObj.addProperty("message", "Event cancelled successfully");
                    sendJsonResponse(response, HttpServletResponse.SC_OK, responseObj);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                                    "Failed to cancel event");
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid event ID format");
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