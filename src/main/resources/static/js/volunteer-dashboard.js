// Check authentication
if (!sessionStorage.getItem('token')) {
    window.location.href = 'login.html';
}

const user = JSON.parse(sessionStorage.getItem('user'));
if (user.userType !== 'volunteer') {
    window.location.href = 'admin-dashboard.html';
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    loadUserInfo();
    loadDashboardData();
});

// Load user information
function loadUserInfo() {
    const userName = document.getElementById('user-name');
    const userAvatar = document.getElementById('user-avatar');
    
    userName.textContent = `${user.firstName} ${user.lastName}`;
    userAvatar.textContent = user.firstName.charAt(0).toUpperCase();
}

// Load dashboard statistics
async function loadDashboardData() {
    try {
        // Load total hours
        document.getElementById('total-hours').textContent = user.totalHours || '0';
        
        // Load other statistics
        await Promise.all([
            loadUpcomingEvents(),
            loadAnnouncements(),
            loadStatistics()
        ]);
    } catch (error) {
        console.error('Error loading dashboard data:', error);
    }
}

// Load statistics
async function loadStatistics() {
    try {
        const response = await fetch(`api/volunteers/${user.id}/statistics`, {
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            }
        });
        
        if (response.ok) {
            const stats = await response.json();
            document.getElementById('events-attended').textContent = stats.eventsAttended || '0';
            document.getElementById('awards-earned').textContent = stats.awardsEarned || '0';
            document.getElementById('volunteer-rank').textContent = stats.rank ? `#${stats.rank}` : '#-';
        }
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// Load upcoming events
async function loadUpcomingEvents() {
    const container = document.getElementById('upcoming-events');
    
    try {
        const response = await fetch('api/events?status=upcoming&limit=5', {
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            }
        });
        
        if (response.ok) {
            const events = await response.json();
            
            if (events.length === 0) {
                container.innerHTML = '<p class="no-data">No upcoming events</p>';
                return;
            }
            
            container.innerHTML = events.map(event => `
                <div class="event-item">
                    <h4>${event.title}</h4>
                    <p class="event-date">📅 ${formatDate(event.eventDate)}</p>
                    <p class="event-location">📍 ${event.location}</p>
                    <button onclick="registerForEvent(${event.eventId})" class="btn-small">Register</button>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading events:', error);
        container.innerHTML = '<p class="error">Failed to load events</p>';
    }
}

// Load announcements
async function loadAnnouncements() {
    const container = document.getElementById('announcements');
    
    try {
        const response = await fetch('api/announcements?limit=5', {
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            }
        });
        
        if (response.ok) {
            const announcements = await response.json();
            
            if (announcements.length === 0) {
                container.innerHTML = '<p class="no-data">No announcements</p>';
                return;
            }
            
            container.innerHTML = announcements.map(ann => `
                <div class="announcement-item">
                    <h4>${ann.title}</h4>
                    <p>${ann.content.substring(0, 100)}...</p>
                    <small>${formatDate(ann.createdDate)}</small>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading announcements:', error);
        container.innerHTML = '<p class="error">Failed to load announcements</p>';
    }
}

// Navigation functions
function showSection(sectionName) {
    // Hide all sections
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });
    
    // Remove active class from all nav items
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // Show selected section
    document.getElementById(`${sectionName}-section`).classList.add('active');
    
    // Add active class to clicked nav item
    event.target.closest('.nav-item').classList.add('active');
    
    // Update page title
    const titles = {
        'dashboard': 'Dashboard',
        'events': 'Events',
        'timesheet': 'Timesheet',
        'awards': 'Awards',
        'profile': 'Profile'
    };
    document.getElementById('page-title').textContent = titles[sectionName];
    
    // Load section-specific data
    if (sectionName === 'events') {
        loadEventsSection();
    } else if (sectionName === 'timesheet') {
        loadTimesheetSection();
    } else if (sectionName === 'awards') {
        loadAwardsSection();
    } else if (sectionName === 'profile') {
        loadProfileSection();
    }
}

// Load events section
async function loadEventsSection() {
    const container = document.getElementById('events-grid');
    container.innerHTML = '<p class="loading">Loading events...</p>';
    
    try {
        const response = await fetch('api/events', {
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            }
        });
        
        if (response.ok) {
            const events = await response.json();
            
            if (events.length === 0) {
                container.innerHTML = '<p class="no-data">No events available</p>';
                return;
            }
            
            container.innerHTML = events.map(event => `
                <div class="event-card">
                    <h3>${event.title}</h3>
                    <p>${event.description}</p>
                    <div class="event-details">
                        <p>📅 ${formatDate(event.eventDate)}</p>
                        <p>📍 ${event.location}</p>
                        <p>⏱️ ${event.duration} hours</p>
                    </div>
                    <button onclick="registerForEvent(${event.eventId})" class="btn-primary">Register</button>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading events:', error);
        container.innerHTML = '<p class="error">Failed to load events</p>';
    }
}

// Load profile section
function loadProfileSection() {
    document.getElementById('profile-firstname').value = user.firstName;
    document.getElementById('profile-lastname').value = user.lastName;
    document.getElementById('profile-email').value = user.email;
}

// Logout function
async function logout() {
    try {
        await fetch('api/logout', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            }
        });
    } catch (error) {
        console.error('Logout error:', error);
    }
    
    sessionStorage.clear();
    window.location.href = 'login.html';
}

// Utility functions
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

function registerForEvent(eventId) {
    alert('Event registration feature coming soon!');
}

function loadTimesheetSection() {
    alert('Timesheet feature coming soon!');
}

function loadAwardsSection() {
    alert('Awards feature coming soon!');
}

function exportTimesheet() {
    alert('Export feature coming soon!');
}

function showChangePassword() {
    alert('Change password feature coming soon!');
}

function filterEvents() {
    alert('Filter feature coming soon!');
}
