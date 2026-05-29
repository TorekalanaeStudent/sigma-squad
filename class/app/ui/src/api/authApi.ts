import apiClient from './axios';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  studentId: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: {
    id: number;
    name: string;
    email: string;
    studentId: string;
    isAdmin: boolean;
    createdAt: string;
  };
}

export interface ApiError {
  message: string;
  status?: number;
}

class AuthService {
  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await apiClient.post<AuthResponse>('/auth/login', data);
      if (response.data.token) {
        localStorage.setItem('jwt_token', response.data.token);
        localStorage.setItem('user', JSON.stringify(response.data.user));
      }
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Login failed. Please try again.',
        status: error.response?.status,
      } as ApiError;
    }
  }

  async register(data: RegisterRequest): Promise<AuthResponse> {
    try {
      const response = await apiClient.post<AuthResponse>('/auth/register', data);
      if (response.data.token) {
        localStorage.setItem('jwt_token', response.data.token);
        localStorage.setItem('user', JSON.stringify(response.data.user));
      }
      return response.data;
    } catch (error: any) {
      throw {
        message: error.response?.data?.message || 'Registration failed. Please try again.',
        status: error.response?.status,
      } as ApiError;
    }
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user');
  }

  getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('jwt_token');
  }
}

export default new AuthService();
