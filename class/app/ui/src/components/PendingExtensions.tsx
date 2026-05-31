import React, { useState, useEffect } from 'react';
import serviceApi, { type ExtensionRequest } from '../api/serviceApi';
import styles from '../styles/adminDashboard.module.css';

interface PendingExtensionsProps {
  onRefresh?: () => void;
  highlightedId?: number | null;
}

const PendingExtensions: React.FC<PendingExtensionsProps> = ({ onRefresh, highlightedId }) => {
  const [extensionRequests, setExtensionRequests] = useState<ExtensionRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchPendingExtensions();
  }, []);

  const fetchPendingExtensions = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.extensions.getPendingExtensionRequests();
      setExtensionRequests(data);
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
      alert('Extension approved! Session extended by 1 hour.');
      await fetchPendingExtensions();
      if (onRefresh) onRefresh();
    } catch (err: any) {
      setError(err.message || 'Failed to approve extension');
    }
  };

  const handleReject = async (id: number) => {
    try {
      await serviceApi.extensions.rejectExtensionRequest(id);
      alert('Extension request rejected');
      await fetchPendingExtensions();
      if (onRefresh) onRefresh();
    } catch (err: any) {
      setError(err.message || 'Failed to reject extension');
    }
  };

  if (loading) {
    return <div className={styles['loading']}>Loading extension requests...</div>;
  }

  return (
    <div className={styles['pending-extensions']}>
      <h2>Extension Requests (1 Hour Maximum)</h2>
      {error && <div style={{ color: '#fca5a5', marginBottom: '1rem' }}>⚠️ {error}</div>}

      {extensionRequests.length > 0 ? (
        <div className={styles['extensions-list']}>
          {extensionRequests.map((request) => (
            <div
              id={`extension-${request.id}`}
              key={request.id}
              className={styles['extension-card']}
              style={{
                backgroundColor: highlightedId === request.id ? 'rgba(139, 92, 246, 0.15)' : undefined,
                borderColor: highlightedId === request.id ? 'rgba(139, 92, 246, 0.8)' : undefined,
                boxShadow: highlightedId === request.id ? '0 0 20px rgba(139, 92, 246, 0.4)' : undefined,
              }}
            >
              <div className={styles['card-header']}>
                <div>
                  <h3>Extension Request #{request.id}</h3>
                  <p>User ID: {request.userId} | Session ID: {request.sessionId}</p>
                </div>
                <span className={styles['status']} style={{ color: '#f59e0b' }}>
                  PENDING
                </span>
              </div>

              <div className={styles['card-body']}>
                <div className={styles['info-row']}>
                  <span>Requested At:</span>
                  <span>{new Date(request.requestedAt).toLocaleString()}</span>
                </div>
                <div className={styles['info-row']}>
                  <span>Duration:</span>
                  <span>1 Hour (Fixed)</span>
                </div>
              </div>

              <div className={styles['card-actions']}>
                <button
                  className={styles['approve-btn']}
                  onClick={() => handleApprove(request.id)}
                >
                  ✓ Approve
                </button>
                <button
                  className={styles['reject-btn']}
                  onClick={() => handleReject(request.id)}
                >
                  ✕ Reject
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className={styles['empty']}>
          <p>No pending extension requests</p>
        </div>
      )}
    </div>
  );
};

export default PendingExtensions;
