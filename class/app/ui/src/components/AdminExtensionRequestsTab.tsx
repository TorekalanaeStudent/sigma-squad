import React, { useState, useEffect } from 'react';
import serviceApi from '../api/serviceApi';
import styles from '../styles/adminExtensions.module.css';

interface ExtensionRequest {
  id: number;
  sessionId: number;
  userId: number;
  userName?: string;
  computerNumber?: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'EXPIRED';
  requestedAt: string;
  respondedAt?: string;
  durationMinutes: number;
}

export const AdminExtensionRequestsTab: React.FC = () => {
  const [extensionRequests, setExtensionRequests] = useState<ExtensionRequest[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [actionInProgress, setActionInProgress] = useState<number | null>(null);

  useEffect(() => {
    fetchPendingExtensions();
    const interval = setInterval(fetchPendingExtensions, 10000); // Refresh every 10 seconds
    return () => clearInterval(interval);
  }, []);

  const fetchPendingExtensions = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await serviceApi.extensions.getPendingExtensionRequests();
      setExtensionRequests(response.data);
    } catch (err: any) {
      setError(err.message || 'Failed to load extension requests');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id: number) => {
    setActionInProgress(id);
    setError(null);
    try {
      await serviceApi.extensions.approveExtensionRequest(id);
      setExtensionRequests((prev) =>
        prev.map((req) =>
          req.id === id ? { ...req, status: 'APPROVED', respondedAt: new Date().toISOString() } : req
        )
      );
    } catch (err: any) {
      setError(err.message || 'Failed to approve extension request');
    } finally {
      setActionInProgress(null);
    }
  };

  const handleReject = async (id: number) => {
    setActionInProgress(id);
    setError(null);
    try {
      await serviceApi.extensions.rejectExtensionRequest(id);
      setExtensionRequests((prev) =>
        prev.map((req) =>
          req.id === id ? { ...req, status: 'REJECTED', respondedAt: new Date().toISOString() } : req
        )
      );
    } catch (err: any) {
      setError(err.message || 'Failed to reject extension request');
    } finally {
      setActionInProgress(null);
    }
  };

  const pendingRequests = extensionRequests.filter((r) => r.status === 'PENDING');
  const processedRequests = extensionRequests.filter((r) => r.status !== 'PENDING');

  if (loading && extensionRequests.length === 0) {
    return <div className={styles['container']}>Loading extension requests...</div>;
  }

  return (
    <div className={styles['container']}>
      {error && <div className={styles['error-message']}>⚠️ {error}</div>}

      {/* Pending Requests Section */}
      <section className={styles['section']}>
        <div className={styles['section-header']}>
          <h3>📝 Pending Extension Requests ({pendingRequests.length})</h3>
          <button
            className={styles['refresh-btn']}
            onClick={fetchPendingExtensions}
            disabled={loading}
          >
            {loading ? '⏳ Refreshing...' : '🔄 Refresh'}
          </button>
        </div>

        {pendingRequests.length === 0 ? (
          <div className={styles['no-data']}>
            <p>✅ No pending extension requests</p>
          </div>
        ) : (
          <div className={styles['requests-grid']}>
            {pendingRequests.map((req) => (
              <div key={req.id} className={styles['request-card']}>
                <div className={styles['request-header']}>
                  <h4>{req.userName || `Student #${req.userId}`}</h4>
                  <span className={styles['status-badge']}>PENDING</span>
                </div>

                <div className={styles['request-details']}>
                  <div className={styles['detail-item']}>
                    <span className={styles['label']}>Computer:</span>
                    <span className={styles['value']}>PC-{req.computerNumber || req.sessionId}</span>
                  </div>
                  <div className={styles['detail-item']}>
                    <span className={styles['label']}>Requested At:</span>
                    <span className={styles['value']}>
                      {new Date(req.requestedAt).toLocaleTimeString()}
                    </span>
                  </div>
                  <div className={styles['detail-item']}>
                    <span className={styles['label']}>Extension Duration:</span>
                    <span className={styles['value']}>{req.durationMinutes} minutes</span>
                  </div>
                </div>

                <div className={styles['request-actions']}>
                  <button
                    className={styles['approve-btn']}
                    onClick={() => handleApprove(req.id)}
                    disabled={actionInProgress === req.id}
                  >
                    {actionInProgress === req.id ? '⏳ Approving...' : '✅ Approve'}
                  </button>
                  <button
                    className={styles['reject-btn']}
                    onClick={() => handleReject(req.id)}
                    disabled={actionInProgress === req.id}
                  >
                    {actionInProgress === req.id ? '⏳ Rejecting...' : '❌ Reject'}
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Processed Requests Section */}
      {processedRequests.length > 0 && (
        <section className={styles['section']}>
          <h3>📋 Processed Requests ({processedRequests.length})</h3>
          <div className={styles['requests-grid']}>
            {processedRequests.map((req) => (
              <div key={req.id} className={`${styles['request-card']} ${styles['processed']}`}>
                <div className={styles['request-header']}>
                  <h4>{req.userName || `Student #${req.userId}`}</h4>
                  <span
                    className={styles['status-badge']}
                    style={{
                      backgroundColor:
                        req.status === 'APPROVED'
                          ? 'rgba(34, 197, 94, 0.2)'
                          : req.status === 'REJECTED'
                          ? 'rgba(239, 68, 68, 0.2)'
                          : 'rgba(156, 163, 175, 0.2)',
                      color:
                        req.status === 'APPROVED'
                          ? '#22c55e'
                          : req.status === 'REJECTED'
                          ? '#ef4444'
                          : '#9ca3af',
                    }}
                  >
                    {req.status}
                  </span>
                </div>

                <div className={styles['request-details']}>
                  <div className={styles['detail-item']}>
                    <span className={styles['label']}>Computer:</span>
                    <span className={styles['value']}>PC-{req.computerNumber || req.sessionId}</span>
                  </div>
                  <div className={styles['detail-item']}>
                    <span className={styles['label']}>Responded At:</span>
                    <span className={styles['value']}>
                      {req.respondedAt ? new Date(req.respondedAt).toLocaleTimeString() : 'N/A'}
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  );
};

export default AdminExtensionRequestsTab;
