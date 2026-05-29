import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import RegisterForm from '../components/RegisterForm';

// Mock the useAuth hook
vi.mock('../hooks/useAuth', () => ({
  useAuth: () => ({
    login: vi.fn(),
    register: vi.fn().mockResolvedValue({}),
    isLoading: false,
    error: null,
    logout: vi.fn(),
    isAuthenticated: false,
  }),
}));

describe('RegisterForm Component', () => {
  const mockSwitchToLogin = vi.fn();
  const mockRegisterSuccess = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should render register form with all required fields', () => {
    render(
      <RegisterForm 
        onSwitchToLogin={mockSwitchToLogin}
        onRegisterSuccess={mockRegisterSuccess}
      />
    );

    expect(screen.getByPlaceholderText('Full name')).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/student id/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/email/i)).toBeInTheDocument();
    expect(screen.getAllByPlaceholderText('Password')).toHaveLength(2);
  });

  it('should validate student ID format', async () => {
    render(
      <RegisterForm 
        onSwitchToLogin={mockSwitchToLogin}
        onRegisterSuccess={mockRegisterSuccess}
      />
    );

    const nameInput = screen.getByPlaceholderText('Full name');
    const studentIdInput = screen.getByPlaceholderText(/student id/i);
    const emailInput = screen.getByPlaceholderText(/email/i);
    const passwordInputs = screen.getAllByPlaceholderText('Password');

    fireEvent.change(nameInput, { target: { value: 'John Doe' } });
    fireEvent.change(studentIdInput, { target: { value: 'invalid' } });
    fireEvent.change(emailInput, { target: { value: 'john@students.nu-laguna.edu.ph' } });
    fireEvent.change(passwordInputs[0], { target: { value: 'password123' } });
    fireEvent.change(passwordInputs[1], { target: { value: 'password123' } });

    fireEvent.click(screen.getByRole('button', { name: /sign up/i }));

    await waitFor(() => {
      expect(screen.getByText(/invalid student id/i)).toBeInTheDocument();
    });
  });

  it('should validate email domain', async () => {
    render(
      <RegisterForm 
        onSwitchToLogin={mockSwitchToLogin}
        onRegisterSuccess={mockRegisterSuccess}
      />
    );

    const nameInput = screen.getByPlaceholderText('Full name');
    const studentIdInput = screen.getByPlaceholderText(/student id/i);
    const emailInput = screen.getByPlaceholderText(/email/i);
    const passwordInputs = screen.getAllByPlaceholderText('Password');

    fireEvent.change(nameInput, { target: { value: 'John Doe' } });
    fireEvent.change(studentIdInput, { target: { value: '2025-12345' } });
    fireEvent.change(emailInput, { target: { value: 'john@gmail.com' } });
    fireEvent.change(passwordInputs[0], { target: { value: 'password123' } });
    fireEvent.change(passwordInputs[1], { target: { value: 'password123' } });

    fireEvent.click(screen.getByRole('button', { name: /sign up/i }));

    await waitFor(() => {
      expect(screen.getByText(/email must end with @students.nu-laguna.edu.ph/i)).toBeInTheDocument();
    });
  });

  it('should validate password match', async () => {
    render(
      <RegisterForm 
        onSwitchToLogin={mockSwitchToLogin}
        onRegisterSuccess={mockRegisterSuccess}
      />
    );

    const nameInput = screen.getByPlaceholderText('Full name');
    const studentIdInput = screen.getByPlaceholderText(/student id/i);
    const emailInput = screen.getByPlaceholderText(/email/i);
    const passwordInputs = screen.getAllByPlaceholderText('Password');

    fireEvent.change(nameInput, { target: { value: 'John Doe' } });
    fireEvent.change(studentIdInput, { target: { value: '2025-12345' } });
    fireEvent.change(emailInput, { target: { value: 'john@students.nu-laguna.edu.ph' } });
    fireEvent.change(passwordInputs[0], { target: { value: 'password123' } });
    fireEvent.change(passwordInputs[1], { target: { value: 'password456' } });

    fireEvent.click(screen.getByRole('button', { name: /sign up/i }));

    await waitFor(() => {
      expect(screen.getByText(/passwords do not match/i)).toBeInTheDocument();
    });
  });

  it('should switch to login form when button is clicked', () => {
    render(
      <RegisterForm 
        onSwitchToLogin={mockSwitchToLogin}
        onRegisterSuccess={mockRegisterSuccess}
      />
    );

    const switchButton = screen.getByRole('button', { name: /already have an account/i });
    fireEvent.click(switchButton);

    expect(mockSwitchToLogin).toHaveBeenCalled();
  });
});
