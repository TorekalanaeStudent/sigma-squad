import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../api/authApi';
import AdminSidebarNav from '../components/AdminSidebarNav';
import PendingReservations from '../components/PendingReservations';
import PendingExtensions from '../components/PendingExtensions';
import ActiveSessionsTab from '../components/ActiveSessionsTab';
import NotificationToast from '../components/NotificationToast';
import { useWebSocketNotifications } from '../hooks/useWebSocketNotifications';
import styles from '../styles/adminDashboard.module.css';

const AdminDashboard: React.FC = () => {
  const navigate = useNavigate();
  const user = AuthService.getCurrentUser();
  const [activeTab, setActiveTab] = useState<'dashboard' | 'pending' | 'sessions' | 'extensions'>('dashboard');
  const { notifications, isConnected, clearNotification } = useWebSocketNotifications();

  const handleLogout = () => {
    AuthService.logout();
    navigate('/');
  };

  useEffect(() => {
    document.title = 'Admin Dashboard - CLASS';
  }, []);

  return (
    <div className={styles['dashboard']}>
      {/* Sidebar Navigation */}
      <AdminSidebarNav activeTab={activeTab} onTabChange={setActiveTab} />

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
        {/* Dashboard Tab */}
        {activeTab === 'dashboard' && (
          <>
            <section className={styles['welcome-section']}>
              <h2>Admin Control Panel</h2>
              <p>Manage computers, confirm reservations, and monitor sessions</p>
            </section>

            {/* Admin Features */}
            <section className={styles['stats-grid']}>
              <div className={styles['stat-card']} style={{ borderLeft: '4px solid #dc2626' }}>
                <div className={styles['stat-icon']}>📋</div>
                <div className={styles['stat-content']}>
                  <h3>Pending Requests</h3>
                  <p className={styles['stat-number']}>View & Accept</p>
                </div>
              </div>

              <div className={styles['stat-card']} style={{ borderLeft: '4px solid #dc2626' }}>
                <div className={styles['stat-icon']}>🚀</div>
                <div className={styles['stat-content']}>
                  <h3>Active Sessions</h3>
                  <p className={styles['stat-number']}>Monitor</p>
                </div>
              </div>

              <div className={styles['stat-card']} style={{ borderLeft: '4px solid #dc2626' }}>
                <div className={styles['stat-icon']}>⏱️</div>
                <div className={styles['stat-content']}>
                  <h3>Extensions</h3>
                  <p className={styles['stat-number']}>Approve/Reject</p>
                </div>
              </div>
            </section>
          </>
        )}

        {/* Pending Reservations Tab */}
        {activeTab === 'pending' && <PendingReservations />}

        {/* Active Sessions Tab */}
        {activeTab === 'sessions' && <ActiveSessionsTab />}

        {/* Extensions Tab */}
        {activeTab === 'extensions' && <PendingExtensions />}
      </main>

      {/* Notification Toasts */}
      <div className={styles['notification-container']}>
        {notifications.map((notification, index) => (
          <NotificationToast
            key={`${notification.timestamp}-${index}`}
            notification={notification}
            index={index}
            onClose={() => clearNotification(index)}
          />
        ))}
      </div>

      {/* Footer */}
      <footer className={styles['footer']}>
        <p>© 2026 Sigma Squad - Computer Library Access System. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default AdminDashboard;
