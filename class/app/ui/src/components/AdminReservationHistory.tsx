import React, { useState, useEffect } from 'react';
import serviceApi, { type Reservation } from '../api/serviceApi';
import styles from '../styles/auditLog.module.css';

interface AdminReservationHistoryProps {
  onRefresh?: () => void;
}

type SortColumn = keyof Reservation | null;
type SortOrder = 'asc' | 'desc';

const AdminReservationHistory: React.FC<AdminReservationHistoryProps> = () => {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [filteredReservations, setFilteredReservations] = useState<Reservation[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [sortColumn, setSortColumn] = useState<SortColumn>(null);
  const [sortOrder, setSortOrder] = useState<SortOrder>('asc');

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

  const handleSort = (column: SortColumn) => {
    if (sortColumn === column) {
      // Toggle sort order if clicking the same column
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    } else {
      // Set new column and default to ascending
      setSortColumn(column);
      setSortOrder('asc');
    }
  };

  const getSortedReservations = () => {
    if (!sortColumn) return filteredReservations;

    const sorted = [...filteredReservations].sort((a, b) => {
      const aVal = a[sortColumn];
      const bVal = b[sortColumn];

      if (aVal === null || aVal === undefined) return 1;
      if (bVal === null || bVal === undefined) return -1;

      if (typeof aVal === 'string') {
        const comparison = aVal.localeCompare(bVal as string);
        return sortOrder === 'asc' ? comparison : -comparison;
      }

      if (typeof aVal === 'number') {
        return sortOrder === 'asc' ? (aVal as number) - (bVal as number) : (bVal as number) - (aVal as number);
      }

      return 0;
    });

    return sorted;
  };

  const getSortIndicator = (column: SortColumn) => {
    if (sortColumn !== column) return '';
    return sortOrder === 'asc' ? ' ▲' : ' ▼';
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
              <th onClick={() => handleSort('id')} style={{ cursor: 'pointer' }}>Reservation ID{getSortIndicator('id')}</th>
              <th onClick={() => handleSort('userName')} style={{ cursor: 'pointer' }}>User Name{getSortIndicator('userName')}</th>
              <th onClick={() => handleSort('computerId')} style={{ cursor: 'pointer' }}>Computer{getSortIndicator('computerId')}</th>
              <th onClick={() => handleSort('reservedAt')} style={{ cursor: 'pointer' }}>Reserved At{getSortIndicator('reservedAt')}</th>
              <th onClick={() => handleSort('expiresAt')} style={{ cursor: 'pointer' }}>Expires At{getSortIndicator('expiresAt')}</th>
              <th onClick={() => handleSort('status')} style={{ cursor: 'pointer' }}>Status{getSortIndicator('status')}</th>
              <th>Duration (min)</th>
            </tr>
          </thead>
          <tbody>
            {filteredReservations.length === 0 ? (
              <tr><td colSpan={7} className={styles['empty']}>No reservations found</td></tr>
            ) : (
              getSortedReservations().map(r => (
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
