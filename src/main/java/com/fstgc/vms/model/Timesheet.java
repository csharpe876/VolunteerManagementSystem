package com.fstgc.vms.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Timesheet Entity Model
 * Represents a timesheet for a volunteer over a period
 */
public class Timesheet {
    private int timesheetId;
    private int volunteerId;
    private Date periodStartDate;
    private Date periodEndDate;
    private BigDecimal totalHours;
    private BigDecimal approvedHours;
    private String approvalStatus; // 'pending', 'approved', 'rejected'
    private Integer approvedBy;
    private Timestamp approvalDate;
    private String rejectionReason;
    private Timestamp createdDate;

    // Constructors
    public Timesheet() {
        this.approvalStatus = "pending";
        this.totalHours = BigDecimal.ZERO;
        this.createdDate = new Timestamp(System.currentTimeMillis());
    }

    public Timesheet(int volunteerId, Date periodStartDate, Date periodEndDate) {
        this();
        this.volunteerId = volunteerId;
        this.periodStartDate = periodStartDate;
        this.periodEndDate = periodEndDate;
    }

    // Getters and Setters
    public int getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(int timesheetId) {
        this.timesheetId = timesheetId;
    }

    public int getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Date getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(Date periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public Date getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(Date periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public BigDecimal getApprovedHours() {
        return approvedHours;
    }

    public void setApprovedHours(BigDecimal approvedHours) {
        this.approvedHours = approvedHours;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Timestamp getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Timestamp approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    // Utility Methods
    public boolean isPending() {
        return "pending".equals(approvalStatus);
    }

    public boolean isApproved() {
        return "approved".equals(approvalStatus);
    }

    public boolean isRejected() {
        return "rejected".equals(approvalStatus);
    }

    @Override
    public String toString() {
        return "Timesheet{" +
                "timesheetId=" + timesheetId +
                ", volunteerId=" + volunteerId +
                ", periodStartDate=" + periodStartDate +
                ", periodEndDate=" + periodEndDate +
                ", totalHours=" + totalHours +
                ", approvalStatus='" + approvalStatus + '\'' +
                '}';
    }
}