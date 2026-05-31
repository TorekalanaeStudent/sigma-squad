import apiClient from './axios';

export interface ComputerStats {
  totalComputers: number;
  availableComputers: number;
  reservedComputers: number;
  inUseComputers: number;
  outOfServiceComputers: number;
}

export interface Computer {
  id: number;
  computerNumber: number;
  status: 'AVAILABLE' | 'RESERVED' | 'IN_USE' | 'OUT_OF_SERVICE';
  currentUserId?: number;
}

export interface Reservation {
  id: number;
  userId: number;
  computerId: number;
  status: 'ACTIVE' | 'EXPIRED' | 'CANCELLED' | 'CONFIRMED';
  reservedAt: string;
  expiresAt: string;
  userName: string;
}

export interface Session {
  id: number;
  userId: number;
  computerId: number;
  startTime: string;
  endTime: string;
  status: 'ACTIVE' | 'ENDED';
  minutesRemaining?: number;
  userName?: string;
  computerNumber?: string;
}

export interface ChatMessage {
  id: string;
  sender: 'user' | 'bot';
  message: string;
  timestamp: Date;
}

export interface ChatBotResponse {
  reply: string;
  remainingMessages: number;
}

export interface ExtensionRequest {
  id: number;
  sessionId: number;
  userId: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'EXPIRED';
  requestedAt: string;
  respondedAt?: string;
  expiresAt?: string;
}

export interface AuditLogDTO {
  id: number;
  userId: number;
  reservationId: number;
  action: string;
  timestamp: string;
  details?: string;
}

class StatsService {
  async getComputerStats(): Promise<ComputerStats> {
    try {
      const response = await apiClient.get<ComputerStats>('/stats/computer-availability');
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch computer stats',
        status: error.response?.status,
      };
    }
  }
}

class ComputerService {
  async getAllComputers(): Promise<Computer[]> {
    try {
      const response = await apiClient.get<Computer[]>('/computers');
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch computers',
        status: error.response?.status,
      };
    }
  }

  async getComputerById(id: number): Promise<Computer> {
    try {
      const response = await apiClient.get<Computer>(`/computers/${id}`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch computer',
        status: error.response?.status,
      };
    }
  }
}

class ReservationService {
  async createReservation(computerId: number): Promise<Reservation> {
    try {
      const response = await apiClient.post<Reservation>('/reservations', { computerId });
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to create reservation',
        status: error.response?.status,
      };
    }
  }

  async getReservation(id: number): Promise<Reservation> {
    try {
      const response = await apiClient.get<Reservation>(`/reservations/${id}`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch reservation',
        status: error.response?.status,
      };
    }
  }

  async getAllReservations(): Promise<Reservation[]> {
    try {
      const response = await apiClient.get<Reservation[]>('/reservations');
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch reservations',
        status: error.response?.status,
      };
    }
  }

  async getReservationHistory(): Promise<Reservation[]> {
    try {
      const response = await apiClient.get<Reservation[]>('/reservations/history');
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch reservation history',
        status: error.response?.status,
      };
    }
  }

  async getUserReservations(userId: number): Promise<Reservation[]> {
    try {
      const response = await apiClient.get<Reservation[]>(`/reservations/user/${userId}`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch user reservations',
        status: error.response?.status,
      };
    }
  }

  async cancelReservation(id: number): Promise<void> {
    try {
      await apiClient.post(`/reservations/${id}/cancel`);
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to cancel reservation',
        status: error.response?.status,
      };
    }
  }

  async confirmReservation(id: number): Promise<Reservation> {
    try {
      const response = await apiClient.post<Reservation>(`/reservations/${id}/confirm`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to confirm reservation',
        status: error.response?.status,
      };
    }
  }

  async updateReservationExpiry(id: number, expiresAtSeconds: number): Promise<Reservation> {
    try {
      const response = await apiClient.put<Reservation>(`/reservations/${id}/update-expiry?expiresAtSeconds=${expiresAtSeconds}`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to update reservation expiry',
        status: error.response?.status,
      };
    }
  }
}

class ChatBotService {
  async sendMessage(message: string): Promise<ChatBotResponse> {
    try {
      const response = await apiClient.post<ChatBotResponse>('/chatbot/send', { message });
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to send message to chatbot',
        status: error.response?.status,
      };
    }
  }
}

class ExtensionService {
  async requestExtension(sessionId: number): Promise<ExtensionRequest> {
    try {
      const response = await apiClient.post<ExtensionRequest>(`/extensions/session/${sessionId}`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to request extension',
        status: error.response?.status,
      };
    }
  }

  async getUserExtensionRequests(userId: number): Promise<ExtensionRequest[]> {
    try {
      const response = await apiClient.get<ExtensionRequest[]>(`/extensions/user/${userId}`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch user extension requests',
        status: error.response?.status,
      };
    }
  }

  async getPendingExtensionRequests(): Promise<ExtensionRequest[]> {
    try {
      const response = await apiClient.get<ExtensionRequest[]>('/extensions/pending');
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch pending extension requests',
        status: error.response?.status,
      };
    }
  }

  async approveExtensionRequest(id: number): Promise<ExtensionRequest> {
    try {
      const response = await apiClient.post<ExtensionRequest>(`/extensions/${id}/approve`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to approve extension request',
        status: error.response?.status,
      };
    }
  }

  async rejectExtensionRequest(id: number): Promise<ExtensionRequest> {
    try {
      const response = await apiClient.post<ExtensionRequest>(`/extensions/${id}/reject`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to reject extension request',
        status: error.response?.status,
      };
    }
  }
}

export interface Notification {
  id: number;
  extensionRequestId?: number;
  reservationId?: number;
  adminId: number;
  title: string;
  message: string;
  type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS';
  isRead: boolean;
  createdAt: string;
}

class SessionService {
  async getUserActiveSession(userId: number): Promise<Session | null> {
    try {
      const response = await apiClient.get<Session>(`/sessions/user/${userId}/active`);
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 204) {
        return null;
      }
      throw {
        message: error.response?.data?.message || 'Failed to fetch active session',
        status: error.response?.status,
      };
    }
  }

  async getSession(id: number): Promise<Session> {
    try {
      const response = await apiClient.get<Session>(`/sessions/${id}`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch session',
        status: error.response?.status,
      };
    }
  }

  async endSession(id: number): Promise<Session> {
    try {
      const response = await apiClient.post<Session>(`/sessions/${id}/end`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to end session',
        status: error.response?.status,
      };
    }
  }

  async getAllActiveSessions(): Promise<Session[]> {
    try {
      const response = await apiClient.get<Session[]>(`/sessions`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch active sessions',
        status: error.response?.status,
      };
    }
  }

  async removeUserFromSession(sessionId: number): Promise<Session> {
    try {
      const response = await apiClient.post<Session>(`/sessions/${sessionId}/remove-user`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to remove user from session',
        status: error.response?.status,
      };
    }
  }
}

class NotificationService {
  async getUnreadNotifications(adminId: number): Promise<Notification[]> {
    try {
      const response = await apiClient.get<Notification[]>(`/notifications/admin/${adminId}/unread`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch unread notifications',
        status: error.response?.status,
      };
    }
  }

  async getAllNotifications(adminId: number): Promise<Notification[]> {
    try {
      const response = await apiClient.get<Notification[]>(`/notifications/admin/${adminId}/all`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch notifications',
        status: error.response?.status,
      };
    }
  }

  async getUnreadCount(adminId: number): Promise<number> {
    try {
      const response = await apiClient.get<number>(`/notifications/admin/${adminId}/unread-count`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch unread count',
        status: error.response?.status,
      };
    }
  }

  async markAsRead(notificationId: number): Promise<void> {
    try {
      await apiClient.post(`/notifications/${notificationId}/read`);
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to mark notification as read',
        status: error.response?.status,
      };
    }
  }

  async markMultipleAsRead(notificationIds: number[]): Promise<void> {
    try {
      await apiClient.post(`/notifications/mark-all-read`, notificationIds);
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to mark notifications as read',
        status: error.response?.status,
      };
    }
  }
}

class HistoryService {
  async getUserHistory(userId: number): Promise<AuditLogDTO[]> {
    try {
      const response = await apiClient.get<AuditLogDTO[]>(`/history/user/${userId}`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch user history',
        status: error.response?.status,
      };
    }
  }

  async getReservationHistory(reservationId: number): Promise<AuditLogDTO[]> {
    try {
      const response = await apiClient.get<AuditLogDTO[]>(`/history/reservation/${reservationId}`);
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Failed to fetch reservation history',
        status: error.response?.status,
      };
    }
  }
}

export default {
  stats: new StatsService(),
  computers: new ComputerService(),
  reservations: new ReservationService(),
  chatbot: new ChatBotService(),
  extensions: new ExtensionService(),
  sessions: new SessionService(),
  notifications: new NotificationService(),
  history: new HistoryService(),
};


