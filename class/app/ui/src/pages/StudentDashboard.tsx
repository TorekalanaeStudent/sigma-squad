import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../api/authApi';
import serviceApi, { type ComputerStats } from '../api/serviceApi';
import ChatBot from '../components/ChatBot';
import styles from '../styles/studentDashboard.module.css';

const StudentDashboard: React.FC = () => {
  const navigate = useNavigate();
  const user = AuthService.getCurrentUser();
  const [stats, setStats] = useState<ComputerStats | null>(null);
  const [showChatBot, setShowChatBot] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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
        totalComputers: 50,
        availableComputers: 35,
        reservedComputers: 8,
        inUseComputers: 7,
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

  if (loading && !stats) {
    return <div className={styles['dashboard']}>Loading...</div>;
  }

  return (
    <div className={styles['dashboard']}>
      {/* Header */}
      <header className={styles['header']}>
        <div className={styles['header-left']}>
          <div className={styles['logo']}>📚 CLASS</div>
          <h1>Student Dashboard</h1>
        </div>
        <div className={styles['header-right']}>
          <span className={styles['user-name']}>Welcome, {user?.name}</span>
          <button className={styles['chatbot-btn']} onClick={() => setShowChatBot(!showChatBot)}>
            💬 Chat
          </button>
          <button onClick={handleLogout} className={styles['logout-btn']}>
            Logout
          </button>
        </div>
      </header>

      {/* Main Content */}
      <main className={styles['main-content']}>
        {/* Welcome Section */}
        <section className={styles['welcome-section']}>
          <h2>Computer Availability Overview</h2>
          <p>View real-time statistics about available computers in the library</p>
          {error && <div style={{ color: '#fca5a5', marginTop: '0.5rem' }}>⚠️ {error}</div>}
        </section>

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

        {/* Quick Actions */}
        <section className={styles['actions-section']}>
          <button className={styles['action-btn']} style={{ background: '#8b5cf6' }}>
            📖 View Computers
          </button>
          <button className={styles['action-btn']} style={{ background: '#6366f1' }}>
            📝 My Reservations
          </button>
          <button className={styles['action-btn']} style={{ background: '#3b82f6' }}>
            ⏱️ Active Sessions
          </button>
        </section>
      </main>

      {/* ChatBot Overlay */}
      {showChatBot && <ChatBot onClose={() => setShowChatBot(false)} />}

      {/* Footer */}
      <footer className={styles['footer']}>
        <p>© 2026 Sigma Squad - Computer Library Access System. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default StudentDashboard;
