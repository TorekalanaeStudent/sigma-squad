import React, { useState, useEffect } from 'react';
import serviceApi, { type AuditLogDTO } from '../api/serviceApi';
import styles from '../styles/studentHistoryTab.module.css';

interface StudentHistoryTabProps {
  userId: number;
}

const StudentHistoryTab: React.FC<StudentHistoryTabProps> = ({ userId }) => {
  const [history, setHistory] = useState<AuditLogDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (userId) {
      fetchHistory();
    }
  }, [userId]);

  const fetchHistory = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.history.getUserHistory(userId);
      setHistory(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load history');
    } finally {
      setLoading(false);
    }
  };

  const getActionIcon = (action: string) => {
    switch (action) {
      case 'CREATE':
        return '➕';
      case 'CANCEL':
        return '❌';
      case 'EXPIRE':
        return '⏰';
      case 'ACCEPT':
        return '✅';
      default:
        return '📋';
    }
  };

  const getActionColor = (action: string) => {
    switch (action) {
      case 'CREATE':
        return '#8b5cf6';
      case 'CANCEL':
        return '#ef4444';
      case 'EXPIRE':
        return '#f59e0b';
      case 'ACCEPT':
        return '#22c55e';
      default:
        return '#6b7280';
    }
  };

  if (loading) {
    return <div className={styles['loading']}>Loading history...</div>;
  }

  return (
    <div className={styles['history']}>
      <h2>Reservation History</h2>
      {error && <div className={styles['error']}>{error}</div>}

      {history.length > 0 ? (
        <div className={styles['list']}>
          {history.map((entry) => (
            <div
              key={entry.id}
              className={styles['history-card']}
              style={{ borderLeft: `4px solid ${getActionColor(entry.action)}` }}
            >
              <div className={styles['action-row']}>
                <span className={styles['action-icon']}>{getActionIcon(entry.action)}</span>
                <span className={styles['action']} style={{ color: getActionColor(entry.action) }}>
                  {entry.action}
                </span>
                <span className={styles['timestamp']}>
                  {new Date(entry.timestamp).toLocaleString()}
                </span>
              </div>

              <div className={styles['details']}>
                <p className={styles['reservation-id']}>
                  Reservation #{entry.reservationId}
                </p>
                {entry.details && <p className={styles['detail-text']}>{entry.details}</p>}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className={styles['empty']}>
          <p>📚 No reservation history yet</p>
          <p style={{ fontSize: '0.95rem', color: '#9ca3af', marginTop: '0.5rem' }}>
            Start making reservations to see your history here!
          </p>
        </div>
      )}
    </div>
  );
};

export default StudentHistoryTab;
