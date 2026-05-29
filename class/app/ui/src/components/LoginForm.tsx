import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import styles from '../styles/authForm.module.css';

interface LoginFormProps {
  onSwitchToRegister: () => void;
  onLoginSuccess: () => void;
}

export const LoginForm: React.FC<LoginFormProps> = ({ onSwitchToRegister, onLoginSuccess }) => {
  const { login, isLoading, error } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [localError, setLocalError] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLocalError('');

    // Validation
    if (!email || !password) {
      setLocalError('Please fill in all fields');
      return;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setLocalError('Please enter a valid email');
      return;
    }

    try {
      await login({ email, password });
      onLoginSuccess();
    } catch (err: any) {
      setLocalError(err.message || 'Login failed');
    }
  };

  const displayError = localError || error;

  return (
    <form className={styles['auth-form']} onSubmit={handleSubmit}>
      <h2 className={styles['form-title']}>Log in to CLASS</h2>

      {displayError && <div className={styles['error-message']}>{displayError}</div>}

      <div className={styles['form-group']}>
        <input
          type="email"
          placeholder="Email address"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          disabled={isLoading}
          className={styles['form-input']}
        />
      </div>

      <div className={styles['form-group']}>
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          disabled={isLoading}
          className={styles['form-input']}
        />
      </div>

      <button type="submit" disabled={isLoading} className={styles['btn-primary']}>
        {isLoading ? 'Logging in...' : 'Log in'}
      </button>

      <div className={styles['divider']}>or</div>

      <button
        type="button"
        onClick={onSwitchToRegister}
        disabled={isLoading}
        className={styles['btn-secondary']}
      >
        Create New Account
      </button>

      <a href="#" className={styles['forgot-password']}>
        Forgotten password?
      </a>
    </form>
  );
};

export default LoginForm;
