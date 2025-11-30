package com.fstgc.vms.model;

import java.sql.Timestamp;

/**
 * Award Entity Model
 * Represents a badge/award earned by a volunteer
 */
public class Award {
    private int awardId;
    private int volunteerId;
    private String badgeName;
    private String badgeDescription;
    private Integer criteriaId;
    private Timestamp dateEarned;
    private String badgeTier; // 'bronze', 'silver', 'gold', 'platinum'
    private String badgeIconUrl;
    private boolean isFeatured;

    // Constructors
    public Award() {
        this.dateEarned = new Timestamp(System.currentTimeMillis());
        this.isFeatured = false;
    }

    public Award(int volunteerId, String badgeName, String badgeTier) {
        this();
        this.volunteerId = volunteerId;
        this.badgeName = badgeName;
        this.badgeTier = badgeTier;
    }

    public Award(int volunteerId, String badgeName, String badgeDescription, 
                 String badgeTier, Integer criteriaId) {
        this(volunteerId, badgeName, badgeTier);
        this.badgeDescription = badgeDescription;
        this.criteriaId = criteriaId;
    }

    // Getters and Setters
    public int getAwardId() {
        return awardId;
    }

    public void setAwardId(int awardId) {
        this.awardId = awardId;
    }

    public int getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getBadgeName() {
        return badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    public String getBadgeDescription() {
        return badgeDescription;
    }

    public void setBadgeDescription(String badgeDescription) {
        this.badgeDescription = badgeDescription;
    }

    public Integer getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(Integer criteriaId) {
        this.criteriaId = criteriaId;
    }

    public Timestamp getDateEarned() {
        return dateEarned;
    }

    public void setDateEarned(Timestamp dateEarned) {
        this.dateEarned = dateEarned;
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

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    // Utility Methods
    public int getTierLevel() {
        switch (badgeTier.toLowerCase()) {
            case "bronze": return 1;
            case "silver": return 2;
            case "gold": return 3;
            case "platinum": return 4;
            default: return 0;
        }
    }

    public boolean isPlatinum() {
        return "platinum".equals(badgeTier);
    }

    public boolean isGold() {
        return "gold".equals(badgeTier);
    }

    @Override
    public String toString() {
        return "Award{" +
                "awardId=" + awardId +
                ", volunteerId=" + volunteerId +
                ", badgeName='" + badgeName + '\'' +
                ", badgeTier='" + badgeTier + '\'' +
                ", dateEarned=" + dateEarned +
                '}';
    }
}