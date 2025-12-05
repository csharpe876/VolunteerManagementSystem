package com.fstgc.vms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Main Spring Boot Application for Volunteer Management System
 * 
 * This standalone application includes an embedded Tomcat server.
 * No separate Tomcat installation required!
 * 
 * To run: java -jar volunteer-management-system.jar
 * Access at: http://localhost:8080/login.html
 */
@SpringBootApplication
@ServletComponentScan(basePackages = "com.fstgc.vms") // Enables @WebServlet annotations
public class VolunteerManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolunteerManagementApplication.class, args);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Volunteer Management System Started Successfully!");
        System.out.println("=".repeat(60));
        System.out.println("Access the application at: http://localhost:8080/login.html");
        System.out.println("\nTest Accounts:");
        System.out.println("  Admin:     username: admin");
        System.out.println("             password: admin123");
        System.out.println("\n  Volunteer: username: carl.sharpe@mymona.uwi.edu");
        System.out.println("             password: volunteer123");
        System.out.println("=".repeat(60) + "\n");
    }
}
