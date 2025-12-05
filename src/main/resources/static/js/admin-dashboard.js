// Check authentication
if (!sessionStorage.getItem('token')) {
    window.location.href = 'login.html';
}

const user = JSON.parse(sessionStorage.getItem('user'));
if (user.userType !== 'admin') {
    window.location.href = 'volunteer-dashboard.html';
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    loadUserInfo();
    loadDashboardStats();
});

// Load user information
function loadUserInfo() {
    const userName = document.getElementById('user-name');
    const userAvatar = document.getElementById('user-avatar');
    
    userName.textContent = `${user.firstName} ${user.lastName}`;
    userAvatar.textContent = user.firstName.charAt(0).toUpperCase();
}

// Load dashboard statistics
async function loadDashboardStats() {
    try {
        const response = await fetch('api/admin/statistics', {
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            }
        });
        
        if (response.ok) {
            const stats = await response.json();
            document.getElementById('total-volunteers').textContent = stats.totalVolunteers || '0';
            document.getElementById('total-events').textContent = stats.totalEvents || '0';
            document.getElementById('total-hours').textContent = stats.totalHours || '0';
            document.getElementById('active-volunteers').textContent = stats.activeVolunteers || '0';
        }
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
    
    loadRecentActivities();
    loadUpcomingEvents();
}

// Load recent activities
async function loadRecentActivities() {
    const container = document.getElementById('recent-activities');
    container.innerHTML = '<div class="activity-item">✅ System initialized successfully</div>';
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
                    <p>📅 ${formatDate(event.eventDate)}</p>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading events:', error);
        container.innerHTML = '<p class="error">Failed to load events</p>';
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
        'dashboard': 'Admin Dashboard',
        'volunteers': 'Manage Volunteers',
        'events': 'Manage Events',
        'attendance': 'Attendance Management',
        'announcements': 'Manage Announcements',
        'awards': 'Awards Management',
        'reports': 'Reports & Analytics'
    };
    document.getElementById('page-title').textContent = titles[sectionName];
    
    // Load section-specific data
    if (sectionName === 'volunteers') {
        loadVolunteersSection();
    } else if (sectionName === 'events') {
        loadEventsSection();
    } else if (sectionName === 'announcements') {
        loadAnnouncementsSection();
    }
}

// Load volunteers section
async function loadVolunteersSection() {
    const tbody = document.querySelector('#volunteers-table tbody');
    tbody.innerHTML = '<tr><td colspan="7" class="loading">Loading volunteers...</td></tr>';
    
    try {
        const response = await fetch('api/volunteers', {
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            }
        });
        
        if (response.ok) {
            const volunteers = await response.json();
            
            if (volunteers.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="no-data">No volunteers found</td></tr>';
                return;
            }
            
            tbody.innerHTML = volunteers.map(v => `
                <tr>
                    <td>${v.volunteerId}</td>
                    <td>${v.firstName} ${v.lastName}</td>
                    <td>${v.email}</td>
                    <td>${v.phone || 'N/A'}</td>
                    <td>${v.totalHours || 0}</td>
                    <td><span class="status-badge ${v.isActive ? 'active' : 'inactive'}">${v.isActive ? 'Active' : 'Inactive'}</span></td>
                    <td>
                        <button onclick="viewVolunteer(${v.volunteerId})" class="btn-small">View</button>
                        <button onclick="editVolunteer(${v.volunteerId})" class="btn-small">Edit</button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading volunteers:', error);
        tbody.innerHTML = '<tr><td colspan="7" class="error">Failed to load volunteers</td></tr>';
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
                container.innerHTML = '<p class="no-data">No events found</p>';
                return;
            }
            
            container.innerHTML = events.map(event => `
                <div class="event-card">
                    <h3>${event.title}</h3>
                    <p>${event.description}</p>
                    <div class="event-details">
                        <p>📅 ${formatDate(event.eventDate)}</p>
                        <p>📍 ${event.location}</p>
                        <p>👥 ${event.registeredCount || 0}/${event.capacity}</p>
                    </div>
                    <div class="card-actions">
                        <button onclick="viewEvent(${event.eventId})" class="btn-primary">View</button>
                        <button onclick="editEvent(${event.eventId})" class="btn-secondary">Edit</button>
                    </div>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading events:', error);
        container.innerHTML = '<p class="error">Failed to load events</p>';
    }
}

// Load announcements section
async function loadAnnouncementsSection() {
    const container = document.getElementById('announcements-list');
    container.innerHTML = '<p class="loading">Loading announcements...</p>';
    
    try {
        const response = await fetch('api/announcements', {
            headers: {
                'Authorization': `Bearer ${sessionStorage.getItem('token')}`
            }
        });
        
        if (response.ok) {
            const announcements = await response.json();
            
            if (announcements.length === 0) {
                container.innerHTML = '<p class="no-data">No announcements found</p>';
                return;
            }
            
            container.innerHTML = announcements.map(ann => `
                <div class="announcement-card">
                    <h3>${ann.title}</h3>
                    <p>${ann.content}</p>
                    <div class="announcement-meta">
                        <span>Priority: ${ann.priority}</span>
                        <span>Posted: ${formatDate(ann.createdDate)}</span>
                        <button onclick="editAnnouncement(${ann.announcementId})" class="btn-small">Edit</button>
                        <button onclick="deleteAnnouncement(${ann.announcementId})" class="btn-small btn-danger">Delete</button>
                    </div>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading announcements:', error);
        container.innerHTML = '<p class="error">Failed to load announcements</p>';
    }
}

// Modal and action functions
function showAddVolunteerModal() {
    alert('Add Volunteer feature - Coming soon!');
}

function showAddEventModal() {
    alert('Create Event feature - Coming soon!');
}

function showAddAnnouncementModal() {
    alert('Create Announcement feature - Coming soon!');
}

function viewVolunteer(id) {
    alert(`View volunteer ${id} - Coming soon!`);
}

function editVolunteer(id) {
    alert(`Edit volunteer ${id} - Coming soon!`);
}

function viewEvent(id) {
    alert(`View event ${id} - Coming soon!`);
}

function editEvent(id) {
    alert(`Edit event ${id} - Coming soon!`);
}

function editAnnouncement(id) {
    alert(`Edit announcement ${id} - Coming soon!`);
}

function deleteAnnouncement(id) {
    if (confirm('Are you sure you want to delete this announcement?')) {
        alert(`Delete announcement ${id} - Coming soon!`);
    }
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
