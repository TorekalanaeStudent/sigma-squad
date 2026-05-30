import React, { useState } from 'react';
import serviceApi from '../api/serviceApi';

interface ExtensionRequestButtonProps {
  sessionId: number;
  onSuccess?: () => void;
}

const ExtensionRequestButton: React.FC<ExtensionRequestButtonProps> = ({ sessionId, onSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleRequestExtension = async () => {
    try {
      setLoading(true);
      setError(null);
      await serviceApi.extensions.requestExtension(sessionId);
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
      if (onSuccess) onSuccess();
    } catch (err: any) {
      setError(err.message || 'Failed to request extension');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {error && (
        <div style={{ color: '#fca5a5', marginBottom: '0.5rem', fontSize: '0.9rem' }}>
          ⚠️ {error}
        </div>
      )}
      {success && (
        <div style={{ color: '#86efac', marginBottom: '0.5rem', fontSize: '0.9rem' }}>
          ✓ Extension requested! Awaiting admin approval.
        </div>
      )}
      <button
        onClick={handleRequestExtension}
        disabled={loading}
        style={{
          width: '100%',
          padding: '0.75rem',
          background: loading ? '#94a3b8' : 'linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%)',
          border: 'none',
          borderRadius: '8px',
          color: '#ffffff',
          fontWeight: '600',
          cursor: loading ? 'not-allowed' : 'pointer',
          transition: 'all 0.3s ease',
          fontSize: '0.9rem',
          opacity: loading ? 0.7 : 1,
        }}
      >
        {loading ? '⏳ Requesting...' : '⏱️ Request 1 Hour Extension'}
      </button>
    </div>
  );
};

export default ExtensionRequestButton;
