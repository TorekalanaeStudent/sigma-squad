import React, { useState, useEffect } from 'react';
import serviceApi, { type Reservation } from '../api/serviceApi';
import styles from '../styles/auditLog.module.css';

interface AdminReservationHistoryProps {
  onRefresh?: () => void;
}

const AdminReservationHistory: React.FC<AdminReservationHistoryProps> = () => {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [filteredReservations, setFilteredReservations] = useState<Reservation[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchReservationHistory();
  }, []);

  useEffect(() => {
    filterReservations();
  }, [searchTerm, reservations]);

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

  const filterReservations = () => {
    if (!searchTerm) {
      setFilteredReservations(reservations);
      return;
    }
    const term = searchTerm.toLowerCase();
    setFilteredReservations(reservations.filter(r => 
      (r.userName?.toLowerCase() || '').includes(term) ||
      r.id?.toString().includes(term) ||
      r.computerId?.toString().includes(term) ||
      r.userId?.toString().includes(term)
    ));
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return '✅';
      case 'CANCELLED':
        return '❌';
      case 'EXPIRED':
        return '⏰';
      default:
        return '❓';
    }
  };

  const exportToExcel = () => {
    const headers = ['Reservation ID', 'User Name', 'Computer', 'Reserved At', 'Expires At', 'Status', 'Duration (min)'];
    const rows = filteredReservations.map(r => [
      r.id,
      r.userName || 'N/A',
      r.computerId || 'N/A',
      r.reservedAt ? new Date(r.reservedAt).toLocaleString() : 'N/A',
      r.expiresAt ? new Date(r.expiresAt).toLocaleString() : 'N/A',
      r.status || 'N/A',
      r.expiresAt && r.reservedAt ? Math.round((new Date(r.expiresAt).getTime() - new Date(r.reservedAt).getTime()) / 60000) : 0
    ]);

    const csv = [headers, ...rows].map(row => row.map(cell => `"${cell}"`).join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `reservation-history-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
  };

  if (loading) return <div className={styles['container']}>Loading reservation history...</div>;

  return (
    <div className={styles['container']}>
      <section className={styles['header']}>
        <h2>📚 Reservation History</h2>
        <div className={styles['controls']}>
          <input
            type="text"
            placeholder="Search by user, computer, or reservation ID..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className={styles['search-input']}
          />
          <button onClick={exportToExcel} className={styles['export-btn']}>
            📊 Export to Excel
          </button>
        </div>
      </section>

      {error && <div className={styles['error-msg']}>❌ {error}</div>}

      <div className={styles['table-wrapper']}>
        <table className={styles['audit-table']}>
          <thead>
            <tr>
              <th>Reservation ID</th>
              <th>User Name</th>
              <th>Computer</th>
              <th>Reserved At</th>
              <th>Expires At</th>
              <th>Status</th>
              <th>Duration (min)</th>
            </tr>
          </thead>
          <tbody>
            {filteredReservations.length === 0 ? (
              <tr><td colSpan={7} className={styles['empty']}>No reservations found</td></tr>
            ) : (
              filteredReservations.map(r => (
                <tr key={r.id}>
                  <td>#{r.id}</td>
                  <td>{r.userName || 'N/A'}</td>
                  <td>{r.computerId || 'N/A'}</td>
                  <td>{r.reservedAt ? new Date(r.reservedAt).toLocaleString() : 'N/A'}</td>
                  <td>{r.expiresAt ? new Date(r.expiresAt).toLocaleString() : 'N/A'}</td>
                  <td>{getStatusIcon(r.status || '')} {r.status}</td>
                  <td>{r.expiresAt && r.reservedAt ? Math.round((new Date(r.expiresAt).getTime() - new Date(r.reservedAt).getTime()) / 60000) : 0}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminReservationHistory;
