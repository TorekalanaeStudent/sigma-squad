import React, { useState, useRef, useEffect } from 'react';
import serviceApi from '../api/serviceApi';
import styles from '../styles/authForm.module.css';

interface VerifyEmailFormProps {
  email: string;
  onClose: () => void;
  onSuccess: () => void;
}

const VerifyEmailForm: React.FC<VerifyEmailFormProps> = ({ email, onClose, onSuccess }) => {
  const [digits, setDigits] = useState(['', '', '', '', '', '']);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  const handleDigitChange = (index: number, value: string) => {
    const numValue = value.replace(/\D/g, '');
    
    if (numValue.length > 1) {
      return;
    }

    const newDigits = [...digits];
    newDigits[index] = numValue;
    setDigits(newDigits);

    if (numValue && index < 5) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !digits[index] && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent) => {
    e.preventDefault();
    const pastedData = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    const newDigits = [...digits];
    pastedData.split('').forEach((char, idx) => {
      newDigits[idx] = char;
    });
    setDigits(newDigits);
    
    if (pastedData.length === 6) {
      inputRefs.current[5]?.blur();
    } else {
      inputRefs.current[Math.min(pastedData.length, 5)]?.focus();
    }
  };

  const code = digits.join('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (code.length !== 6) {
      setError('Please enter all 6 digits');
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

  useEffect(() => {
    inputRefs.current[0]?.focus();
  }, []);

  return (
    <div className={styles['modal-overlay']} onClick={onClose}>
      <form className={styles['auth-form']} onClick={(e) => e.stopPropagation()} onSubmit={handleSubmit}>
        <button type="button" className={styles['close-btn']} onClick={onClose}>×</button>
        <h2 className={styles['form-title']}>Verify Email</h2>
        <p className={styles['form-subtitle']}>Enter the 6-digit code sent to {email}</p>

        {error && <div className={styles['error-message']}>{error}</div>}
        {success && <div className={styles['success-message']}>{success}</div>}

        <div className={styles['otp-container']}>
          {digits.map((digit, index) => (
            <input
              key={index}
              ref={(el) => {inputRefs.current[index] = el;}}
              type="text"
              inputMode="numeric"
              maxLength={1}
              value={digit}
              onChange={(e) => handleDigitChange(index, e.target.value)}
              onKeyDown={(e) => handleKeyDown(index, e)}
              onPaste={handlePaste}
              disabled={isLoading || !!success}
              className={styles['otp-input']}
            />
          ))}
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
