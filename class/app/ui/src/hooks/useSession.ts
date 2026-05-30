import { useState, useEffect, useCallback } from 'react';
import apiClient from "../api/axios";

export interface Session {
  id: number;
  userId: number;
  computerId: number;
  startTime: string;
  endTime: string;
  status: string;
  minutesRemaining: number;
}

export const useSession = () => {
  const [session, setSession] = useState<Session | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [timeRemaining, setTimeRemaining] = useState(0);

  // Fetch active session
  const fetchActiveSession = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await apiClient.get<Session>('/sessions/user/active');
      setSession(response.data);
      // Convert minutes to seconds for countdown
      setTimeRemaining(response.data.minutesRemaining * 60);
    } catch (err: any) {
      if (err.response?.status !== 204) {
        setError(err.response?.data?.message || 'Failed to fetch session');
      }
      setSession(null);
    } finally {
      setLoading(false);
    }
  }, []);

  // End session early
  const endSessionEarly = useCallback(async (sessionId: number) => {
    try {
      const response = await apiClient.post<Session>(`/sessions/${sessionId}/end-early`);
      setSession(response.data);
      return { success: true, data: response.data };
    } catch (err: any) {
      const message = err.response?.data?.message || 'Failed to end session';
      setError(message);
      return { success: false, error: message };
    }
  }, []);

  // Request extension (admin approval required)
  const requestExtension = useCallback(async (sessionId: number) => {
    try {
      const response = await apiClient.post<any>(`/extensions/session/${sessionId}`);
      return { success: true, data: response.data };
    } catch (err: any) {
      const message = err.response?.data?.message || 'Failed to request extension';
      setError(message);
      return { success: false, error: message };
    }
  }, []);

  // Extend session (direct - used when admin approves)
  const extendSession = useCallback(async (sessionId: number, durationMinutes: number = 60) => {
    try {
      const response = await apiClient.post<Session>(`/sessions/${sessionId}/extend`, {
        durationMinutes,
      });
      setSession(response.data);
      // Convert minutes to seconds for countdown
      setTimeRemaining(response.data.minutesRemaining * 60);
      return { success: true, data: response.data };
    } catch (err: any) {
      const message = err.response?.data?.message || 'Failed to extend session';
      setError(message);
      return { success: false, error: message };
    }
  }, []);

  // Auto-update time remaining
  useEffect(() => {
    if (!session || session.status !== 'ACTIVE') return;

    const interval = setInterval(() => {
      setTimeRemaining((prev) => Math.max(0, prev - 1));
    }, 1000);

    return () => clearInterval(interval);
  }, [session]);

  // Format seconds to MM:SS display
  const formatTime = (seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return {
    session,
    loading,
    error,
    timeRemaining,
    fetchActiveSession,
    endSessionEarly,
    requestExtension,
    extendSession,
    formatTime,
  };
};
