import React, { useState, useEffect } from 'react';
import serviceApi, { type Reservation } from '../api/serviceApi';
import AuthService from '../api/authApi';
import styles from '../styles/userReservations.module.css';

interface StudentPendingReservationsProps {
  onRefresh?: () => void;
}

const StudentPendingReservations: React.FC<StudentPendingReservationsProps> = ({ onRefresh }) => {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const user = AuthService.getCurrentUser();

  useEffect(() => {
    if (user?.id) {
      fetchReservations();
    }
  }, [user?.id]);

  const fetchReservations = async () => {
    if (!user?.id) return;
    try {
      setLoading(true);
      const data = await serviceApi.reservations.getUserReservations(user.id);
      setReservations(data.filter(r => r.status === 'ACTIVE'));
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load reservations');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (id: number) => {
    try {
      await serviceApi.reservations.cancelReservation(id);
      await fetchReservations();
      if (onRefresh) onRefresh();
    } catch (err: any) {
      setError(err.message || 'Failed to cancel reservation');
    }
  };

  const getTimeRemaining = (expiresAt: string) => {
    const expiresDate = new Date(expiresAt);
    const now = new Date();
    const diffMs = expiresDate.getTime() - now.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    if (diffMins < 0) return 'Expired';
    if (diffMins === 0) return 'Less than 1 min';
    return `${diffMins} min${diffMins > 1 ? 's' : ''}`;
  };

  if (loading) {
    return <div className={styles['loading']}>Loading pending reservations...</div>;
  }

  return (
    <div className={styles['reservations']}>
      <h2>Pending Reservation</h2>
      {error && <div className={styles['error']}>{error}</div>}

      {reservations.length > 0 ? (
        <div className={styles['list']}>
          {reservations.map((reservation) => (
            <div
              key={reservation.id}
              className={styles['reservation-card']}
              style={{ borderLeft: '4px solid #f59e0b' }}
            >
              {/* Card Header */}
              <div className={styles['header']}>
                <div className={styles['header-content']}>
                  <div className={styles['title-row']}>
                    <span className={styles['status-icon']}>⏳</span>
                    <h3>Reservation #{reservation.id}</h3>
                  </div>
                  <p className={styles['subtitle']}>Computer: <strong>PC {reservation.computerId}</strong></p>
                </div>
                <span 
                  className={styles['status']} 
                  style={{ color: '#f59e0b', borderColor: '#f59e0b' }}
                >
                  PENDING
                </span>
              </div>

              {/* Card Details */}
              <div className={styles['details']}>
                <div className={styles['detail-row']}>
                  <span className={styles['label']}>📅 Reserved At:</span>
                  <span className={styles['value']}>
                    {new Date(reservation.reservedAt).toLocaleString()}
                  </span>
                </div>
                <div className={styles['detail-row']}>
                  <span className={styles['label']}>⏱️ Expires In:</span>
                  <span className={styles['value']} style={{ color: '#22c55e' }}>
                    {getTimeRemaining(reservation.expiresAt)}
                  </span>
                </div>
              </div>

              {/* Card Actions */}
              <div className={styles['actions']}>
                <button
                  className={styles['cancel-btn']}
                  onClick={() => handleCancel(reservation.id)}
                >
                  ✕ Cancel
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className={styles['empty']}>
          <p>🎯 No pending reservations</p>
          <p style={{ fontSize: '0.95rem', color: '#9ca3af', marginTop: '0.5rem' }}>
            Make a reservation to see it here while waiting for admin approval!
          </p>
        </div>
      )}
    </div>
  );
};

export default StudentPendingReservations;
