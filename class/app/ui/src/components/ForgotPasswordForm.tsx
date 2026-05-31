import React, { useState } from 'react';
import serviceApi from '../api/serviceApi';
import styles from '../styles/authForm.module.css';

interface ForgotPasswordFormProps {
  onClose: () => void;
  onSuccess: () => void;
}

const ForgotPasswordForm: React.FC<ForgotPasswordFormProps> = ({ onClose, onSuccess }) => {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!email) {
      setError('Please enter your email');
      return;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setError('Please enter a valid email');
      return;
    }

    setIsLoading(true);
    try {
      await serviceApi.auth.requestPasswordReset({ email });
      setSuccess('Check your email for a password reset link');
      setTimeout(onSuccess, 2000);
    } catch (err: any) {
      setError(err.message || 'Failed to send reset email');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles['modal-overlay']} onClick={onClose}>
      <form className={styles['auth-form']} onClick={(e) => e.stopPropagation()} onSubmit={handleSubmit}>
        <button type="button" className={styles['close-btn']} onClick={onClose}>×</button>
        <h2 className={styles['form-title']}>Reset Password</h2>

        {error && <div className={styles['error-message']}>{error}</div>}
        {success && <div className={styles['success-message']}>{success}</div>}

        <div className={styles['form-group']}>
          <input
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            disabled={isLoading || !!success}
            className={styles['form-input']}
          />
        </div>

        <button type="submit" disabled={isLoading || !!success} className={styles['btn-primary']}>
          {isLoading ? 'Sending...' : 'Send Reset Link'}
        </button>

        <button type="button" onClick={onClose} className={styles['btn-secondary']}>
          Cancel
        </button>
      </form>
    </div>
  );
};

export default ForgotPasswordForm;
