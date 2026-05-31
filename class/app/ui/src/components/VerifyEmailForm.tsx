import React, { useState } from 'react';
import serviceApi from '../api/serviceApi';
import styles from '../styles/authForm.module.css';

interface VerifyEmailFormProps {
  email: string;
  onClose: () => void;
  onSuccess: () => void;
}

const VerifyEmailForm: React.FC<VerifyEmailFormProps> = ({ email, onClose, onSuccess }) => {
  const [code, setCode] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!code) {
      setError('Please enter verification code');
      return;
    }

    setIsLoading(true);
    try {
      await serviceApi.auth.verifyEmail({ email, code });
      setSuccess('Email verified! Redirecting to login...');
      setTimeout(onSuccess, 2000);
    } catch (err: any) {
      setError(err.message || 'Failed to verify email');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles['modal-overlay']} onClick={onClose}>
      <form className={styles['auth-form']} onClick={(e) => e.stopPropagation()} onSubmit={handleSubmit}>
        <button type="button" className={styles['close-btn']} onClick={onClose}>×</button>
        <h2 className={styles['form-title']}>Verify Email</h2>
        <p className={styles['form-subtitle']}>Enter the 6-digit code sent to {email}</p>

        {error && <div className={styles['error-message']}>{error}</div>}
        {success && <div className={styles['success-message']}>{success}</div>}

        <div className={styles['form-group']}>
          <input
            type="text"
            placeholder="000000"
            maxLength={6}
            value={code}
            onChange={(e) => setCode(e.target.value.replace(/\D/g, ''))}
            disabled={isLoading || !!success}
            className={styles['form-input']}
            autoFocus
          />
        </div>

        <button type="submit" disabled={isLoading || !!success || code.length !== 6} className={styles['btn-primary']}>
          {isLoading ? 'Verifying...' : 'Verify'}
        </button>

        <button type="button" onClick={onClose} className={styles['btn-secondary']}>
          Cancel
        </button>
      </form>
    </div>
  );
};

export default VerifyEmailForm;
