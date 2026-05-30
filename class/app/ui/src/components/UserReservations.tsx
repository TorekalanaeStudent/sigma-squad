import React, { useState, useEffect } from 'react';
import serviceApi, { type Reservation } from '../api/serviceApi';
import AuthService from '../api/authApi';
import styles from '../styles/userReservations.module.css';

interface UserReservationsProps {
  onRefresh?: () => void;
}

const UserReservations: React.FC<UserReservationsProps> = ({ onRefresh }) => {
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
      setReservations(data.filter(r => r.status !== 'ACTIVE'));
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

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return '#f59e0b';
      case 'CONFIRMED':
        return '#22c55e';
      case 'EXPIRED':
        return '#94a3b8';
      case 'CANCELLED':
        return '#ef4444';
      default:
        return '#6b7280';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return '⏳';
      case 'CONFIRMED':
        return '✅';
      case 'EXPIRED':
        return '⏰';
      case 'CANCELLED':
        return '❌';
      default:
        return '📋';
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
    return <div className={styles['loading']}>Loading reservations...</div>;
  }

  return (
    <div className={styles['reservations']}>
      <h2>Your Reservations</h2>
      {error && <div className={styles['error']}>{error}</div>}

      {reservations.length > 0 ? (
        <div className={styles['list']}>
          {reservations.map((reservation) => (
            <div
              key={reservation.id}
              className={styles['reservation-card']}
              style={{ borderLeft: `4px solid ${getStatusColor(reservation.status)}` }}
            >
              {/* Card Header */}
              <div className={styles['header']}>
                <div className={styles['header-content']}>
                  <div className={styles['title-row']}>
                    <span className={styles['status-icon']}>{getStatusIcon(reservation.status)}</span>
                    <h3>Reservation #{reservation.id}</h3>
                  </div>
                  <p className={styles['subtitle']}>Computer: <strong>PC {reservation.computerId}</strong></p>
                </div>
                <span 
                  className={styles['status']} 
                  style={{ color: getStatusColor(reservation.status), borderColor: getStatusColor(reservation.status) }}
                >
                  {reservation.status}
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
                  <span className={styles['label']}>
                    {reservation.status === 'ACTIVE' ? '⏱️ Expires In:' : '⏰ Expires At:'}
                  </span>
                  <span className={styles['value']} style={{
                    color: reservation.status === 'ACTIVE' && getTimeRemaining(reservation.expiresAt).includes('min') ? '#22c55e' : 'inherit'
                  }}>
                    {reservation.status === 'ACTIVE'
                      ? getTimeRemaining(reservation.expiresAt)
                      : new Date(reservation.expiresAt).toLocaleString()}
                  </span>
                </div>
              </div>

              {/* Success Message */}
              {/* Card Actions */}
              <div className={styles['actions']}>
                {(reservation.status === 'ACTIVE' || reservation.status === 'CONFIRMED') && (
                  <button
                    className={styles['cancel-btn']}
                    onClick={() => handleCancel(reservation.id)}
                  >
                    ✕ Cancel
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className={styles['empty']}>
          <p>🎯 No reservations yet</p>
          <p style={{ fontSize: '0.95rem', color: '#9ca3af', marginTop: '0.5rem' }}>
            Browse available computers and make your first reservation!
          </p>
        </div>
      )}
    </div>
  );
};

export default UserReservations;
