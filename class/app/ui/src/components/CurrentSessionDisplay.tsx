import React, { useState, useEffect } from 'react';
import { useSession } from '../hooks/useSession';
import styles from '../styles/currentSession.module.css';

interface CurrentSessionDisplayProps {
  userId?: number;
  onRefresh?: () => void;
}

const CurrentSessionDisplay: React.FC<CurrentSessionDisplayProps> = ({ onRefresh }) => {
  const {
    session,
    loading,
    error,
    timeRemaining,
    fetchActiveSession,
    endSessionEarly,
    requestExtension,
    formatTime,
  } = useSession();

  const [isEnding, setIsEnding] = useState(false);
  const [isExtending, setIsExtending] = useState(false);
  const [actionError, setActionError] = useState<string | null>(null);
  const [actionSuccess, setActionSuccess] = useState<string | null>(null);

  useEffect(() => {
    fetchActiveSession();
    const interval = setInterval(fetchActiveSession, 30000); // Refresh every 30 seconds
    return () => clearInterval(interval);
  }, []);

  const handleEndEarly = async () => {
    if (!session) return;
    setIsEnding(true);
    setActionError(null);
    const result = await endSessionEarly(session.id);
    if (result.success) {
      setActionSuccess('Session ended. Computer is now available.');
      setTimeout(() => setActionSuccess(null), 3000);
      if (onRefresh) onRefresh();
    } else {
      setActionError(result.error || 'Failed to end session');
    }
    setIsEnding(false);
  };

  const handleExtend = async () => {
    if (!session) return;
    setIsExtending(true);
    setActionError(null);
    const result = await requestExtension(session.id); // Request extension (requires admin approval)
    if (result.success) {
      setActionSuccess('Extension request submitted! Waiting for librarian approval.');
      setTimeout(() => setActionSuccess(null), 3000);
    } else {
      setActionError(result.error || 'Failed to request extension');
    }
    setIsExtending(false);
  };

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

  // Warning color for low time (5 minutes = 300 seconds)
  const isLowTime = timeRemaining <= 300;
  const timerColor = isLowTime ? '#dc2626' : '#8b5cf6';
  const displayTime = formatTime(timeRemaining);

  return (
    <div className={styles['container']}>
      <div className={styles['session-card']}>
        <h3>⏱️ Current Session</h3>

        <div className={styles['session-info']}>
          <div className={styles['info-item']}>
            <span className={styles['label']}>Computer:</span>
            <span className={styles['value']}>PC-{session.computerId}</span>
          </div>

          <div className={styles['info-item']}>
            <span className={styles['label']}>Time Remaining:</span>
            <span
              className={styles['timer']}
              style={{
                color: timerColor,
                fontWeight: 'bold',
                fontSize: '1.5rem',
              }}
            >
              {displayTime}
            </span>
          </div>

          {isLowTime && timeRemaining > 0 && (
            <div className={styles['warning']}>
              ⚠️ Time running out! Consider extending your session.
            </div>
          )}
        </div>

        <div className={styles['session-actions']}>
          <button
            className={styles['extend-btn']}
            onClick={handleExtend}
            disabled={isExtending || loading}
          >
            {isExtending ? '⏳ Requesting...' : '📝 Request Extension'}
          </button>

          <button
            className={styles['end-btn']}
            onClick={handleEndEarly}
            disabled={isEnding || loading}
          >
            {isEnding ? '⏳ Ending...' : '🚪 Early Out'}
          </button>
        </div>

        {actionError && (
          <div className={styles['error-message']}>{actionError}</div>
        )}

        {actionSuccess && (
          <div className={styles['success-message']}>{actionSuccess}</div>
        )}

        {timeRemaining <= 0 && (
          <div className={styles['session-ended']}>
            Session has ended. Please make a new reservation.
          </div>
        )}
      </div>
    </div>
  );
};

export default CurrentSessionDisplay;
