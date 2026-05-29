import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import styles from '../styles/authForm.module.css';

interface RegisterFormProps {
  onSwitchToLogin: () => void;
  onRegisterSuccess: () => void;
}

export const RegisterForm: React.FC<RegisterFormProps> = ({ onSwitchToLogin, onRegisterSuccess }) => {
  const { register, isLoading, error } = useAuth();
  const [name, setName] = useState('');
  const [studentId, setStudentId] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [localError, setLocalError] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLocalError('');

    // Validation
    if (!name || !studentId || !email || !password || !confirmPassword) {
      setLocalError('Please fill in all fields');
      return;
    }

    if (name.trim().length < 2) {
      setLocalError('Name must be at least 2 characters');
      return;
    }

    // StudentId format: YYYY-[5-7 digits]
    if (!/^\d{4}-\d{5,7}$/.test(studentId.trim())) {
      setLocalError('Invalid student ID. \n Format: YYYY-[5-7 digits]');
      return;
    }

    if (!email.endsWith('@students.nu-laguna.edu.ph')) {
      setLocalError('Email must end with @students.nu-laguna.edu.ph');
      return;
    }

    if (password.length < 6) {
      setLocalError('Password must be at least 6 characters');
      return;
    }

    if (password !== confirmPassword) {
      setLocalError('Passwords do not match');
      return;
    }

    try {
      await register({ name, studentId, email, password });
      onRegisterSuccess();
    } catch (err: any) {
      setLocalError(err.message || 'Registration failed');
    }
  };

  const displayError = localError || error;

  return (
    <form className={styles['auth-form']} onSubmit={handleSubmit}>
      <h2 className={styles['form-title']}>Create CLASS Account</h2>
      <p className={styles['form-subtitle']}>Join our library computer system</p>

      {displayError && <div className={styles['error-message']}>{displayError}</div>}

      <div className={styles['form-group']}>
        <input
          type="text"
          placeholder="Full Name (e.g., Reyes, John Doe L.)"
          value={name}
          onChange={(e) => setName(e.target.value)}
          disabled={isLoading}
          className={styles['form-input']}
        />
      </div>

      <div className={styles['form-group']}>
        <input
          type="text"
          placeholder="School ID (e.g., 2026-12345)"
          value={studentId}
          onChange={(e) => setStudentId(e.target.value)}
          disabled={isLoading}
          className={styles['form-input']}
        />
      </div>

      <div className={styles['form-group']}>
        <input
          type="email"
          placeholder="Email (@students.nu-laguna.edu.ph)"
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

      <div className={styles['form-group']}>
        <input
          type="password"
          placeholder="Confirm password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          disabled={isLoading}
          className={styles['form-input']}
        />
      </div>

      <button type="submit" disabled={isLoading} className={styles['btn-primary']}>
        {isLoading ? 'Creating Account...' : 'Sign Up'}
      </button>

      <div className={styles['divider']}>or</div>

      <button
        type="button"
        onClick={onSwitchToLogin}
        disabled={isLoading}
        className={styles['btn-secondary']}
      >
        Already Have an Account?
      </button>
    </form>
  );
};

export default RegisterForm;
