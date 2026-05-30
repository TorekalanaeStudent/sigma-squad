import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../api/authApi';
import serviceApi, { type ComputerStats } from '../api/serviceApi';
import ChatBot from '../components/ChatBot';
import ComputerList from '../components/ComputerList';
import UserReservations from '../components/UserReservations';
import StudentPendingReservations from '../components/StudentPendingReservations';
import SidebarNav from '../components/SidebarNav';
import CurrentSessionDisplay from '../components/CurrentSessionDisplay';
import StudentHistoryTab from '../components/StudentHistoryTab';
import NotificationToast from '../components/NotificationToast';
import { useWebSocketNotifications } from '../hooks/useWebSocketNotifications';
import styles from '../styles/studentDashboard.module.css';

const StudentDashboard: React.FC = () => {
  const navigate = useNavigate();
  const user = AuthService.getCurrentUser();
  const [stats, setStats] = useState<ComputerStats | null>(null);
  const [showChatBot, setShowChatBot] = useState(false);
  const [isChatbotMinimized, setIsChatbotMinimized] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'overview' | 'computers' | 'pending' | 'reservations' | 'history'>('overview');
  const [reservationError, setReservationError] = useState<string | null>(null);
  const { notifications, clearNotification } = useWebSocketNotifications();

  useEffect(() => {
    document.title = 'Student Dashboard - CLASS';
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.stats.getComputerStats();
      setStats(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load statistics');
      // Fallback to mock data if API fails
      setStats({
        totalComputers: 10,
        availableComputers: 7,
        reservedComputers: 2,
        inUseComputers: 1,
        outOfServiceComputers: 0,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    AuthService.logout();
    navigate('/');
  };

  const handleMinimize = () => {
    setShowChatBot(false);
    setIsChatbotMinimized(true);
  };

  const handleOpenMinimized = () => {
    setShowChatBot(true);
    setIsChatbotMinimized(false);
  };

  const handleReserve = async (computerId: number) => {
    try {
      setReservationError(null);
      await serviceApi.reservations.createReservation(computerId);
      alert('Reservation created successfully!');
      setActiveTab('reservations');
      await fetchStats();
    } catch (err: any) {
      setReservationError(err.message || 'Failed to create reservation');
    }
  };

  if (loading && !stats) {
    return <div className={styles['dashboard']}>Loading...</div>;
  }

  return (
    <div className={styles['dashboard']}>
      {/* Sidebar Navigation */}
      <SidebarNav activeTab={activeTab} onTabChange={setActiveTab} />

      {/* Header */}
      <header className={styles['header']}>
        <div className={styles['header-left']}>
          <div className={styles['logo']}>📚 CLASS</div>
          <h1>Student Dashboard</h1>
        </div>
        <div className={styles['header-right']}>
          <span className={styles['user-name']}>Welcome, {user?.name}</span>
          <button className={styles['chatbot-btn']} onClick={() => handleOpenMinimized()}>
            💬 Chat {isChatbotMinimized && '●'}
          </button>
          <button onClick={handleLogout} className={styles['logout-btn']}>
            Logout
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className={styles['main-content']}>
        {/* Overview Tab */}
        {activeTab === 'overview' && (
          <>
            {/* Welcome Section */}
            <section className={styles['welcome-section']}>
              <h2>Computer Availability Overview</h2>
              <p>View real-time statistics about available computers in the library</p>
              {error && <div style={{ color: '#fca5a5', marginTop: '0.5rem' }}>⚠️ {error}</div>}
            </section>

            {/* Current Session Display */}
            {user?.id && <CurrentSessionDisplay userId={user.id} onRefresh={fetchStats} />}

            {/* Statistics Grid */}
            {stats && (
              <section className={styles['stats-grid']}>
                <div className={styles['stat-card']} style={{ borderLeft: '4px solid #8b5cf6' }}>
                  <div className={styles['stat-icon']}>💻</div>
                  <div className={styles['stat-content']}>
                    <h3>Total Computers</h3>
                    <p className={styles['stat-number']}>{stats.totalComputers}</p>
                  </div>
                </div>

                <div className={styles['stat-card']} style={{ borderLeft: '4px solid #22c55e' }}>
                  <div className={styles['stat-icon']}>✅</div>
                  <div className={styles['stat-content']}>
                    <h3>Available</h3>
                    <p className={styles['stat-number']}>{stats.availableComputers}</p>
                  </div>
                </div>

                <div className={styles['stat-card']} style={{ borderLeft: '4px solid #f59e0b' }}>
                  <div className={styles['stat-icon']}>🔄</div>
                  <div className={styles['stat-content']}>
                    <h3>Reserved</h3>
                    <p className={styles['stat-number']}>{stats.reservedComputers}</p>
                  </div>
                </div>

                <div className={styles['stat-card']} style={{ borderLeft: '4px solid #ef4444' }}>
                  <div className={styles['stat-icon']}>🚀</div>
                  <div className={styles['stat-content']}>
                    <h3>In Use</h3>
                    <p className={styles['stat-number']}>{stats.inUseComputers}</p>
                  </div>
                </div>

                {stats.outOfServiceComputers > 0 && (
                  <div className={styles['stat-card']} style={{ borderLeft: '4px solid #94a3b8' }}>
                    <div className={styles['stat-icon']}>🔧</div>
                    <div className={styles['stat-content']}>
                      <h3>Out of Service</h3>
                      <p className={styles['stat-number']}>{stats.outOfServiceComputers}</p>
                    </div>
                  </div>
                )}
              </section>
            )}

            {/* Computer Rules Section */}
            <section className={styles['info-section']}>
              <div className={styles['info-card']}>
                <h3>📋 Reservation Rules</h3>
                <ul>
                  <li>You can reserve <strong>1 computer</strong> at a time</li>
                  <li>Reservation expires after <strong>5 minutes</strong> if not confirmed</li>
                  <li>Librarian must confirm your reservation to start a session</li>
                  <li>Sessions are tracked for library management</li>
                </ul>
              </div>

              <div className={styles['info-card']}>
                <h3>⚠️ Important Information</h3>
                <ul>
                  <li>You can only have <strong>1 active reservation</strong> per session</li>
                  <li>Double-booking is <strong>not allowed</strong></li>
                  <li>Reserved computers cannot be reserved by other students</li>
                  <li>Please cancel reservations if your plans change</li>
                </ul>
              </div>
            </section>
          </>
        )}

        {/* Computers Tab */}
        {activeTab === 'computers' && (
          <>
            {reservationError && (
              <div style={{ padding: '1rem', background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.3)', borderRadius: '8px', color: '#fca5a5', marginBottom: '1rem' }}>
                ⚠️ {reservationError}
              </div>
            )}
            <ComputerList onReserve={handleReserve} />
          </>
        )}

        {/* Pending Reservations Tab */}
        {activeTab === 'pending' && <StudentPendingReservations onRefresh={fetchStats} />}

        {/* Reservations Tab */}
        {activeTab === 'reservations' && <UserReservations onRefresh={fetchStats} />}

        {/* History Tab */}
        {activeTab === 'history' && user?.id && <StudentHistoryTab userId={user.id} />}
      </main>

      {/* ChatBot Overlay */}
      {showChatBot && <ChatBot onClose={() => setShowChatBot(false)} onMinimize={handleMinimize} />}

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

export default StudentDashboard;
