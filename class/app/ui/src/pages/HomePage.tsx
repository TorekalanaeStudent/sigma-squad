import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import LoginForm from '../components/LoginForm';
import RegisterForm from '../components/RegisterForm';
import AuthService from '../api/authApi';
import styles from '../styles/homePage.module.css';

type FormMode = 'login' | 'register';

const HomePage: React.FC = () => {
  const [formMode, setFormMode] = useState<FormMode>('login');
  const navigate = useNavigate();

  // Check if already logged in
  useEffect(() => {
    if (AuthService.isAuthenticated()) {
      navigate('/dashboard');
    }
  }, [navigate]);

  const handleLoginSuccess = () => {
    navigate('/dashboard');
  };

  const handleRegisterSuccess = () => {
    navigate('/dashboard');
  };

  return (
    <div className={styles['homepage']}>
      {/* Left Side - Branding & Features */}
      <div className={styles['left-section']}>
        <div className={styles['brand-container']}>
          <div className={styles['tagline']}>
            <h2><span className={styles['welcome-text']}>Welcome to </span><span className={styles['class-text']}>CLASS</span></h2>
            <p>Computer Library Access System</p>
          </div>

          <div className={styles['description']}>
            <p>
              CLASS is a modern computer resource management system designed for educational institutions. 
              Reserve computers, track usage, and manage library resources effortlessly.
            </p>
          </div>

          {/* Features Grid */}
          <div className={styles['features']}>
            <div className={styles['feature']}>
              <div className={styles['feature-icon']}>📋</div>
              <h3>Easy Reservations</h3>
              <p>Reserve computers in seconds with our intuitive interface</p>
            </div>

            <div className={styles['feature']}>
              <div className={styles['feature-icon']}>⚡</div>
              <h3>Real-time Status</h3>
              <p>See live availability of all computers in your library</p>
            </div>

            <div className={styles['feature']}>
              <div className={styles['feature-icon']}>🔐</div>
              <h3>Secure Access</h3>
              <p>JWT-based authentication ensures your data is protected</p>
            </div>

            <div className={styles['feature']}>
              <div className={styles['feature-icon']}>👥</div>
              <h3>Multi-role System</h3>
              <p>Separate student and librarian interfaces for optimal workflow</p>
            </div>

            <div className={styles['feature']}>
              <div className={styles['feature-icon']}>🎯</div>
              <h3>Smart Scheduling</h3>
              <p>5-minute reservation slots prevent resource conflicts</p>
            </div>

            <div className={styles['feature']}>
              <div className={styles['feature-icon']}>📊</div>
              <h3>Session Tracking</h3>
              <p>Librarians can monitor and manage active user sessions</p>
            </div>
          </div>

          {/* Mission Statement */}
          <div className={styles['mission']}>
            <h3>Our Mission</h3>
            <p>
              To streamline computer resource allocation in educational institutions, 
              ensuring fair access, reducing idle time, and enhancing user experience through 
              modern technology.
            </p>
          </div>
        </div>
      </div>

      {/* Right Side - Auth Forms */}
      <div className={styles['right-section']}>
        <div className={styles['form-container']}>
          {formMode === 'login' ? (
            <LoginForm
              onSwitchToRegister={() => setFormMode('register')}
              onLoginSuccess={handleLoginSuccess}
            />
          ) : (
            <RegisterForm
              onSwitchToLogin={() => setFormMode('login')}
              onRegisterSuccess={handleRegisterSuccess}
            />
          )}
        </div>

        {/* Footer */}
        <footer className={styles['footer']}>
          <p>© 2026 Sigma Squad. All rights reserved.</p>
        </footer>
      </div>
    </div>
  );
};

export default HomePage;
