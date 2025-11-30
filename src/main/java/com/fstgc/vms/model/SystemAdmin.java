package com.fstgc.vms.model;

import java.sql.Timestamp;

/**
 * SystemAdmin Entity Model
 * Represents an administrator in the system
 */
public class SystemAdmin {
    private int adminId;
    private String username;
    private String passwordHash;
    private String email;
    private String firstName;
    private String lastName;
    private String role; // 'super_admin', 'coordinator'
    private String permissions; // JSON string
    private Timestamp lastLogin;
    private int failedLoginAttempts;
    private String accountStatus; // 'active', 'inactive', 'locked'
    private Timestamp createdDate;
    private Integer createdBy;

    // Constructors
    public SystemAdmin() {
        this.failedLoginAttempts = 0;
        this.accountStatus = "active";
        this.createdDate = new Timestamp(System.currentTimeMillis());
    }

    public SystemAdmin(String username, String email, String firstName, 
                       String lastName, String role) {
        this();
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    // Utility Methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isSuperAdmin() {
        return "super_admin".equals(role);
    }

    public boolean isCoordinator() {
        return "coordinator".equals(role);
    }

    public boolean isActive() {
        return "active".equals(accountStatus);
    }

    public boolean isLocked() {
        return "locked".equals(accountStatus);
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountStatus = "locked";
        }
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    @Override
    public String toString() {
        return "SystemAdmin{" +
                "adminId=" + adminId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                '}';
    }
}