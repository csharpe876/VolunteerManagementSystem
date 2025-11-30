package com.fstgc.vms.service;

import com.fstgc.vms.model.Award;
import com.fstgc.vms.repository.AwardRepository;
import com.fstgc.vms.repository.AttendanceRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Award Service Layer
 * Contains business logic for award/badge operations
 */
public class AwardService {
    private final AwardRepository awardRepository;
    private final AttendanceRepository attendanceRepository;

    public AwardService() {
        this.awardRepository = new AwardRepository();
        this.attendanceRepository = new AttendanceRepository();
    }

    public Award assignAward(Award award) throws SQLException {
        validateAward(award);
        
        // Check if award already exists for this volunteer and criteria
        if (award.getCriteriaId() != null) {
            boolean alreadyAwarded = awardRepository.checkIfAwarded(
                award.getVolunteerId(), 
                award.getCriteriaId()
            );
            
            if (alreadyAwarded) {
                throw new IllegalArgumentException("This award has already been given to this volunteer");
            }
        }
        
        return awardRepository.save(award);
    }

    public void checkAndAssignAutomaticAwards(int volunteerId) throws SQLException {
        Double totalHours = attendanceRepository.getTotalHoursByVolunteer(volunteerId);
        int eventCount = attendanceRepository.getEventCountByVolunteer(volunteerId);
        
        // Check for hour-based badges
        if (totalHours >= 200 && !awardRepository.checkIfAwarded(volunteerId, 4)) {
            Award award = new Award(volunteerId, "Legend Status", "platinum");
            award.setBadgeDescription("Accomplished 200+ hours of volunteering");
            award.setCriteriaId(4);
            awardRepository.save(award);
        } else if (totalHours >= 100 && !awardRepository.checkIfAwarded(volunteerId, 3)) {
            Award award = new Award(volunteerId, "Century Club", "gold");
            award.setBadgeDescription("Achieved 100 hours of volunteer work");
            award.setCriteriaId(3);
            awardRepository.save(award);
        } else if (totalHours >= 50 && !awardRepository.checkIfAwarded(volunteerId, 2)) {
            Award award = new Award(volunteerId, "Dedicated Volunteer", "silver");
            award.setBadgeDescription("Reached 50 hours of community service");
            award.setCriteriaId(2);
            awardRepository.save(award);
        } else if (totalHours >= 10 && !awardRepository.checkIfAwarded(volunteerId, 1)) {
            Award award = new Award(volunteerId, "First Steps", "bronze");
            award.setBadgeDescription("Completed your first 10 hours of volunteering");
            award.setCriteriaId(1);
            awardRepository.save(award);
        }
        
        // Check for event-based badges
        if (eventCount >= 10 && !awardRepository.checkIfAwarded(volunteerId, 5)) {
            Award award = new Award(volunteerId, "Event Enthusiast", "bronze");
            award.setBadgeDescription("Participated in 10 different events");
            award.setCriteriaId(5);
            awardRepository.save(award);
        }
    }

    public List<Award> getAwardsByVolunteer(int volunteerId) throws SQLException {
        return awardRepository.findByVolunteer(volunteerId);
    }

    public List<Award> getAwardsByTier(String tier) throws SQLException {
        return awardRepository.findByTier(tier);
    }

    public List<Award> getFeaturedAwards() throws SQLException {
        return awardRepository.findFeatured();
    }

    public Award getAwardById(int awardId) throws SQLException {
        Award award = awardRepository.findById(awardId);
        if (award == null) {
            throw new IllegalArgumentException("Award not found");
        }
        return award;
    }

    public Award updateAward(Award award) throws SQLException {
        Award existing = awardRepository.findById(award.getAwardId());
        if (existing == null) {
            throw new IllegalArgumentException("Award not found");
        }
        return awardRepository.update(award);
    }

    public boolean deleteAward(int awardId) throws SQLException {
        Award award = awardRepository.findById(awardId);
        if (award == null) {
            throw new IllegalArgumentException("Award not found");
        }
        return awardRepository.delete(awardId);
    }

    public int getAwardCountByVolunteer(int volunteerId) throws SQLException {
        return awardRepository.getAwardCountByVolunteer(volunteerId);
    }

    public int getAwardCountByTier(String tier) throws SQLException {
        return awardRepository.getAwardCountByTier(tier);
    }

    private void validateAward(Award award) {
        if (award == null) {
            throw new IllegalArgumentException("Award object cannot be null");
        }
        
        if (award.getVolunteerId() <= 0) {
            throw new IllegalArgumentException("Valid volunteer ID is required");
        }
        
        if (award.getBadgeName() == null || award.getBadgeName().trim().isEmpty()) {
            throw new IllegalArgumentException("Badge name is required");
        }
        
        if (award.getBadgeTier() == null) {
            throw new IllegalArgumentException("Badge tier is required");
        }
    }
}