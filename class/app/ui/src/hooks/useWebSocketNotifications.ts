import { useEffect, useState, useCallback } from 'react';
import serviceApi from '../api/serviceApi';
import AuthService from '../api/authApi';

export interface Notification {
  id: number;
  extensionRequestId?: number;
  reservationId?: number;
  adminId: number;
  title: string;
  message: string;
  type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS';
  isRead: boolean;
  createdAt: number;
}

export const useWebSocketNotifications = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [isConnected, setIsConnected] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const addNotification = useCallback((notification: Notification) => {
    setNotifications((prev) => [notification, ...prev.slice(0, 9)]);
  }, []);

  const clearNotification = useCallback((index: number) => {
    setNotifications((prev) => prev.filter((_, i) => i !== index));
  }, []);

  const clearAllNotifications = useCallback(() => {
    setNotifications([]);
  }, []);

  // Poll for notifications every 3 seconds
  useEffect(() => {
    setIsConnected(true);
    setError(null);

    const user = AuthService.getCurrentUser();
    if (!user || !user.id) {
      return;
    }

    const pollForNotifications = async () => {
      try {
        const unreadNotifications = await serviceApi.notifications.getUnreadNotifications(user.id);
        
        // Map API notifications to local format with timestamp as number
        const mappedNotifications = unreadNotifications.map((notif: any) => ({
          ...notif,
          createdAt: new Date(notif.createdAt).getTime(),
        }));
        
        setNotifications(mappedNotifications);
        setError(null);
      } catch (err: any) {
        console.error('Error polling notifications:', err);
        setError(err.message || 'Failed to fetch notifications');
      }
    };

    // Initial poll
    pollForNotifications();

    // Poll every 3 seconds
    const pollInterval = setInterval(pollForNotifications, 3000);

    return () => clearInterval(pollInterval);
  }, []);

  return {
    notifications,
    isConnected,
    error,
    addNotification,
    clearNotification,
    clearAllNotifications,
  };
};
