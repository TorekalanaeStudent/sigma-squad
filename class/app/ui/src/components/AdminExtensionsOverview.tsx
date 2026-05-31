import React, { useState, useEffect } from 'react';
import serviceApi, { type ExtensionRequest } from '../api/serviceApi';
import AdminQuickOverviewCard, { type QuickOverviewCardProps } from './AdminQuickOverviewCard';
import styles from '../styles/adminOverviewSection.module.css';

interface AdminExtensionsOverviewProps {
  onNavigateToExtensions?: (extensionId: number) => void;
  onRefresh?: () => void;
}

const AdminExtensionsOverview: React.FC<AdminExtensionsOverviewProps> = ({
  onNavigateToExtensions,
  onRefresh,
}) => {
  const [extensions, setExtensions] = useState<ExtensionRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [timers, setTimers] = useState<{ [key: number]: number }>({});

  useEffect(() => {
    fetchPendingExtensions();
    const interval = setInterval(fetchPendingExtensions, 30000); // Fetch every 30 seconds instead of 1 second
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const timer = setInterval(() => {
      setTimers((prevTimers) => {
        const newTimers = { ...prevTimers };
        extensions.forEach((ext) => {
          if (ext.expiresAt) {
            const timeLeft = Math.max(
              0,
              Math.floor((new Date(ext.expiresAt).getTime() - new Date().getTime()) / 1000)
            );
            newTimers[ext.id] = timeLeft;
          }
        });
        return newTimers;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [extensions]);

  const fetchPendingExtensions = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.extensions.getPendingExtensionRequests();
      setExtensions(data.slice(0, 5));
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load extension requests');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id: number) => {
    try {
      await serviceApi.extensions.approveExtensionRequest(id);
      alert('Extension approved!');
      await fetchPendingExtensions();
      if (onRefresh) onRefresh();
    } catch (err: any) {
      alert(err.message || 'Failed to approve extension');
    }
  };

  const handleReject = async (id: number) => {
    try {
      await serviceApi.extensions.rejectExtensionRequest(id);
      alert('Extension rejected');
      await fetchPendingExtensions();
      if (onRefresh) onRefresh();
    } catch (err: any) {
      alert(err.message || 'Failed to reject extension');
    }
  };

  const handleCardClick = (id: number) => {
    if (onNavigateToExtensions) {
      onNavigateToExtensions(id);
    }
  };

  const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  if (loading) return <div className={styles['section']}>Loading extension requests...</div>;
  if (error) return <div className={styles['section']} style={{ color: '#fca5a5' }}>⚠️ {error}</div>;
  if (extensions.length === 0)
    return <div className={styles['section']}>No pending extension requests</div>;

  return (
    <section className={styles['section']}>
      <h3 className={styles['section-title']}>⏱️ Extension Requests</h3>
      <p className={styles['section-subtitle']}>Quick overview - Click to view details</p>

      <div className={styles['cards-grid']}>
        {extensions.map((ext) => {
          const isExpired = ext.expiresAt && new Date(ext.expiresAt) < new Date();
          const timeRemaining = timers[ext.id] || 0;
          const cardProps: QuickOverviewCardProps = {
            id: ext.id,
            title: `${ext.computerNumber || `PC ${ext.userId}`} - ${ext.userName || `User ${ext.userId}`}`,
            details: [
              { label: 'Student', value: ext.userName || `User ${ext.userId}` },
              { label: 'Computer', value: String(ext.computerNumber) || `Session ${ext.sessionId}` },
              { label: 'Requested At', value: new Date(ext.requestedAt).toLocaleTimeString() },
              { label: 'Time Remaining', value: isExpired ? '⏰ Expired' : `⏳ ${formatTime(timeRemaining)}` },
            ],
            actions: [
              {
                label: 'Approve',
                icon: '✅',
                color: 'success',
                onClick: () => handleApprove(ext.id),
              },
              {
                label: 'Reject',
                icon: '❌',
                color: 'danger',
                onClick: () => handleReject(ext.id),
              },
            ],
            onClick: () => handleCardClick(ext.id),
          };

          return <AdminQuickOverviewCard key={ext.id} {...cardProps} />;
        })}
      </div>
    </section>
  );
};

export default AdminExtensionsOverview;
