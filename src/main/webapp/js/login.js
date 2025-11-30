document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('error-message');

    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        // Clear previous error messages
        hideError();

        // Get form data
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;
        const rememberMe = document.getElementById('rememberMe').checked;

        // Client-side validation
        if (!username || !password) {
            showError('Please enter both username and password');
            return;
        }

        // Disable submit button to prevent double submission
        const submitBtn = loginForm.querySelector('.login-btn');
        submitBtn.disabled = true;
        submitBtn.textContent = 'Logging in...';

        try {
            const response = await fetch('api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: username,
                    password: password,
                    rememberMe: rememberMe
                })
            });

            const data = await response.json();

            if (response.ok) {
                // Store user data in session storage
                sessionStorage.setItem('user', JSON.stringify(data.user));
                sessionStorage.setItem('token', data.token);

                // Redirect based on user role
                if (data.user.role === 'admin' || data.user.role === 'super_admin') {
                    window.location.href = 'admin-dashboard.html';
                } else {
                    window.location.href = 'volunteer-dashboard.html';
                }
            } else {
                showError(data.message || 'Invalid username or password');
                submitBtn.disabled = false;
                submitBtn.textContent = 'Login';
            }
        } catch (error) {
            console.error('Login error:', error);
            showError('An error occurred. Please try again later.');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Login';
        }
    });
});

function togglePassword() {
    const passwordInput = document.getElementById('password');
    const toggleIcon = document.querySelector('.toggle-password');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleIcon.textContent = '🙈';
    } else {
        passwordInput.type = 'password';
        toggleIcon.textContent = '👁️';
    }
}

function showError(message) {
    const errorDiv = document.getElementById('error-message');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        hideError();
    }, 5000);
}

function hideError() {
    const errorDiv = document.getElementById('error-message');
    errorDiv.style.display = 'none';
    errorDiv.textContent = '';
}

// Check if user is already logged in
if (sessionStorage.getItem('token')) {
    const user = JSON.parse(sessionStorage.getItem('user'));
    if (user.role === 'admin' || user.role === 'super_admin') {
        window.location.href = 'admin-dashboard.html';
    } else {
        window.location.href = 'volunteer-dashboard.html';
    }
}
