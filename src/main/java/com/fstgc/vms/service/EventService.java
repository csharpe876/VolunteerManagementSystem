package com.fstgc.vms.service;

import com.fstgc.vms.model.Event;
import com.fstgc.vms.repository.EventRepository;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Event Service Layer
 * Contains business logic for event operations
 */
public class EventService {
    private final EventRepository eventRepository;

    public EventService() {
        this.eventRepository = new EventRepository();
    }

    public Event createEvent(Event event) throws SQLException {
        validateEvent(event);
        
        // Check for scheduling conflicts
        checkSchedulingConflicts(event);
        
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() throws SQLException {
        return eventRepository.findAll();
    }

    public List<Event> getUpcomingEvents() throws SQLException {
        return eventRepository.findUpcoming();
    }

    public List<Event> getEventsByStatus(String status) throws SQLException {
        return eventRepository.findByStatus(status);
    }

    public Event getEventById(int eventId) throws SQLException {
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found with ID: " + eventId);
        }
        return event;
    }

    public Event updateEvent(Event event) throws SQLException {
        validateEvent(event);
        
        Event existingEvent = eventRepository.findById(event.getEventId());
        if (existingEvent == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        return eventRepository.update(event);
    }

    public boolean cancelEvent(int eventId) throws SQLException {
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        return eventRepository.delete(eventId);
    }

    public boolean publishEvent(int eventId) throws SQLException {
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        event.setStatus("published");
        eventRepository.update(event);
        return true;
    }

    public boolean registerVolunteer(int eventId) throws SQLException {
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        if (event.isFull()) {
            throw new IllegalArgumentException("Event is full");
        }
        
        if (!event.isPublished()) {
            throw new IllegalArgumentException("Event is not published");
        }
        
        return eventRepository.updateRegistrationCount(eventId, 1);
    }

    public boolean unregisterVolunteer(int eventId) throws SQLException {
        Event event = eventRepository.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        return eventRepository.updateRegistrationCount(eventId, -1);
    }

    public List<Event> searchEvents(String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllEvents();
        }
        return eventRepository.searchByTitle(searchTerm.trim());
    }

    public int getTotalEventCount() throws SQLException {
        return eventRepository.getEventCount();
    }

    private void validateEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event object cannot be null");
        }
        
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Event title is required");
        }
        
        if (event.getEventDate() == null) {
            throw new IllegalArgumentException("Event date is required");
        }
        
        if (event.getStartTime() == null || event.getEndTime() == null) {
            throw new IllegalArgumentException("Event start and end times are required");
        }
        
        if (event.getEndTime().before(event.getStartTime())) {
            throw new IllegalArgumentException("Event end time must be after start time");
        }
        
        if (event.getLocation() == null || event.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Event location is required");
        }
        
        if (event.getCapacity() <= 0) {
            throw new IllegalArgumentException("Event capacity must be greater than 0");
        }
        
        if (event.getEventType() == null) {
            throw new IllegalArgumentException("Event type is required");
        }
    }

    private void checkSchedulingConflicts(Event event) throws SQLException {
        // Check if there's already an event at the same location and overlapping time
        Date eventDate = event.getEventDate();
        List<Event> eventsOnSameDate = eventRepository.findByDateRange(eventDate, eventDate);
        
        for (Event existingEvent : eventsOnSameDate) {
            if (existingEvent.getLocation().equalsIgnoreCase(event.getLocation())) {
                // Check for time overlap
                boolean startsDuring = event.getStartTime().after(existingEvent.getStartTime()) && 
                                      event.getStartTime().before(existingEvent.getEndTime());
                boolean endsDuring = event.getEndTime().after(existingEvent.getStartTime()) && 
                                    event.getEndTime().before(existingEvent.getEndTime());
                boolean encompasses = event.getStartTime().before(existingEvent.getStartTime()) && 
                                     event.getEndTime().after(existingEvent.getEndTime());
                
                if (startsDuring || endsDuring || encompasses) {
                    throw new IllegalArgumentException(
                        "Scheduling conflict: Another event is already scheduled at this location during this time"
                    );
                }
            }
        }
    }
}