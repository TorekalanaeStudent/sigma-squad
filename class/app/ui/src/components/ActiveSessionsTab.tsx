import React, { useState, useEffect } from 'react';
import serviceApi, { type Session } from '../api/serviceApi';
import styles from '../styles/activeSessions.module.css';

const ActiveSessionsTab: React.FC = () => {
  const [sessions, setSessions] = useState<Session[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [timers, setTimers] = useState<{ [key: number]: number }>({});
  const [removingSessionId, setRemovingSessionId] = useState<number | null>(null);

  // Auto-refresh every 30 seconds
  useEffect(() => {
    fetchActiveSessions();
    const interval = setInterval(fetchActiveSessions, 30000);
    return () => clearInterval(interval);
  }, []);

  // Update timers every second
  useEffect(() => {
    const timerInterval = setInterval(() => {
      setTimers((prev) => {
        const updated = { ...prev };
        Object.keys(updated).forEach((key) => {
          const sessionId = parseInt(key);
          updated[sessionId] = Math.max(0, updated[sessionId] - 1);
        });
        return updated;
      });
    }, 1000);
    return () => clearInterval(timerInterval);
  }, []);

  const fetchActiveSessions = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.sessions.getAllActiveSessions();
      setSessions(data);
      
      // Initialize timers from server data
      const newTimers: { [key: number]: number } = {};
      data.forEach((session) => {
        if (session.minutesRemaining !== undefined) {
          newTimers[session.id] = session.minutesRemaining * 60;
        }
      });
      setTimers(newTimers);
      
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch active sessions');
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveUser = async (sessionId: number) => {
    if (!window.confirm('Are you sure you want to remove this user from their session?')) {
      return;
    }

    try {
      setRemovingSessionId(sessionId);
      await serviceApi.sessions.removeUserFromSession(sessionId);
      
      setSuccessMessage('User removed from session. Computer is now available.');
      
      // Refresh sessions list
      await fetchActiveSessions();
      
      // Auto-dismiss message
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err: any) {
      setError(err.message || 'Failed to remove user');
    } finally {
      setRemovingSessionId(null);
    }
  };

  const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const getTimeColor = (sessionId: number): string => {
    const timeRemaining = timers[sessionId] || 0;
    const minutes = timeRemaining / 60;
    return minutes <= 5 ? '#dc2626' : '#22c55e'; // Red if <= 5 mins, green otherwise
  };

  if (loading) {
    return <div className={styles['container']}>Loading active sessions...</div>;
  }

  return (
    <div className={styles['container']}>
      <section className={styles['header-section']}>
        <h2>Active Sessions Monitoring</h2>
        <p>Real-time view of all active student sessions. Click "Remove User" to forcefully end a session.</p>
        <button className={styles['refresh-btn']} onClick={fetchActiveSessions}>
          🔄 Refresh Now
        </button>
      </section>

      {error && <div className={styles['error-message']}>❌ {error}</div>}
      {successMessage && <div className={styles['success-message']}>✅ {successMessage}</div>}

      {sessions.length === 0 ? (
        <div className={styles['empty-state']}>
          <p className={styles['empty-icon']}>💻</p>
          <p>No active sessions at the moment.</p>
          <p className={styles['empty-subtext']}>All computers are available!</p>
        </div>
      ) : (
        <div className={styles['sessions-grid']}>
          {sessions.map((session) => (
            <div key={session.id} className={styles['session-card']}>
              <div className={styles['card-header']}>
                <span className={styles['session-id']}>Session #{session.id}</span>
                <span className={styles['status-badge']}>ACTIVE</span>
              </div>

              <div className={styles['session-details']}>
                <div className={styles['detail-row']}>
                  <span className={styles['detail-label']}>Student:</span>
                  <span className={styles['detail-value']}>{session.userName || `User ${session.userId}`}</span>
                </div>

                <div className={styles['detail-row']}>
                  <span className={styles['detail-label']}>Computer:</span>
                  <span className={styles['detail-value']}>{session.computerNumber || `PC-${session.computerId}`}</span>
                </div>

                <div className={styles['detail-row']}>
                  <span className={styles['detail-label']}>Started:</span>
                  <span className={styles['detail-value']}>
                    {new Date(session.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </span>
                </div>

                <div className={styles['detail-row']}>
                  <span className={styles['detail-label']}>Time Remaining:</span>
                  <span
                    className={styles['detail-value']}
                    style={{
                      color: getTimeColor(session.id),
                      fontWeight: '700',
                      fontFamily: 'monospace',
                      fontSize: '1.1rem',
                    }}
                  >
                    {formatTime(timers[session.id] || 0)}
                  </span>
                </div>

                {timers[session.id] !== undefined && timers[session.id] <= 300 && (
                  <div className={styles['warning-message']}>
                    ⚠️ Less than 5 minutes remaining
                  </div>
                )}
              </div>

              <button
                className={styles['remove-btn']}
                onClick={() => handleRemoveUser(session.id)}
                disabled={removingSessionId === session.id}
              >
                {removingSessionId === session.id ? '⏳ Removing...' : '❌ Remove User'}
              </button>
            </div>
          ))}
        </div>
      )}

      <section className={styles['stats-footer']}>
        <div className={styles['stat']}>
          <p className={styles['stat-label']}>Total Active Sessions:</p>
          <p className={styles['stat-value']}>{sessions.length}</p>
        </div>
        <div className={styles['stat']}>
          <p className={styles['stat-label']}>Low Time Warning:</p>
          <p className={styles['stat-value']}>
            {sessions.filter((s) => timers[s.id] !== undefined && timers[s.id] <= 300).length}
          </p>
        </div>
      </section>
    </div>
  );
};

export default ActiveSessionsTab;
