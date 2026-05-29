import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../api/authApi';
import styles from '../styles/adminDashboard.module.css';

const AdminDashboard: React.FC = () => {
  const navigate = useNavigate();
  const user = AuthService.getCurrentUser();

  const handleLogout = () => {
    AuthService.logout();
    navigate('/');
  };

  useEffect(() => {
    document.title = 'Admin Dashboard - CLASS';
  }, []);

  return (
    <div className={styles['dashboard']}>
      {/* Header */}
      <header className={styles['header']}>
        <div className={styles['header-left']}>
          <div className={styles['logo']}>📚 CLASS</div>
          <h1>Admin Dashboard</h1>
        </div>
        <div className={styles['header-right']}>
          <span className={styles['user-name']}>Librarian: {user?.name}</span>
          <button onClick={handleLogout} className={styles['logout-btn']}>
            Logout
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className={styles['main-content']}>
        <section className={styles['welcome-section']}>
          <h2>Admin Control Panel</h2>
          <p>Manage computers, confirm reservations, and monitor sessions</p>
        </section>

        {/* Admin Features */}
        <section className={styles['features-grid']}>
          <div className={styles['feature-card']}>
            <div className={styles['feature-icon']}>💻</div>
            <h3>Manage Computers</h3>
            <p>Add, edit, or remove computers from the system</p>
            <button className={styles['feature-btn']}>Manage</button>
          </div>

          <div className={styles['feature-card']}>
            <div className={styles['feature-icon']}>📋</div>
            <h3>Reservations</h3>
            <p>View and confirm pending student reservations</p>
            <button className={styles['feature-btn']}>View</button>
          </div>

          <div className={styles['feature-card']}>
            <div className={styles['feature-icon']}>⏱️</div>
            <h3>Active Sessions</h3>
            <p>Monitor and manage active user sessions</p>
            <button className={styles['feature-btn']}>Monitor</button>
          </div>

          <div className={styles['feature-card']}>
            <div className={styles['feature-icon']}>👥</div>
            <h3>User Management</h3>
            <p>View and manage student and admin accounts</p>
            <button className={styles['feature-btn']}>Manage</button>
          </div>

          <div className={styles['feature-card']}>
            <div className={styles['feature-icon']}>📊</div>
            <h3>Reports</h3>
            <p>Generate usage reports and analytics</p>
            <button className={styles['feature-btn']}>Generate</button>
          </div>

          <div className={styles['feature-card']}>
            <div className={styles['feature-icon']}>⚙️</div>
            <h3>System Settings</h3>
            <p>Configure system parameters and policies</p>
            <button className={styles['feature-btn']}>Configure</button>
          </div>
        </section>

        {/* Admin Info */}
        <section className={styles['info-section']}>
          <div className={styles['info-box']}>
            <h3>🔐 Admin Privileges</h3>
            <p>
              This is the <strong>Admin Dashboard</strong>. Only librarians with admin access can view this page.
              Students cannot access this area. Use these tools to effectively manage the computer library.
            </p>
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className={styles['footer']}>
        <p>© 2026 Sigma Squad - Computer Library Access System. Admin Panel.</p>
      </footer>
    </div>
  );
};

export default AdminDashboard;
