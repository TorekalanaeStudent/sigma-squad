import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import Logo from '../components/Logo';
import serviceApi from '../api/serviceApi';
import styles from '../styles/authForm.module.css';

const ResetPasswordPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token');

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!token) {
      setError('Invalid reset link. Redirecting to login...');
      setTimeout(() => navigate('/'), 3000);
    }
  }, [token, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!newPassword || !confirmPassword) {
      setError('Please fill in all fields');
      return;
    }

    if (newPassword.length < 8) {
      setError('Password must be at least 8 characters');
      return;
    }

    if (newPassword !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setIsLoading(true);
    try {
      await serviceApi.auth.resetPassword({
        token: token || '',
        newPassword,
        confirmPassword,
      });
      setSuccess('Password reset successfully! Redirecting to login...');
      setTimeout(() => navigate('/'), 2000);
    } catch (err: any) {
      setError(err.message || 'Failed to reset password');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles['homepage']}>
      <div className={styles['left-section']}>
        <div className={styles['brand-container']}>
          <Logo size="large" />
          <div className={styles['tagline']}>
            <h2>Reset Your Password</h2>
            <p>Computer Library Access System</p>
          </div>
        </div>
      </div>

      <div className={styles['right-section']}>
        <div className={styles['form-container']}>
          <form className={styles['auth-form']} onSubmit={handleSubmit}>
            <h2 className={styles['form-title']}>Set New Password</h2>

            {error && <div className={styles['error-message']}>{error}</div>}
            {success && <div className={styles['success-message']}>{success}</div>}

            {token && (
              <>
                <div className={styles['form-group']}>
                  <input
                    type="password"
                    placeholder="New Password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    disabled={isLoading || !!success}
                    className={styles['form-input']}
                  />
                </div>

                <div className={styles['form-group']}>
                  <input
                    type="password"
                    placeholder="Confirm Password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    disabled={isLoading || !!success}
                    className={styles['form-input']}
                  />
                </div>

                <button type="submit" disabled={isLoading || !!success} className={styles['btn-primary']}>
                  {isLoading ? 'Resetting...' : 'Reset Password'}
                </button>

                <button type="button" onClick={() => navigate('/')} className={styles['btn-secondary']}>
                  Back to Login
                </button>
              </>
            )}
          </form>
        </div>

        <footer className={styles['footer']}>
          <p>© 2026 Sigma Squad. All rights reserved.</p>
        </footer>
      </div>
    </div>
  );
};

export default ResetPasswordPage;
