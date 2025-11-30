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
    private String status; // 'active' or 'inactive'
    private String profilePhotoUrl;
    private LocalDate dateOfBirth;
    private String address;
    private String passwordHash;

    // Constructors
    public Volunteer() {
        this.registrationDate = new Timestamp(System.currentTimeMillis());
        this.status = "active";
    }

    public Volunteer(String firstName, String lastName, String email, String phone) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public Volunteer(int volunteerId, String firstName, String lastName, String email, 
                     String phone, Timestamp registrationDate, String status, 
                     String profilePhotoUrl, LocalDate dateOfBirth, String address) {
        this.volunteerId = volunteerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.registrationDate = registrationDate;
        this.status = status;
        this.profilePhotoUrl = profilePhotoUrl;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    // Utility Methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isActive() {
        return "active".equals(status);
    }

    @Override
    public String toString() {
        return "Volunteer{" +
                "volunteerId=" + volunteerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
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