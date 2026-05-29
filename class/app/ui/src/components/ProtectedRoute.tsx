import React from 'react';
import { Navigate } from 'react-router-dom';
import AuthService from '../api/authApi';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: 'admin' | 'student';
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, requiredRole }) => {
  const user = AuthService.getCurrentUser();

  if (!AuthService.isAuthenticated()) {
    return <Navigate to="/" replace />;
  }

  if (requiredRole === 'admin' && !user?.isAdmin) {
    return <Navigate to="/dashboard" replace />;
  }

  if (requiredRole === 'student' && user?.isAdmin) {
    return <Navigate to="/admin-dashboard" replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
