import React, { useState, useEffect } from 'react';
import serviceApi, { type Session } from '../api/serviceApi';
import ExtensionRequestButton from './ExtensionRequestButton';
import styles from '../styles/currentSession.module.css';

interface CurrentSessionDisplayProps {
  userId: number;
  onRefresh?: () => void;
}

const CurrentSessionDisplay: React.FC<CurrentSessionDisplayProps> = ({ userId, onRefresh }) => {
  const [session, setSession] = useState<Session | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [timeRemaining, setTimeRemaining] = useState<string>('');

  useEffect(() => {
    fetchActiveSession();
  }, [userId]);

  const fetchActiveSession = async () => {
    try {
      setLoading(true);
      const activeSession = await serviceApi.sessions.getUserActiveSession(userId);
      setSession(activeSession);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load active session');
      setSession(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!session || !session.endTime) return;

    const updateTimer = () => {
      const endTime = new Date(session.endTime as string).getTime();
      const now = Date.now();
      const diff = endTime - now;

      if (diff <= 0) {
        setTimeRemaining('Session ended');
        return;
      }

      const hours = Math.floor(diff / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((diff % (1000 * 60)) / 1000);

      setTimeRemaining(`${hours}h ${minutes}m ${seconds}s`);
    };

    updateTimer();
    const interval = setInterval(updateTimer, 1000);
    return () => clearInterval(interval);
  }, [session]);

  if (loading) {
    return (
      <div className={styles['container']}>
        <p>Loading session information...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className={styles['container']}>
        <p style={{ color: '#fca5a5' }}>⚠️ {error}</p>
      </div>
    );
  }

  if (!session) {
    return (
      <div className={styles['container']}>
        <div className={styles['no-session']}>
          <p>No active session</p>
          <p style={{ fontSize: '0.9rem', color: '#9ca3af' }}>
            Start a new session by reserving a computer
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className={styles['container']}>
      <div className={styles['session-card']}>
        <h3>💻 Current Session</h3>
        
        <div className={styles['session-info']}>
          <div className={styles['info-row']}>
            <span className={styles['label']}>Computer:</span>
            <span className={styles['value']}>PC-{session.computerId}</span>
          </div>

          <div className={styles['info-row']}>
            <span className={styles['label']}>⏱️ Time Remaining:</span>
            <span className={`${styles['value']} ${styles['timer']}`}>{timeRemaining}</span>
          </div>

          <div className={styles['info-row']}>
            <span className={styles['label']}>Status:</span>
            <span className={`${styles['value']} ${styles['status-active']}`}>
              {session.status === 'ACTIVE' ? '🟢 Active' : '⚪ Ended'}
            </span>
          </div>
        </div>

        {session.status === 'ACTIVE' && (
          <div className={styles['extension-section']}>
            <ExtensionRequestButton 
              sessionId={session.id} 
              onSuccess={() => {
                if (onRefresh) onRefresh();
              }}
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default CurrentSessionDisplay;
