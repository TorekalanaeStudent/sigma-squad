import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import LoginForm from '../components/LoginForm';
import * as authApi from '../api/authApi';

// Mock the auth API
vi.mock('../api/authApi', () => ({
  default: {
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
    getCurrentUser: vi.fn(),
    isAuthenticated: vi.fn(),
  },
}));

// Mock the useAuth hook
vi.mock('../hooks/useAuth', () => ({
  useAuth: () => ({
    login: vi.fn().mockResolvedValue({}),
    register: vi.fn(),
    isLoading: false,
    error: null,
    logout: vi.fn(),
    isAuthenticated: false,
  }),
}));

describe('LoginForm Component', () => {
  const mockSwitchToRegister = vi.fn();
  const mockLoginSuccess = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render login form with email and password fields', () => {
    render(
      <LoginForm 
        onSwitchToRegister={mockSwitchToRegister}
        onLoginSuccess={mockLoginSuccess}
      />
    );

    expect(screen.getByPlaceholderText('Email address')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /log in/i })).toBeInTheDocument();
  });

  it('should display error when fields are empty', async () => {
    render(
      <LoginForm 
        onSwitchToRegister={mockSwitchToRegister}
        onLoginSuccess={mockLoginSuccess}
      />
    );

    const submitButton = screen.getByRole('button', { name: /log in/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/please fill in all fields/i)).toBeInTheDocument();
    });
  });

  it('should display error for invalid email', async () => {
    render(
      <LoginForm 
        onSwitchToRegister={mockSwitchToRegister}
        onLoginSuccess={mockLoginSuccess}
      />
    );

    const emailInput = screen.getByPlaceholderText('Email address');
    const passwordInput = screen.getByPlaceholderText('Password');

    fireEvent.change(emailInput, { target: { value: 'invalid-email' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(screen.getByRole('button', { name: /log in/i }));

    await waitFor(() => {
      expect(screen.getByText(/please enter a valid email/i)).toBeInTheDocument();
    });
  });

  it('should switch to register form when button is clicked', () => {
    render(
      <LoginForm 
        onSwitchToRegister={mockSwitchToRegister}
        onLoginSuccess={mockLoginSuccess}
      />
    );

    const switchButton = screen.getByRole('button', { name: /create new account/i });
    fireEvent.click(switchButton);

    expect(mockSwitchToRegister).toHaveBeenCalled();
  });
});
