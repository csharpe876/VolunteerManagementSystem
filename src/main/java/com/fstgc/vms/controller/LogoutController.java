package com.fstgc.vms.controller;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * LogoutController
 * Handles user logout and session termination
 */
@WebServlet("/api/logout")
public class LogoutController extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Logged out successfully");

            response.setStatus(200);
            out.print(gson.toJson(responseData));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("success", false);
            errorData.put("message", "An error occurred during logout");
            out.print(gson.toJson(errorData));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
