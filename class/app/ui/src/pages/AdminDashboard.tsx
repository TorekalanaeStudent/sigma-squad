import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../api/authApi';
import AdminSidebarNav from '../components/AdminSidebarNav';
import PendingReservations from '../components/PendingReservations';
import AdminReservationHistory from '../components/AdminReservationHistory';
import PendingExtensions from '../components/PendingExtensions';
import ActiveSessionsTab from '../components/ActiveSessionsTab';
import AdminPendingOverview from '../components/AdminPendingOverview';
import AdminExtensionsOverview from '../components/AdminExtensionsOverview';
import AdminActiveSessionsOverview from '../components/AdminActiveSessionsOverview';
import AuditLog from '../components/AuditLog';
import NotificationToast from '../components/NotificationToast';
import { useWebSocketNotifications } from '../hooks/useWebSocketNotifications';
import styles from '../styles/adminDashboard.module.css';

const AdminDashboard: React.FC = () => {
  const navigate = useNavigate();
  const user = AuthService.getCurrentUser();
  const [activeTab, setActiveTab] = useState<'dashboard' | 'pending' | 'sessions' | 'extensions' | 'history' | 'audit'>('dashboard');
  const [highlightedPendingId, setHighlightedPendingId] = useState<number | null>(null);
  const [highlightedExtensionId, setHighlightedExtensionId] = useState<number | null>(null);
  const { notifications, clearNotification } = useWebSocketNotifications();

  const handleLogout = () => {
    AuthService.logout();
    navigate('/');
  };

  const handleNavigateToPending = (reservationId: number) => {
    setHighlightedPendingId(reservationId);
    setActiveTab('pending');
    setTimeout(() => {
      const element = document.getElementById(`pending-${reservationId}`);
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }, 100);
  };

  const handleNavigateToExtensions = (extensionId: number) => {
    setHighlightedExtensionId(extensionId);
    setActiveTab('extensions');
    setTimeout(() => {
      const element = document.getElementById(`extension-${extensionId}`);
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }, 100);
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

            {/* Quick Overview Sections */}
            <AdminPendingOverview onNavigateToPending={handleNavigateToPending} onRefresh={() => {}} />
            <AdminActiveSessionsOverview onRefresh={() => {}} />
            <AdminExtensionsOverview onNavigateToExtensions={handleNavigateToExtensions} onRefresh={() => {}} />
          </>
        )}

        {/* Pending Reservations Tab */}
        {activeTab === 'pending' && <PendingReservations highlightedId={highlightedPendingId} />}

        {/* Active Sessions Tab */}
        {activeTab === 'sessions' && <ActiveSessionsTab />}

        {/* Extensions Tab */}
        {activeTab === 'extensions' && <PendingExtensions highlightedId={highlightedExtensionId} />}

        {/* Reservation History Tab */}
        {activeTab === 'history' && <AdminReservationHistory />}

        {/* Audit Log Tab */}
        {activeTab === 'audit' && <AuditLog />}
      </main>

      {/* Notification Toasts */}
      <div className={styles['notification-container']}>
        {notifications.map((notification, index) => (
          <NotificationToast
            key={`${notification.createdAt}-${index}`}
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
