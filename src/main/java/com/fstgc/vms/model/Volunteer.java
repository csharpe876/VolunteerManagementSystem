package com.fstgc.vms.model;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Volunteer Entity Model
 * Represents a volunteer in the system
 */
public class Volunteer {
    private int volunteerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Timestamp registrationDate;
    private boolean isActive;
    private String profilePhotoUrl;
    private LocalDate dateOfBirth;
    private String address;
    private String passwordHash;
    private String skills;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private double totalHours;
    private Timestamp lastLogin;

    // Constructors
    public Volunteer() {
        this.registrationDate = new Timestamp(System.currentTimeMillis());
        this.isActive = true;
        this.totalHours = 0.0;
    }

    public Volunteer(String firstName, String lastName, String email, String phone) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public Volunteer(int volunteerId, String firstName, String lastName, String email, 
                     String phone, Timestamp registrationDate, boolean isActive, 
                     String profilePhotoUrl, LocalDate dateOfBirth, String address) {
        this.volunteerId = volunteerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
        this.profilePhotoUrl = profilePhotoUrl;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.totalHours = 0.0;
    }

    // Getters and Setters
    public int getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Utility Methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Volunteer{" +
                "volunteerId=" + volunteerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", isActive=" + isActive +
                ", totalHours=" + totalHours +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volunteer volunteer = (Volunteer) o;
        return volunteerId == volunteer.volunteerId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(volunteerId);
    }
}