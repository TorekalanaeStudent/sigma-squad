import React, { useState, useEffect } from 'react';
import serviceApi, { type Session } from '../api/serviceApi';
import AdminQuickOverviewCard, { type QuickOverviewCardProps } from './AdminQuickOverviewCard';
import styles from '../styles/adminOverviewSection.module.css';

interface AdminActiveSessionsOverviewProps {
  onRefresh?: () => void;
}

const AdminActiveSessionsOverview: React.FC<AdminActiveSessionsOverviewProps> = ({
  onRefresh,
}) => {
  const [sessions, setSessions] = useState<Session[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [timers, setTimers] = useState<{ [key: number]: string }>({});
  const [userNameCache, setUserNameCache] = useState<{ [key: number]: string }>({});

  const buildUserCache = async () => {
    try {
      const reservations = await serviceApi.reservations.getAllReservations();
      const cache: { [key: number]: string } = {};
      reservations.forEach((res) => {
        if (res.userName) {
          cache[res.userId] = res.userName;
        }
      });
      setUserNameCache(cache);
    } catch (err: any) {
      console.error('Failed to build user cache:', err);
    }
  };

  useEffect(() => {
    buildUserCache();
    fetchActiveSessions();
  }, []);

  useEffect(() => {
    const timerInterval = setInterval(() => {
      const newTimers: { [key: number]: string } = {};
      sessions.forEach((session) => {
        if (session.endTime) {
          const endTimeMs = new Date(session.endTime).getTime();
          const nowMs = Date.now();
          const remainingMs = Math.max(0, endTimeMs - nowMs);
          const seconds = Math.ceil(remainingMs / 1000);
          const mins = Math.floor(seconds / 60);
          const secs = seconds % 60;
          newTimers[session.id] = `${mins}:${secs.toString().padStart(2, '0')}`;
        }
      });
      setTimers(newTimers);
    }, 1000);

    return () => clearInterval(timerInterval);
  }, [sessions]);

  const fetchActiveSessions = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.sessions.getAllActiveSessions();
      setSessions(data.slice(0, 5));
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load active sessions');
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveUser = async (sessionId: number) => {
    try {
      await serviceApi.sessions.endSession(sessionId);
      alert('Session ended');
      await fetchActiveSessions();
      if (onRefresh) onRefresh();
    } catch (err: any) {
      alert(err.message || 'Failed to end session');
    }
  };

  if (loading) return <div className={styles['section']}>Loading active sessions...</div>;
  if (error) return <div className={styles['section']} style={{ color: '#fca5a5' }}>⚠️ {error}</div>;
  if (sessions.length === 0)
    return <div className={styles['section']}>No active sessions</div>;

  return (
    <section className={styles['section']}>
      <h3 className={styles['section-title']}>🚀 Active Sessions</h3>
      <p className={styles['section-subtitle']}>Current user sessions</p>

      <div className={styles['cards-grid']}>
        {sessions.map((session) => {
          const cardProps: QuickOverviewCardProps = {
            id: session.id,
            title: `Session - ${userNameCache[session.userId] || session.userName || `User ${session.userId}`}`,
            details: [
              { label: 'Student', value: userNameCache[session.userId] || session.userName || `User ${session.userId}` },
              { label: 'Computer', value: session.computerNumber || `PC ${session.computerId}` },
              { label: 'Time Remaining', value: timers[session.id] || '--:--' },
            ],
            actions: [
              {
                label: 'Remove User',
                icon: '❌',
                color: 'danger',
                onClick: () => handleRemoveUser(session.id),
              },
            ],
          };

          return <AdminQuickOverviewCard key={session.id} {...cardProps} />;
        })}
      </div>
    </section>
  );
};

export default AdminActiveSessionsOverview;
