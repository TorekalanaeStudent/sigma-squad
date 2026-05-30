import React, { useState, useEffect } from 'react';
import serviceApi, { type Computer } from '../api/serviceApi';
import styles from '../styles/computerList.module.css';

interface ComputerListProps {
  onReserve: (computerId: number) => void;
}

const ComputerList: React.FC<ComputerListProps> = ({ onReserve }) => {
  const [computers, setComputers] = useState<Computer[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchComputers();
  }, []);

  const fetchComputers = async () => {
    try {
      setLoading(true);
      const data = await serviceApi.computers.getAllComputers();
      setComputers(data);
      setError(null);
    } catch (err: any) {
      setError(err.message || 'Failed to load computers');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'AVAILABLE':
        return '#22c55e';
      case 'RESERVED':
        return '#f59e0b';
      case 'IN_USE':
        return '#ef4444';
      case 'OUT_OF_SERVICE':
        return '#94a3b8';
      default:
        return '#6b7280';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'AVAILABLE':
        return '✅';
      case 'RESERVED':
        return '🔄';
      case 'IN_USE':
        return '🚀';
      case 'OUT_OF_SERVICE':
        return '🔧';
      default:
        return '❓';
    }
  };

  if (loading) {
    return <div className={styles['loading']}>Loading computers...</div>;
  }

  return (
    <div className={styles['computer-list']}>
      <h2>Available Computers</h2>
      {error && <div className={styles['error']}>{error}</div>}

      <div className={styles['grid']}>
        {computers.map((computer) => (
          <div
            key={computer.id}
            className={styles['computer-card']}
            style={{ borderLeft: `4px solid ${getStatusColor(computer.status)}` }}
          >
            <div className={styles['card-header']}>
              <span className={styles['icon']}>{getStatusIcon(computer.status)}</span>
              <span className={styles['number']}>PC {computer.computerNumber}</span>
            </div>

            <div className={styles['card-body']}>
              <div className={styles['status']}>
                <span className={styles['status-label']}>Status:</span>
                <span className={styles['status-value']} style={{ color: getStatusColor(computer.status) }}>
                  {computer.status}
                </span>
              </div>
            </div>

            <div className={styles['card-footer']}>
              <button
                className={styles['reserve-btn']}
                onClick={() => onReserve(computer.id)}
                disabled={computer.status !== 'AVAILABLE'}
              >
                {computer.status === 'AVAILABLE' ? '📖 Reserve' : 'Not Available'}
              </button>
            </div>
          </div>
        ))}
      </div>

      {computers.length === 0 && (
        <div className={styles['empty']}>
          <p>No computers found</p>
        </div>
      )}
    </div>
  );
};

export default ComputerList;
