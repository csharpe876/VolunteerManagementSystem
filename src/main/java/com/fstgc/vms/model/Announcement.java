package com.fstgc.vms.model;

import java.sql.Timestamp;

/**
 * Announcement Entity Model
 * Represents an announcement in the system
 */
public class Announcement {
    private int announcementId;
    private String title;
    private String message;
    private Timestamp publishedDate;
    private Timestamp expiryDate;
    private String priority; // 'low', 'medium', 'high', 'urgent'
    private String targetAudience; // 'all', 'coordinators', 'volunteers'
    private Integer eventId;
    private Integer createdBy;
    private boolean isDeleted;

    // Constructors
    public Announcement() {
        this.publishedDate = new Timestamp(System.currentTimeMillis());
        this.priority = "medium";
        this.targetAudience = "all";
        this.isDeleted = false;
    }

    public Announcement(String title, String message) {
        this();
        this.title = title;
        this.message = message;
    }

    public Announcement(String title, String message, String priority, String targetAudience) {
        this(title, message);
        this.priority = priority;
        this.targetAudience = targetAudience;
    }

    // Getters and Setters
    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Timestamp publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    // Utility Methods
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.before(new Timestamp(System.currentTimeMillis()));
    }

    public boolean isActive() {
        return !isDeleted && !isExpired();
    }

    public boolean isHighPriority() {
        return "high".equals(priority) || "urgent".equals(priority);
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "announcementId=" + announcementId +
                ", title='" + title + '\'' +
                ", priority='" + priority + '\'' +
                ", targetAudience='" + targetAudience + '\'' +
                ", publishedDate=" + publishedDate +
                '}';
    }
}