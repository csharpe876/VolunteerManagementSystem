# Volunteer Management System - Setup Guide

## Project Overview
The Volunteer Management System (VMS) is a comprehensive web-based application developed for the Faculty of Science and Technology Guild Committee (FSTGC) at UWI Mona. The system streamlines volunteer management, event coordination, attendance tracking, and award recognition.

## Technology Stack
- **Frontend**: HTML5, CSS3, JavaScript
- **Backend**: Java (Servlets, JSP)
- **Database**: MySQL
- **Server**: Apache Tomcat (via XAMPP)
- **Build Tool**: Maven
- **Java Version**: Java 21 LTS

## Prerequisites
1. **XAMPP** with Apache and MySQL
2. **Java Development Kit (JDK) 21**
3. **Apache Maven**
4. **Modern Web Browser** (Chrome, Firefox, Edge)

## Installation Steps

### 1. Database Setup

1. Start XAMPP and ensure MySQL is running
2. Open phpMyAdmin (http://localhost/phpmyadmin)
3. Create a new database or run the schema:
   ```sql
   -- Navigate to SQL tab and execute
   source C:/xampp/htdocs/VolunteerManagementSystem/database/schema.sql
   ```
4. Load sample data:
   ```sql
   source C:/xampp/htdocs/VolunteerManagementSystem/database/sample_data.sql
   ```

### 2. Configure Database Connection

Update the database credentials in:
`src/main/java/com/fstgc/vms/util/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/volunteer_management_system";
private static final String USER = "root";  // Your MySQL username
private static final String PASSWORD = "";  // Your MySQL password
```

### 3. Build the Project

Open terminal in the project root directory:

```bash
# Clean and build
mvn clean package

# This will create a WAR file in target/ directory
```

### 4. Deploy to Tomcat

1. Copy the WAR file from `target/volunteer-management-system.war`
2. Paste it into `C:/xampp/tomcat/webapps/`
3. Start Tomcat through XAMPP Control Panel
4. The application will auto-deploy

### 5. Access the Application

Open your browser and navigate to:
```
http://localhost:8080/volunteer-management-system/
```

## Default Login Credentials

### Administrator Account
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: admin@fstgc.uwi.edu

### Coordinator Account
- **Username**: `coordinator`
- **Password**: `admin123`
- **Email**: coordinator@fstgc.uwi.edu

### Sample Volunteer Accounts
- **Email**: carl.sharpe@mymona.uwi.edu
- **Password**: volunteer123

(Other sample volunteers use the same password)

## Features

### For Volunteers:
- ✅ User authentication and profile management
- ✅ View and register for events
- ✅ Track volunteer hours via timesheet
- ✅ Earn awards and badges
- ✅ View announcements

### For Administrators:
- ✅ Manage volunteers
- ✅ Create and manage events
- ✅ Record attendance
- ✅ Generate reports
- ✅ Post announcements
- ✅ Award badges

## Project Structure

```
VolunteerManagementSystem/
├── database/
│   ├── schema.sql          # Database schema
│   └── sample_data.sql     # Sample test data
├── src/
│   └── main/
│       ├── java/
│       │   └── com/fstgc/vms/
│       │       ├── controller/     # Servlet controllers
│       │       ├── model/          # Entity models
│       │       ├── repository/     # Data access layer
│       │       ├── service/        # Business logic
│       │       └── util/           # Utilities
│       └── webapp/
│           ├── css/               # Stylesheets
│           ├── js/                # JavaScript files
│           ├── images/            # Logo and images
│           ├── login.html         # Login page
│           ├── volunteer-dashboard.html
│           └── WEB-INF/
│               └── web.xml        # Servlet configuration
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## API Endpoints

### Authentication
- `POST /api/login` - User login
- `POST /api/logout` - User logout

### Volunteers
- `GET /api/volunteers` - Get all volunteers
- `GET /api/volunteers/{id}` - Get volunteer by ID
- `POST /api/volunteers` - Register new volunteer
- `PUT /api/volunteers/{id}` - Update volunteer
- `DELETE /api/volunteers/{id}` - Delete volunteer

### Events
- `GET /api/events` - Get all events
- `GET /api/events/{id}` - Get event by ID
- `POST /api/events` - Create event
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event

### Attendance
- `POST /api/attendance/checkin` - Check in to event
- `POST /api/attendance/checkout` - Check out from event
- `GET /api/attendance/volunteer/{id}` - Get volunteer attendance

### Announcements
- `GET /api/announcements` - Get all announcements
- `POST /api/announcements` - Create announcement

## Troubleshooting

### Database Connection Issues
1. Ensure MySQL is running in XAMPP
2. Verify database credentials in `DatabaseConnection.java`
3. Check that the database `volunteer_management_system` exists

### Tomcat Not Starting
1. Check if port 8080 is available
2. Review Tomcat logs in `C:/xampp/tomcat/logs/`
3. Ensure Java 21 is properly installed

### Build Errors
1. Run `mvn clean install` to refresh dependencies
2. Ensure Java 21 is set as JAVA_HOME
3. Check Maven installation: `mvn -version`

### Login Issues
1. Verify the database has sample data loaded
2. Check browser console for JavaScript errors
3. Ensure cookies/session storage is enabled

## Development Team

**Group: THUR_5-7_G01**

- Carl Sharpe - 05017725
- Jaedon Beckford - 621696555
- Ti-Carla Thompson - 620147445
- Ashani Falconer - 620155746
- Oneil Marshall - 620143423
- Johnathan Jackson - 620169527

**Course**: COMP2140 – Introduction to Software Engineering  
**Instructor**: Dr. R. Anderson  
**Studio Facilitator**: Mr. E. Ferguson  
**Date**: November 22, 2025

## License
This project is developed as part of academic coursework at The University of the West Indies, Mona Campus.

## Support
For issues or questions, please contact the development team or your course instructor.
