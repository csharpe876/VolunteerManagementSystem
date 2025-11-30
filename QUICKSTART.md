# Volunteer Management System - Quick Start Guide

## 🚀 Quick Setup (5 Minutes)

### Step 1: Database Setup
1. Open XAMPP Control Panel
2. Start **Apache** and **MySQL**
3. Click **Admin** button next to MySQL (opens phpMyAdmin)
4. Execute the following commands in SQL tab:

```sql
CREATE DATABASE volunteer_management_system;
USE volunteer_management_system;
```

5. Go to **Import** tab
6. Choose file: `database/schema.sql` → Click **Go**
7. Choose file: `database/sample_data.sql` → Click **Go**

### Step 2: Build the Project
Open terminal in project folder:
```bash
mvn clean package
```

### Step 3: Deploy
1. Copy `target/volunteer-management-system.war`
2. Paste into `C:/xampp/tomcat/webapps/`
3. In XAMPP, click **Start** for Tomcat

### Step 4: Access Application
Open browser: **http://localhost:8080/volunteer-management-system/**

## 🔐 Test Accounts

### Admin Login
```
Username: admin
Password: admin123
```

### Volunteer Login
```
Email: carl.sharpe@mymona.uwi.edu
Password: volunteer123
```

## ✨ Key Features to Test

### As Admin:
1. View dashboard statistics
2. Browse volunteer list
3. View and create events
4. Post announcements

### As Volunteer:
1. View personal dashboard
2. Check total hours
3. Browse available events
4. View earned awards
5. Update profile

## 🎨 Application Images

### Required Images (Place in `src/main/webapp/images/`):
- `fst-logo.png` - FST main logo
- `uwi-fst-logo.png` - UWI FST logo
- `science-tech-logo.png` - Science & Technology logo
- `fst-guild-logo.png` - FST Guild logo

## 🛠️ Troubleshooting

### Cannot connect to database?
- Ensure MySQL is running in XAMPP
- Check credentials in `DatabaseConnection.java`

### Tomcat won't start?
- Check if port 8080 is available
- Try stopping and restarting in XAMPP

### Login not working?
- Verify sample data was loaded
- Check browser console (F12) for errors
- Clear browser cache

### Build fails?
- Ensure Java 21 is installed: `java -version`
- Ensure Maven is installed: `mvn -version`
- Run: `mvn clean install -U`

## 📁 Project Structure
```
webapp/
├── login.html              ← First page loaded
├── index.html              ← Redirects to login
├── volunteer-dashboard.html
├── admin-dashboard.html
├── css/
│   ├── login.css
│   └── dashboard.css
├── js/
│   ├── login.js
│   ├── volunteer-dashboard.js
│   └── admin-dashboard.js
└── images/                 ← Place FST logos here
```

## 🎯 Next Steps

1. **Customize**: Update colors and branding
2. **Add Images**: Place FST logos in images folder
3. **Test Features**: Try all login accounts
4. **Explore API**: Check API endpoints in SETUP_GUIDE.md
5. **Add Data**: Create more volunteers/events through admin panel

## 📞 Support

Issues? Check:
1. SETUP_GUIDE.md - Detailed documentation
2. Tomcat logs - `C:/xampp/tomcat/logs/`
3. Browser console - F12 Developer Tools

## 📝 Sample Data Included

- 2 Admin accounts (admin, coordinator)
- 5 Volunteer accounts
- 4 Sample events
- 3 Announcements
- 5 Award types
- Sample awards assigned to volunteers

**All passwords**: See test accounts section above

---

**Happy Volunteering! 🎉**

*Faculty of Science and Technology Guild Committee*  
*The University of the West Indies, Mona*
