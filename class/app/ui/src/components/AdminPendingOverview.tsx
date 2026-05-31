import React, { useState, useEffect } from 'react';
import serviceApi, { type Reservation } from '../api/serviceApi';
import AdminQuickOverviewCard, { type QuickOverviewCardProps } from './AdminQuickOverviewCard';
import styles from '../styles/adminOverviewSection.module.css';

interface AdminPendingOverviewProps {
  onNavigateToPending?: (reservationId: number) => void;
  onRefresh?: () => void;
}

const AdminPendingOverview: React.FC<AdminPendingOverviewProps> = ({
  onNavigateToPending,
  onRefresh,
}) => {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchPendingReservations();
  }, []);

  const fetchPendingReservations = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.reservations.getAllReservations();
      const pending = data.filter(r => r.status === 'ACTIVE').slice(0, 5);
      setReservations(pending);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load pending reservations');
    } finally {
      setLoading(false);
    }
  };

  const handleAccept = async (id: number) => {
    try {
      await serviceApi.reservations.confirmReservation(id);
      alert('Reservation accepted!');
      await fetchPendingReservations();
      if (onRefresh) onRefresh();
    } catch (err: any) {
      alert(err.message || 'Failed to accept reservation');
    }
  };

  const handleDecline = async (id: number) => {
    try {
      await serviceApi.reservations.cancelReservation(id);
      alert('Reservation declined');
      await fetchPendingReservations();
      if (onRefresh) onRefresh();
    } catch (err: any) {
      alert(err.message || 'Failed to decline reservation');
    }
  };

  const handleCardClick = (id: number) => {
    if (onNavigateToPending) {
      onNavigateToPending(id);
    }
  };

  if (loading) return <div className={styles['section']}>Loading pending reservations...</div>;
  if (error) return <div className={styles['section']} style={{ color: '#fca5a5' }}>⚠️ {error}</div>;
  if (reservations.length === 0)
    return <div className={styles['section']}>No pending reservations</div>;

  return (
    <section className={styles['section']}>
      <h3 className={styles['section-title']}>📋 Pending Reservations</h3>
      <p className={styles['section-subtitle']}>Quick overview - Click to view details</p>

      <div className={styles['cards-grid']}>
        {reservations.map((res) => {
          const cardProps: QuickOverviewCardProps = {
            id: res.id,
            title: `PC ${res.computerNumber || res.computerId} - ${res.userName}`,
            details: [
              { label: 'Student', value: res.userName },
              { label: 'Computer', value: `PC ${res.computerNumber || res.computerId}` },
              { label: 'Reserved At', value: new Date(res.reservedAt).toLocaleTimeString() },
            ],
            actions: [
              {
                label: 'Accept',
                icon: '✅',
                color: 'success',
                onClick: () => handleAccept(res.id),
              },
              {
                label: 'Decline',
                icon: '❌',
                color: 'danger',
                onClick: () => handleDecline(res.id),
              },
            ],
            onClick: () => handleCardClick(res.id),
          };

          return <AdminQuickOverviewCard key={res.id} {...cardProps} />;
        })}
      </div>
    </section>
  );
};

export default AdminPendingOverview;
