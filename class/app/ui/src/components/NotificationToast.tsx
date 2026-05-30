import React, { useEffect } from 'react';
import { type Notification } from '../hooks/useWebSocketNotifications';
import styles from '../styles/notificationToast.module.css';

interface NotificationToastProps {
  notification: Notification;
  index: number;
  onClose: () => void;
}

const NotificationToast: React.FC<NotificationToastProps> = ({ notification, index, onClose }) => {
  useEffect(() => {
    // Auto-dismiss after 5 seconds
    const timer = setTimeout(onClose, 5000);
    return () => clearTimeout(timer);
  }, [onClose]);

  const getToastColor = (): string => {
    switch (notification.type) {
      case 'WARNING':
        return styles['toast-warning'];
      case 'ERROR':
        return styles['toast-error'];
      case 'SUCCESS':
        return styles['toast-success'];
      case 'INFO':
      default:
        return styles['toast-info'];
    }
  };

  const getIcon = (): string => {
    switch (notification.type) {
      case 'WARNING':
        return '⚠️';
      case 'ERROR':
        return '❌';
      case 'SUCCESS':
        return '✅';
      case 'INFO':
      default:
        return 'ℹ️';
    }
  };

  return (
    <div className={`${styles['toast']} ${getToastColor()}`} style={{ bottom: `${20 + index * 80}px` }}>
      <div className={styles['toast-content']}>
        <span className={styles['toast-icon']}>{getIcon()}</span>
        <div className={styles['toast-text']}>
          <div className={styles['toast-title']}>{notification.title}</div>
          <div className={styles['toast-message']}>{notification.message}</div>
        </div>
      </div>
      <button className={styles['toast-close']} onClick={onClose}>
        ✕
      </button>
    </div>
  );
};

export default NotificationToast;
