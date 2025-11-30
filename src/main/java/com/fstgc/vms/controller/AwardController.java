package com.fstgc.vms.controller;

import com.fstgc.vms.model.Award;
import com.fstgc.vms.service.AwardService;
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

@WebServlet("/api/awards/*")
public class AwardController extends HttpServlet {
    private AwardService awardService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        awardService = new AwardService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        String volunteerId = request.getParameter("volunteerId");
        String tier = request.getParameter("tier");

        try {
            // Get awards by volunteer
            if (volunteerId != null) {
                int volId = Integer.parseInt(volunteerId);
                List<Award> awards = awardService.getAwardsByVolunteer(volId);
                sendJsonResponse(response, HttpServletResponse.SC_OK, awards);
                return;
            }

            // Get featured awards
            if (pathInfo != null && pathInfo.equals("/featured")) {
                List<Award> awards = awardService.getFeaturedAwards();
                sendJsonResponse(response, HttpServletResponse.SC_OK, awards);
                return;
            }

            // Get awards by tier
            if (tier != null) {
                List<Award> awards = awardService.getAwardsByTier(tier);
                sendJsonResponse(response, HttpServletResponse.SC_OK, awards);
                return;
            }

            // Get award by ID
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                try {
                    int awardId = Integer.parseInt(pathParts[1]);
                    Award award = awardService.getAwardById(awardId);
                    sendJsonResponse(response, HttpServletResponse.SC_OK, award);
                } catch (NumberFormatException e) {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid award ID format");
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
            // Check automatic awards
            if (pathInfo != null && pathInfo.contains("/check/")) {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 3) {
                    int volunteerId = Integer.parseInt(pathParts[2]);
                    awardService.checkAndAssignAutomaticAwards(volunteerId);
                    
                    JsonObject responseObj = new JsonObject();
                    responseObj.addProperty("message", "Awards checked and assigned");
                    sendJsonResponse(response, HttpServletResponse.SC_OK, responseObj);
                    return;
                }
            }

            // Manual award assignment
            Award award = gson.fromJson(request.getReader(), Award.class);
            Award assignedAward = awardService.assignAward(award);
            
            sendJsonResponse(response, HttpServletResponse.SC_CREATED, assignedAward);

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
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Award ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int awardId = Integer.parseInt(pathParts[1]);
                
                Award award = gson.fromJson(request.getReader(), Award.class);
                award.setAwardId(awardId);
                
                Award updatedAward = awardService.updateAward(award);
                sendJsonResponse(response, HttpServletResponse.SC_OK, updatedAward);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid award ID format");
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
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Award ID is required");
            return;
        }

        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                int awardId = Integer.parseInt(pathParts[1]);
                boolean deleted = awardService.deleteAward(awardId);
                
                if (deleted) {
                    JsonObject responseObj = new JsonObject();
                    responseObj.addProperty("message", "Award deleted successfully");
                    sendJsonResponse(response, HttpServletResponse.SC_OK, responseObj);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                                    "Failed to delete award");
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request path");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid award ID format");
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