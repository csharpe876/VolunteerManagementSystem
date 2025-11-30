package com.fstgc.vms.model;

import java.sql.Timestamp;

/**
 * AwardCriteria Entity Model
 * Represents criteria for earning badges/awards
 */
public class AwardCriteria {
    private int criteriaId;
    private String badgeName;
    private String description;
    private String criteriaType; // 'total_hours', 'event_count', 'consecutive_months', 'special_achievement'
    private int thresholdValue;
    private String badgeTier; // 'bronze', 'silver', 'gold', 'platinum'
    private String badgeIconUrl;
    private boolean isActive;
    private Timestamp createdDate;

    // Constructors
    public AwardCriteria() {
        this.isActive = true;
        this.createdDate = new Timestamp(System.currentTimeMillis());
    }

    public AwardCriteria(String badgeName, String description, String criteriaType, 
                        int thresholdValue, String badgeTier) {
        this();
        this.badgeName = badgeName;
        this.description = description;
        this.criteriaType = criteriaType;
        this.thresholdValue = thresholdValue;
        this.badgeTier = badgeTier;
    }

    // Getters and Setters
    public int getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(int criteriaId) {
        this.criteriaId = criteriaId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(String criteriaType) {
        this.criteriaType = criteriaType;
    }

    public int getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(int thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getBadgeTier() {
        return badgeTier;
    }

    public void setBadgeTier(String badgeTier) {
        this.badgeTier = badgeTier;
    }

    public String getBadgeIconUrl() {
        return badgeIconUrl;
    }

    public void setBadgeIconUrl(String badgeIconUrl) {
        this.badgeIconUrl = badgeIconUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    // Utility Methods
    public boolean isTotalHoursCriteria() {
        return "total_hours".equals(criteriaType);
    }

    public boolean isEventCountCriteria() {
        return "event_count".equals(criteriaType);
    }

    @Override
    public String toString() {
        return "AwardCriteria{" +
                "criteriaId=" + criteriaId +
                ", badgeName='" + badgeName + '\'' +
                ", criteriaType='" + criteriaType + '\'' +
                ", thresholdValue=" + thresholdValue +
                ", badgeTier='" + badgeTier + '\'' +
                '}';
    }
}