import { useEffect, useState, useCallback } from 'react';

export interface Notification {
  title: string;
  message: string;
  type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS';
  timestamp: number;
}

export const useWebSocketNotifications = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [webSocket, setWebSocket] = useState<WebSocket | null>(null);

  const addNotification = useCallback((notification: Notification) => {
    setNotifications((prev) => [notification, ...prev.slice(0, 9)]);
  }, []);

  const clearNotification = useCallback((index: number) => {
    setNotifications((prev) => prev.filter((_, i) => i !== index));
  }, []);

  const clearAllNotifications = useCallback(() => {
    setNotifications([]);
  }, []);

  const connect = useCallback(() => {
    try {
      // Get the backend URL - default to port 8080 if not specified
      const backendPort = import.meta.env.VITE_BACKEND_PORT || '8080';
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const wsUrl = `${protocol}//${window.location.hostname}:${backendPort}/ws/notifications`;

      console.log('Connecting to WebSocket:', wsUrl);

      // Use native WebSocket instead of SockJS to avoid browser compatibility issues
      const ws = new WebSocket(wsUrl);

      ws.onopen = () => {
        console.log('WebSocket connected');
        setIsConnected(true);
        setError(null);
      };

      ws.onmessage = (event) => {
        try {
          const notification = JSON.parse(event.data);
          addNotification({
            ...notification,
            timestamp: Date.now(),
          });
        } catch (err) {
          console.error('Failed to parse notification:', err);
        }
      };

      ws.onerror = (event) => {
        console.error('WebSocket error:', event);
        setIsConnected(false);
        setError('WebSocket connection error');
      };

      ws.onclose = () => {
        console.log('WebSocket disconnected');
        setIsConnected(false);
        
        // Attempt to reconnect after 3 seconds
        setTimeout(() => {
          connect();
        }, 3000);
      };

      setWebSocket(ws);

      return () => {
        if (ws && ws.readyState === WebSocket.OPEN) {
          ws.close();
        }
      };
    } catch (err) {
      console.error('WebSocket initialization error:', err);
      setIsConnected(false);
      setError('WebSocket initialization failed');

      // Retry connection after 3 seconds
      setTimeout(() => {
        connect();
      }, 3000);
    }
  }, []);

  useEffect(() => {
    connect();
  }, [connect]);

  return {
    notifications,
    isConnected,
    error,
    clearNotification,
    clearAllNotifications,
  };
};
