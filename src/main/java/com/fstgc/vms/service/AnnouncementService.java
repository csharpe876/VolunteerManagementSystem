package com.fstgc.vms.service;

import com.fstgc.vms.model.Announcement;
import com.fstgc.vms.repository.AnnouncementRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Announcement Service Layer
 * Contains business logic for announcement operations
 */
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementService() {
        this.announcementRepository = new AnnouncementRepository();
    }

    public Announcement createAnnouncement(Announcement announcement) throws SQLException {
        validateAnnouncement(announcement);
        return announcementRepository.save(announcement);
    }

    public Announcement updateAnnouncement(Announcement announcement) throws SQLException {
        validateAnnouncement(announcement);
        
        Announcement existing = announcementRepository.findById(announcement.getAnnouncementId());
        if (existing == null) {
            throw new IllegalArgumentException("Announcement not found");
        }
        
        return announcementRepository.update(announcement);
    }

    public boolean deleteAnnouncement(int announcementId) throws SQLException {
        Announcement announcement = announcementRepository.findById(announcementId);
        if (announcement == null) {
            throw new IllegalArgumentException("Announcement not found");
        }
        return announcementRepository.delete(announcementId);
    }

    public Announcement getAnnouncementById(int announcementId) throws SQLException {
        Announcement announcement = announcementRepository.findById(announcementId);
        if (announcement == null) {
            throw new IllegalArgumentException("Announcement not found");
        }
        return announcement;
    }

    public List<Announcement> getAllAnnouncements() throws SQLException {
        return announcementRepository.findAll();
    }

    public List<Announcement> getActiveAnnouncements() throws SQLException {
        return announcementRepository.findActive();
    }

    public List<Announcement> getAnnouncementsByPriority(String priority) throws SQLException {
        return announcementRepository.findByPriority(priority);
    }

    public List<Announcement> getAnnouncementsForUser(String userType) throws SQLException {
        return announcementRepository.findByTargetAudience(userType);
    }

    public int getActiveAnnouncementCount() throws SQLException {
        return announcementRepository.getActiveAnnouncementCount();
    }

    private void validateAnnouncement(Announcement announcement) {
        if (announcement == null) {
            throw new IllegalArgumentException("Announcement object cannot be null");
        }
        
        if (announcement.getTitle() == null || announcement.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Announcement title is required");
        }
        
        if (announcement.getMessage() == null || announcement.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Announcement message is required");
        }
        
        if (announcement.getPriority() == null) {
            throw new IllegalArgumentException("Announcement priority is required");
        }
        
        if (announcement.getTargetAudience() == null) {
            throw new IllegalArgumentException("Target audience is required");
        }
    }
}
