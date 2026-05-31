import React, { useState, useEffect } from 'react';
import serviceApi, { type Reservation } from '../api/serviceApi';
import styles from '../styles/adminDashboard.module.css';

interface PendingReservationsProps {
  onRefresh?: () => void;
  highlightedId?: number | null;
}

const PendingReservations: React.FC<PendingReservationsProps> = ({ onRefresh, highlightedId }) => {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editTime, setEditTime] = useState<string>('');

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.reservations.getAllReservations();
      const pending = data.filter(r => r.status === 'ACTIVE');
      setReservations(pending);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load reservations');
    } finally {
      setLoading(false);
    }
  };

  const handleAccept = async (id: number) => {
    try {
      await serviceApi.reservations.confirmReservation(id);
      alert('Reservation accepted! User can now start their session.');
      await fetchReservations();
      if (onRefresh) onRefresh();
    } catch (err: any) {
      setError(err.message || 'Failed to accept reservation');
    }
  };

  const handleCancel = async (id: number) => {
    try {
      await serviceApi.reservations.cancelReservation(id);
      alert('Reservation cancelled');
      await fetchReservations();
      if (onRefresh) onRefresh();
    } catch (err: any) {
      setError(err.message || 'Failed to cancel reservation');
    }
  };

  const handleEditTime = async (id: number) => {
    try {
      const expiresAt = new Date(editTime);
      const expiresAtSeconds = Math.floor(expiresAt.getTime() / 1000);
      await serviceApi.reservations.updateReservationExpiry(id, expiresAtSeconds);
      alert('Reservation time updated!');
      setEditingId(null);
      setEditTime('');
      await fetchReservations();
    } catch (err: any) {
      setError(err.message || 'Failed to update reservation time');
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return '#f59e0b';
      case 'CONFIRMED':
        return '#22c55e';
      default:
        return '#6b7280';
    }
  };

  if (loading) {
    return <div className={styles['loading']}>Loading reservations...</div>;
  }

  return (
    <div className={styles['pending-reservations']}>
      <h2>⏱️ Pending Requests (Active Only)</h2>
      {error && <div style={{ color: '#fca5a5', marginBottom: '1rem' }}>⚠️ {error}</div>}

      {reservations.length > 0 ? (
        <div className={styles['reservations-list']}>
          {reservations.map((reservation) => (
            <div
              id={`pending-${reservation.id}`}
              key={reservation.id}
              className={styles['admin-reservation-card']}
              style={{
                borderLeft: `4px solid ${getStatusColor(reservation.status)}`,
                backgroundColor: highlightedId === reservation.id ? 'rgba(139, 92, 246, 0.15)' : undefined,
                borderColor: highlightedId === reservation.id ? 'rgba(139, 92, 246, 0.8)' : undefined,
                boxShadow: highlightedId === reservation.id ? '0 0 20px rgba(139, 92, 246, 0.4)' : undefined,
              }}
            >
              <div className={styles['card-header']}>
                <div>
                  <h3>Reservation #{reservation.id}</h3>
                  <p>User ID: {reservation.userId} | Computer: {reservation.computerId}</p>
                </div>
                <span className={styles['status']} style={{ color: getStatusColor(reservation.status) }}>
                  {reservation.status}
                </span>
              </div>

              <div className={styles['card-body']}>
                <div className={styles['info-row']}>
                  <span>Reserved At:</span>
                  <span>{new Date(reservation.reservedAt).toLocaleString()}</span>
                </div>
                <div className={styles['info-row']}>
                  <span>Expires At:</span>
                  <span>{new Date(reservation.expiresAt).toLocaleString()}</span>
                </div>

                {editingId === reservation.id ? (
                  <div className={styles['edit-time-form']}>
                    <input
                      type="datetime-local"
                      value={editTime}
                      onChange={(e) => setEditTime(e.target.value)}
                      className={styles['time-input']}
                    />
                    <button
                      className={styles['save-btn']}
                      onClick={() => handleEditTime(reservation.id)}
                    >
                      Save
                    </button>
                    <button
                      className={styles['cancel-edit-btn']}
                      onClick={() => {
                        setEditingId(null);
                        setEditTime('');
                      }}
                    >
                      Cancel
                    </button>
                  </div>
                ) : null}
              </div>

              <div className={styles['card-actions']}>
                <button
                  className={styles['accept-btn']}
                  onClick={() => handleAccept(reservation.id)}
                >
                  ✓ Accept
                </button>
                <button
                  className={styles['edit-btn']}
                  onClick={() => {
                    setEditingId(reservation.id);
                    setEditTime(new Date(reservation.expiresAt).toISOString().slice(0, 16));
                  }}
                >
                  ⏱️ Edit Time
                </button>
                <button
                  className={styles['reject-btn']}
                  onClick={() => handleCancel(reservation.id)}
                >
                  ✕ Reject
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className={styles['empty']}>
          <p>No pending reservations at this time</p>
        </div>
      )}
    </div>
  );
};

export default PendingReservations;
