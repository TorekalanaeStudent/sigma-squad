import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../api/authApi';
import AdminSidebarNav from '../components/AdminSidebarNav';
import PendingReservations from '../components/PendingReservations';
import PendingExtensions from '../components/PendingExtensions';
import styles from '../styles/studentDashboard.module.css';

const AdminDashboard: React.FC = () => {
  const navigate = useNavigate();
  const user = AuthService.getCurrentUser();
  const [activeTab, setActiveTab] = useState<'dashboard' | 'pending' | 'sessions' | 'extensions'>('dashboard');

  const handleLogout = () => {
    AuthService.logout();
    navigate('/');
  };

  useEffect(() => {
    document.title = 'Admin Dashboard - CLASS';
  }, []);

  const handleRefresh = () => {
    // Refresh logic if needed
  };

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
              <div className={styles['stat-card']} style={{ borderLeft: '4px solid #8b5cf6' }}>
                <div className={styles['stat-icon']}>📋</div>
                <div className={styles['stat-content']}>
                  <h3>Pending Reservations</h3>
                  <p className={styles['stat-number']}>View & Accept</p>
                </div>
              </div>

              <div className={styles['stat-card']} style={{ borderLeft: '4px solid #22c55e' }}>
                <div className={styles['stat-icon']}>🚀</div>
                <div className={styles['stat-content']}>
                  <h3>Active Sessions</h3>
                  <p className={styles['stat-number']}>Monitor</p>
                </div>
              </div>

              <div className={styles['stat-card']} style={{ borderLeft: '4px solid #f59e0b' }}>
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
        {activeTab === 'sessions' && <div style={{ padding: '2rem', color: '#cbd5e1' }}>🚀 Active Sessions Tab - Coming Soon</div>}

        {/* Extensions Tab */}
        {activeTab === 'extensions' && <PendingExtensions />}
      </main>

      {/* Footer */}
      <footer className={styles['footer']}>
        <p>© 2026 Sigma Squad - Computer Library Access System. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default AdminDashboard;
