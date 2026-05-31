import React, { useState, useEffect } from 'react';
import serviceApi, { type Reservation } from '../api/serviceApi';
import styles from '../styles/adminDashboard.module.css';

interface AdminReservationHistoryProps {
  onRefresh?: () => void;
}

/**
 * AdminReservationHistory Component
 * Displays all reservation history (CONFIRMED, CANCELLED, EXPIRED)
 * for all users - accessible only to admin librarians.
 */
const AdminReservationHistory: React.FC<AdminReservationHistoryProps> = () => {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filterStatus, setFilterStatus] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchReservationHistory();
  }, []);

  const fetchReservationHistory = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.reservations.getReservationHistory();
      setReservations(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load reservation history');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return '#22c55e';
      case 'CANCELLED':
        return '#ef4444';
      case 'EXPIRED':
        return '#8b5cf6';
      default:
        return '#6b7280';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return '✓';
      case 'CANCELLED':
        return '✕';
      case 'EXPIRED':
        return '⏱️';
      default:
        return '?';
    }
  };

  const filteredReservations = reservations.filter(r => {
    // Filter by status
    const statusMatch = filterStatus ? r.status === filterStatus : true;
    
    // Filter by search query (search in userId, userName, computerId, or reservationId)
    const searchMatch = searchQuery === '' || 
      r.id?.toString().includes(searchQuery) ||
      r.userId?.toString().includes(searchQuery) ||
      r.computerId?.toString().includes(searchQuery) ||
      r.userName?.toLowerCase().includes(searchQuery.toLowerCase());
    
    return statusMatch && searchMatch;
  });

  if (loading) {
    return <div className={styles['loading']}>Loading reservation history...</div>;
  }

  return (
    <div className={styles['pending-reservations']}>
      <div className={styles['history-header']}>
        <h2>📚 Reservation History (All Users)</h2>
        <p className={styles['history-subtitle']}>Complete audit log of all reservations across the system</p>
      </div>

      {error && <div style={{ color: '#fca5a5', marginBottom: '1rem' }}>⚠️ {error}</div>}

      {/* Search Bar */}
      <div style={{
        marginBottom: '1.5rem',
        display: 'flex',
        gap: '1rem',
        alignItems: 'center'
      }}>
        <input
          type="text"
          placeholder="🔍 Search by Reservation ID, User ID, or Computer ID..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          style={{
            flex: 1,
            padding: '0.75rem 1rem',
            borderRadius: '0.5rem',
            border: '2px solid #374151',
            backgroundColor: '#111827',
            color: '#f3f4f6',
            fontSize: '0.95rem',
            transition: 'all 0.2s',
            outline: 'none'
          }}
          onFocus={(e) => e.currentTarget.style.borderColor = '#3b82f6'}
          onBlur={(e) => e.currentTarget.style.borderColor = '#374151'}
        />
        {searchQuery && (
          <button
            onClick={() => setSearchQuery('')}
            style={{
              padding: '0.5rem 1rem',
              borderRadius: '0.375rem',
              backgroundColor: '#374151',
              color: '#f3f4f6',
              border: 'none',
              cursor: 'pointer',
              fontSize: '0.875rem',
              transition: 'all 0.2s'
            }}
            onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#4b5563'}
            onMouseLeave={(e) => e.currentTarget.style.backgroundColor = '#374151'}
          >
            Clear
          </button>
        )}
      </div>

      {/* Filter Buttons */}
      <div style={{
        display: 'flex',
        gap: '0.75rem',
        marginBottom: '1.5rem',
        flexWrap: 'wrap',
        alignItems: 'center'
      }}>
        <button
          className={`${styles['filter-btn']} ${filterStatus === null ? styles['active'] : ''}`}
          onClick={() => setFilterStatus(null)}
          style={{
            padding: '0.6rem 1.2rem',
            borderRadius: '0.375rem',
            border: '2px solid',
            borderColor: filterStatus === null ? '#3b82f6' : '#4b5563',
            backgroundColor: filterStatus === null ? '#1e40af' : '#1f2937',
            color: '#f3f4f6',
            cursor: 'pointer',
            fontSize: '0.9rem',
            fontWeight: filterStatus === null ? '600' : '500',
            transition: 'all 0.2s',
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem'
          }}
          onMouseEnter={(e) => {
            if (filterStatus !== null) {
              e.currentTarget.style.backgroundColor = '#2d3748';
              e.currentTarget.style.borderColor = '#5a6b82';
            }
          }}
          onMouseLeave={(e) => {
            if (filterStatus !== null) {
              e.currentTarget.style.backgroundColor = '#1f2937';
              e.currentTarget.style.borderColor = '#4b5563';
            }
          }}
        >
          <span>📋</span> All ({reservations.length})
        </button>
        
        <button
          className={`${styles['filter-btn']} ${filterStatus === 'CONFIRMED' ? styles['active'] : ''}`}
          onClick={() => setFilterStatus('CONFIRMED')}
          style={{
            padding: '0.6rem 1.2rem',
            borderRadius: '0.375rem',
            border: '2px solid',
            borderColor: filterStatus === 'CONFIRMED' ? '#22c55e' : '#4b5563',
            backgroundColor: filterStatus === 'CONFIRMED' ? '#15803d' : '#1f2937',
            color: filterStatus === 'CONFIRMED' ? '#dcfce7' : '#f3f4f6',
            cursor: 'pointer',
            fontSize: '0.9rem',
            fontWeight: filterStatus === 'CONFIRMED' ? '600' : '500',
            transition: 'all 0.2s',
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem'
          }}
          onMouseEnter={(e) => {
            if (filterStatus !== 'CONFIRMED') {
              e.currentTarget.style.backgroundColor = '#16a34a';
              e.currentTarget.style.borderColor = '#4ade80';
            }
          }}
          onMouseLeave={(e) => {
            if (filterStatus !== 'CONFIRMED') {
              e.currentTarget.style.backgroundColor = '#1f2937';
              e.currentTarget.style.borderColor = '#4b5563';
            }
          }}
        >
          <span>✓</span> Confirmed ({reservations.filter(r => r.status === 'CONFIRMED').length})
        </button>
        
        <button
          className={`${styles['filter-btn']} ${filterStatus === 'CANCELLED' ? styles['active'] : ''}`}
          onClick={() => setFilterStatus('CANCELLED')}
          style={{
            padding: '0.6rem 1.2rem',
            borderRadius: '0.375rem',
            border: '2px solid',
            borderColor: filterStatus === 'CANCELLED' ? '#ef4444' : '#4b5563',
            backgroundColor: filterStatus === 'CANCELLED' ? '#b91c1c' : '#1f2937',
            color: filterStatus === 'CANCELLED' ? '#fee2e2' : '#f3f4f6',
            cursor: 'pointer',
            fontSize: '0.9rem',
            fontWeight: filterStatus === 'CANCELLED' ? '600' : '500',
            transition: 'all 0.2s',
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem'
          }}
          onMouseEnter={(e) => {
            if (filterStatus !== 'CANCELLED') {
              e.currentTarget.style.backgroundColor = '#dc2626';
              e.currentTarget.style.borderColor = '#fca5a5';
            }
          }}
          onMouseLeave={(e) => {
            if (filterStatus !== 'CANCELLED') {
              e.currentTarget.style.backgroundColor = '#1f2937';
              e.currentTarget.style.borderColor = '#4b5563';
            }
          }}
        >
          <span>✕</span> Cancelled ({reservations.filter(r => r.status === 'CANCELLED').length})
        </button>
        
        <button
          className={`${styles['filter-btn']} ${filterStatus === 'EXPIRED' ? styles['active'] : ''}`}
          onClick={() => setFilterStatus('EXPIRED')}
          style={{
            padding: '0.6rem 1.2rem',
            borderRadius: '0.375rem',
            border: '2px solid',
            borderColor: filterStatus === 'EXPIRED' ? '#8b5cf6' : '#4b5563',
            backgroundColor: filterStatus === 'EXPIRED' ? '#6d28d9' : '#1f2937',
            color: filterStatus === 'EXPIRED' ? '#ede9fe' : '#f3f4f6',
            cursor: 'pointer',
            fontSize: '0.9rem',
            fontWeight: filterStatus === 'EXPIRED' ? '600' : '500',
            transition: 'all 0.2s',
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem'
          }}
          onMouseEnter={(e) => {
            if (filterStatus !== 'EXPIRED') {
              e.currentTarget.style.backgroundColor = '#7c3aed';
              e.currentTarget.style.borderColor = '#c4b5fd';
            }
          }}
          onMouseLeave={(e) => {
            if (filterStatus !== 'EXPIRED') {
              e.currentTarget.style.backgroundColor = '#1f2937';
              e.currentTarget.style.borderColor = '#4b5563';
            }
          }}
        >
          <span>⏱️</span> Expired ({reservations.filter(r => r.status === 'EXPIRED').length})
        </button>
      </div>

      {filteredReservations.length > 0 ? (
        <div className={styles['reservations-list']}>
          {filteredReservations.map((reservation) => (
            <div
              key={reservation.id}
              className={styles['admin-reservation-card']}
              style={{ borderLeft: `4px solid ${getStatusColor(reservation.status)}`, width: '100%', boxSizing: 'border-box' }}
            >
              <div className={styles['card-header']}>
                <div>
                  <h3>Reservation #{reservation.id}</h3>
                  <p style={{ fontSize: '0.9rem', color: '#9ca3af' }}>
                    {reservation.userName} (ID: {reservation.userId}) | Computer: {reservation.computerId}
                  </p>
                </div>
                <span className={styles['status']} style={{ color: getStatusColor(reservation.status) }}>
                  <span style={{ marginRight: '0.5rem' }}>{getStatusIcon(reservation.status)}</span>
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
                <div className={styles['info-row']}>
                  <span>Duration:</span>
                  <span>
                    {Math.round(
                      (new Date(reservation.expiresAt).getTime() - new Date(reservation.reservedAt).getTime()) / 60000
                    )}{' '}
                    minutes
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className={styles['empty']}>
          <p>
            {filterStatus
              ? `No ${filterStatus.toLowerCase()} reservations found`
              : 'No reservation history at this time'}
          </p>
        </div>
      )}
    </div>
  );
};

export default AdminReservationHistory;
