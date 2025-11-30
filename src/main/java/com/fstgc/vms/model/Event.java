package com.fstgc.vms.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Event Entity Model
 * Represents an event/activity in the system
 */
public class Event {
    private int eventId;
    private String title;
    private String description;
    private Date eventDate;
    private Time startTime;
    private Time endTime;
    private String location;
    private int capacity;
    private int currentRegistrations;
    private String eventType; // 'workshop', 'meeting', 'community_service', 'social'
    private String status; // 'draft', 'published', 'cancelled', 'completed'
    private Integer createdBy;
    private Timestamp createdDate;

    // Constructors
    public Event() {
        this.currentRegistrations = 0;
        this.status = "draft";
        this.createdDate = new Timestamp(System.currentTimeMillis());
    }

    public Event(String title, String description, Date eventDate, Time startTime, 
                 Time endTime, String location, int capacity, String eventType) {
        this();
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.capacity = capacity;
        this.eventType = eventType;
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentRegistrations() {
        return currentRegistrations;
    }

    public void setCurrentRegistrations(int currentRegistrations) {
        this.currentRegistrations = currentRegistrations;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    // Utility Methods
    public boolean isFull() {
        return currentRegistrations >= capacity;
    }

    public int getAvailableSpots() {
        return capacity - currentRegistrations;
    }

    public boolean isPublished() {
        return "published".equals(status);
    }

    public boolean canRegister() {
        return isPublished() && !isFull();
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", title='" + title + '\'' +
                ", eventDate=" + eventDate +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", currentRegistrations=" + currentRegistrations +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventId == event.eventId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(eventId);
    }
}