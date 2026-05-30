import React, { useState, useEffect } from 'react';
import serviceApi, { type ExtensionRequest } from '../api/serviceApi';
import styles from '../styles/studentExtensionsTab.module.css';

interface StudentExtensionsTabProps {
  userId: number;
}

const StudentExtensionsTab: React.FC<StudentExtensionsTabProps> = ({ userId }) => {
  const [extensionRequests, setExtensionRequests] = useState<ExtensionRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchExtensionRequests();
    const interval = setInterval(fetchExtensionRequests, 5000);
    return () => clearInterval(interval);
  }, [userId]);

  const fetchExtensionRequests = async () => {
    try {
      setLoading(true);
      const requests = await serviceApi.extensions.getUserExtensionRequests(userId);
      setExtensionRequests(requests);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load extension requests');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING':
        return '#f59e0b';
      case 'APPROVED':
        return '#22c55e';
      case 'REJECTED':
        return '#ef4444';
      case 'EXPIRED':
        return '#94a3b8';
      default:
        return '#d1d5db';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PENDING':
        return '⏳';
      case 'APPROVED':
        return '✅';
      case 'REJECTED':
        return '❌';
      case 'EXPIRED':
        return '⌛';
      default:
        return '❓';
    }
  };

  if (loading && extensionRequests.length === 0) {
    return <div className={styles['container']}><p>Loading extension requests...</p></div>;
  }

  if (error) {
    return (
      <div className={styles['container']}>
        <p style={{ color: '#fca5a5' }}>⚠️ {error}</p>
      </div>
    );
  }

  if (extensionRequests.length === 0) {
    return (
      <div className={styles['container']}>
        <div className={styles['empty-state']}>
          <p>No extension requests yet</p>
        </div>
      </div>
    );
  }

  return (
    <div className={styles['container']}>
      <h2>My Extension Requests</h2>
      <div className={styles['requests-list']}>
        {extensionRequests.map((request) => (
          <div key={request.id} className={styles['request-card']}>
            <div className={styles['request-header']}>
              <div className={styles['status-badge']} style={{ borderColor: getStatusColor(request.status) }}>
                <span className={styles['status-icon']}>{getStatusIcon(request.status)}</span>
                <span className={styles['status-text']} style={{ color: getStatusColor(request.status) }}>
                  {request.status}
                </span>
              </div>
              <span className={styles['session-id']}>Session #{request.sessionId}</span>
            </div>

            <div className={styles['request-details']}>
              <div className={styles['detail-row']}>
                <span className={styles['label']}>Requested:</span>
                <span className={styles['value']}>
                  {new Date(request.requestedAt).toLocaleString()}
                </span>
              </div>

              {request.respondedAt && (
                <div className={styles['detail-row']}>
                  <span className={styles['label']}>Responded:</span>
                  <span className={styles['value']}>
                    {new Date(request.respondedAt).toLocaleString()}
                  </span>
                </div>
              )}

              {request.expiresAt && request.status === 'PENDING' && (
                <div className={styles['detail-row']}>
                  <span className={styles['label']}>Expires:</span>
                  <span className={styles['value']}>
                    {new Date(request.expiresAt).toLocaleString()}
                  </span>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default StudentExtensionsTab;
