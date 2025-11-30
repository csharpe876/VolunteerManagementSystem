# Volunteer Management System (VMS) 🎯

> A comprehensive web-based application for managing volunteers, events, and community service activities

**Developed for**: Faculty of Science and Technology Guild Committee (FSTGC)  
**Institution**: The University of the West Indies, Mona Campus

---

## 📋 Overview

The Volunteer Management System streamlines volunteer coordination for the FSTGC by providing an automated platform for volunteer management, event coordination, attendance tracking, and recognition.

### Key Benefits
- ✅ Eliminates manual spreadsheet tracking
- ✅ Automates community service hour calculations
- ✅ Provides real-time event registration
- ✅ Facilitates volunteer recognition through badges
- ✅ Centralizes announcements and communications

## 🚀 Quick Start

**5-Minute Setup**: See **[QUICKSTART.md](QUICKSTART.md)**

**Detailed Guide**: See **[SETUP_GUIDE.md](SETUP_GUIDE.md)**

### Super Quick Start
```bash
# 1. Setup database (in MySQL)
CREATE DATABASE volunteer_management_system;
USE volunteer_management_system;
SOURCE database/schema.sql;
SOURCE database/sample_data.sql;

# 2. Build & Deploy
mvn clean package
cp target/*.war /xampp/tomcat/webapps/

# 3. Access at: http://localhost:8080/volunteer-management-system/
```

## 🎯 Key Features

### 👤 For Volunteers
- Personal dashboard with statistics
- Browse and register for events  
- Track volunteer hours automatically
- Earn badges for milestones
- View announcements
- Manage personal profile

### 👨‍💼 For Administrators
- Comprehensive volunteer management
- Event creation and scheduling
- Attendance tracking (check-in/out)
- Report generation (PDF/Excel)
- Announcement broadcasting
- Award/badge management

## 💻 Technology Stack

```
Frontend:  HTML5, CSS3, JavaScript (ES6+)
Backend:   Java 21 (Servlets, JDBC)
Database:  MySQL 8.0
Build:     Apache Maven 3.9+
Server:    Apache Tomcat 9+ (XAMPP)
Security:  BCrypt, Session Management
```

## 📁 Project Structure

```
VolunteerManagementSystem/
├── 📂 database/
│   ├── schema.sql              # Complete database schema
│   └── sample_data.sql         # Test data with accounts
│
├── 📂 src/main/
│   ├── 📂 java/com/fstgc/vms/
│   │   ├── 📂 controller/      # Servlets (LoginController, etc.)
│   │   ├── 📂 model/           # Entities (Volunteer, Event, etc.)
│   │   ├── 📂 repository/      # Data access layer
│   │   ├── 📂 service/         # Business logic
│   │   └── 📂 util/            # Utilities (DatabaseConnection)
│   │
│   └── 📂 webapp/
│       ├── 📂 css/             # Stylesheets
│       ├── 📂 js/              # JavaScript
│       ├── 📂 images/          # 🎨 Place FST logos here
│       ├── 🏠 index.html       # Redirects to login
│       ├── 🔐 login.html       # Entry point
│       ├── volunteer-dashboard.html
│       ├── admin-dashboard.html
│       └── 📂 WEB-INF/
│           └── web.xml
│
├── pom.xml                     # Maven configuration
├── README.md                   # This file
├── QUICKSTART.md              # 5-minute setup
├── SETUP_GUIDE.md             # Detailed docs
└── Software Design Specification.txt
```

## 🔐 Test Accounts

### Administrator
```
Username: admin
Password: admin123
Access:   Full system administration
```

### Volunteer
```
Email:    carl.sharpe@mymona.uwi.edu
Password: volunteer123
Access:   Volunteer dashboard and features
```

*Additional test accounts in `database/sample_data.sql`*

## 🎨 Logo Images Required

Save the FST branding logos to: `src/main/webapp/images/`

Required files:
- `fst-logo.png` - FST "Destined for Greatness" logo
- `uwi-fst-logo.png` - UWI FST official logo  
- `science-tech-logo.png` - Science & Technology icon
- `fst-guild-logo.png` - FST Guild Committee logo

See `src/main/webapp/images/README.md` for specifications.

## 📊 Database Tables

Core entities:
- **Volunteer** - User profiles with credentials
- **SystemAdmin** - Administrator accounts
- **Event** - Volunteer events and activities
- **Attendance** - Check-in/check-out records
- **Timesheet** - Volunteer hour summaries
- **Award** - Badges and achievements
- **AwardCriteria** - Award requirements
- **Announcement** - System notifications

## 🔌 API Endpoints

### Authentication
```
POST /api/login       # User login
POST /api/logout      # Session logout
```

### Volunteers
```
GET    /api/volunteers           # List all volunteers
GET    /api/volunteers/{id}      # Get volunteer by ID
POST   /api/volunteers           # Register volunteer
PUT    /api/volunteers/{id}      # Update volunteer
DELETE /api/volunteers/{id}      # Delete volunteer
```

### Events
```
GET    /api/events               # List events
GET    /api/events/{id}          # Get event details
POST   /api/events               # Create event
PUT    /api/events/{id}          # Update event
DELETE /api/events/{id}          # Delete event
```

*See SETUP_GUIDE.md for complete API documentation*

## 🛠️ Installation

### Prerequisites
- ✅ XAMPP (Apache + MySQL + Tomcat)
- ✅ Java JDK 21
- ✅ Apache Maven 3.9+
- ✅ Modern browser (Chrome/Firefox/Edge)

### Setup Steps

1. **Database Setup**
   ```sql
   CREATE DATABASE volunteer_management_system;
   USE volunteer_management_system;
   SOURCE database/schema.sql;
   SOURCE database/sample_data.sql;
   ```

2. **Configure Connection**  
   Update `src/main/java/com/fstgc/vms/util/DatabaseConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/volunteer_management_system";
   private static final String USER = "root";
   private static final String PASSWORD = "";
   ```

3. **Build Project**
   ```bash
   mvn clean package
   ```

4. **Deploy**
   ```bash
   cp target/volunteer-management-system.war C:/xampp/tomcat/webapps/
   ```

5. **Access**
   ```
   http://localhost:8080/volunteer-management-system/
   ```

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| **Build fails** | Run `mvn clean install -U` |
| **Database connection error** | Check MySQL running, verify credentials |
| **Login not working** | Ensure sample data loaded, check browser console |
| **Tomcat won't start** | Verify port 8080 available, check logs |
| **Page not loading** | Clear browser cache, check Tomcat deployment |

### Log Locations
- Tomcat logs: `C:/xampp/tomcat/logs/`
- MySQL logs: `C:/xampp/mysql/data/`
- Build logs: `target/` folder

## 👥 Development Team

**Group: THUR_5-7_G01**

| Name | Student ID | Email |
|------|-----------|-------|
| Carl Sharpe | 05017725 | carl.sharpe@mymona.uwi.edu |
| Jaedon Beckford | 621696555 | jaedon.beckford@mymona.uwi.edu |
| Ti-Carla Thompson | 620147445 | ti-carla.thompson@mymona.uwi.edu |
| Ashani Falconer | 620155746 | ashani.falconer@mymona.uwi.edu |
| Oneil Marshall | 620143423 | oneil.marshall@mymona.uwi.edu |
| Johnathan Jackson | 620169527 | johnathan.jackson@mymona.uwi.edu |

## 📚 Academic Information

- **Course**: COMP2140 – Introduction to Software Engineering
- **Instructor**: Dr. R. Anderson
- **Studio Facilitator**: Mr. E. Ferguson
- **Date**: November 22, 2025
- **Institution**: The University of the West Indies, Mona Campus
- **Faculty**: Faculty of Science and Technology

## 📖 Documentation

- **[QUICKSTART.md](QUICKSTART.md)** - Get running in 5 minutes ⚡
- **[SETUP_GUIDE.md](SETUP_GUIDE.md)** - Comprehensive setup guide 📘
- **Software Design Specification.txt** - Architecture documentation 🏗️

## 🔒 Security Features

- ✅ BCrypt password hashing
- ✅ Session-based authentication
- ✅ Role-based access control (RBAC)
- ✅ SQL injection prevention (PreparedStatements)
- ✅ XSS protection
- ✅ CORS filtering

## 📈 Sample Data Included

- 2 Administrator accounts
- 5 Volunteer test accounts
- 4 Sample events
- 3 Announcements
- 5 Award types with criteria
- Sample awards assigned to volunteers

## 🚀 Future Enhancements

- [ ] Email notification system
- [ ] Mobile responsive design improvements
- [ ] Advanced reporting analytics
- [ ] Calendar integration
- [ ] Document management
- [ ] SMS notifications
- [ ] Social media integration

## 📄 License

This project is developed as part of academic coursework at The University of the West Indies, Mona Campus. All rights reserved.

## 🤝 Contributing

This is an academic project. For questions or suggestions:
1. Review documentation files
2. Check Tomcat/MySQL logs
3. Contact development team members
4. Consult course instructor

## 🙏 Acknowledgments

- **Dr. R. Anderson** - Course Instructor
- **Mr. E. Ferguson** - Studio Facilitator
- **FST Guild Committee** - Project Stakeholders
- **The University of the West Indies** - Academic Support

---

<div align="center">

**Built with ❤️ for the FST Guild Committee**

*Empowering volunteers, enriching communities* 🌟

</div>

