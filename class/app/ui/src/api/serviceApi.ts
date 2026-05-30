import apiClient from './axios';

export interface ComputerStats {
  totalComputers: number;
  availableComputers: number;
  reservedComputers: number;
  inUseComputers: number;
  outOfServiceComputers: number;
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

export default {
  stats: new StatsService(),
  chatbot: new ChatBotService(),
};

