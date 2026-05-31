import React, { useState, useEffect } from 'react';
import serviceApi from '../api/serviceApi';
import styles from '../styles/auditLog.module.css';

interface SessionRecord {
  id: number;
  userid: number;
  username: string;
  computerid: number;
  computernumber: string;
  starttime: string;
  endtime: string;
  minutesused: number;
}

const AuditLog: React.FC = () => {
  const [sessions, setSessions] = useState<SessionRecord[]>([]);
  const [filteredSessions, setFilteredSessions] = useState<SessionRecord[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchSessions();
  }, []);

  useEffect(() => {
    filterSessions();
  }, [searchTerm, sessions]);

  const fetchSessions = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.auditLog.getAllSessions();
      setSessions(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load audit log');
    } finally {
      setLoading(false);
    }
  };

  const filterSessions = () => {
    if (!searchTerm) {
      setFilteredSessions(sessions);
      return;
    }
    const term = searchTerm.toLowerCase();
    setFilteredSessions(sessions.filter(s => 
      (s.username?.toLowerCase() || '').includes(term) ||
      (s.computernumber?.toLowerCase() || '').includes(term) ||
      s.id.toString().includes(term)
    ));
  };

  const exportToExcel = () => {
    const headers = ['Session ID', 'User Name', 'Computer', 'Start Time', 'End Time', 'Minutes Used'];
    const rows = filteredSessions.map(s => [
      s.id,
      s.username || 'N/A',
      s.computernumber || 'N/A',
      s.starttime ? new Date(s.starttime).toLocaleString() : 'N/A',
      s.endtime ? new Date(s.endtime).toLocaleString() : 'N/A',
      s.minutesused || 0
    ]);

    const csv = [headers, ...rows].map(row => row.map(cell => `"${cell}"`).join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `audit-log-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
  };

  if (loading) return <div className={styles['container']}>Loading audit log...</div>;

  return (
    <div className={styles['container']}>
      <section className={styles['header']}>
        <h2>Session Audit Log</h2>
        <div className={styles['controls']}>
          <input
            type="text"
            placeholder="Search by user, computer, or session ID..."
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
              <th>Session ID</th>
              <th>User Name</th>
              <th>Computer</th>
              <th>Start Time</th>
              <th>End Time</th>
              <th>Minutes Used</th>
            </tr>
          </thead>
          <tbody>
            {filteredSessions.length === 0 ? (
              <tr><td colSpan={6} className={styles['empty']}>No sessions found</td></tr>
            ) : (
              filteredSessions.map(s => (
                <tr key={s.id}>
                  <td>#{s.id}</td>
                  <td>{s.username || 'N/A'}</td>
                  <td>{s.computernumber || 'N/A'}</td>
                  <td>{s.starttime ? new Date(s.starttime).toLocaleString() : 'N/A'}</td>
                  <td>{s.endtime ? new Date(s.endtime).toLocaleString() : 'N/A'}</td>
                  <td>{s.minutesused || 0}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AuditLog;
