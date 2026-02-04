import { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../store';
import { loginStart, loginSuccess, loginFailure, logout } from '../store/slices/authSlice';
import { authService, LoginRequest, RegisterRequest } from '../services/authService';

export const useAuth = () => {
  const dispatch = useAppDispatch();
  const { user, token, isAuthenticated, isLoading, error } = useAppSelector(state => state.auth);
  const [isInitialized, setIsInitialized] = useState(false);

  // Initialize auth state on app load
  useEffect(() => {
    const initializeAuth = async () => {
      const storedToken = localStorage.getItem('token');
      
      if (storedToken) {
        try {
          dispatch(loginStart());
          const userData = await authService.verifyToken();
          dispatch(loginSuccess({ user: userData, token: storedToken }));
        } catch (error) {
          dispatch(loginFailure('Session expired'));
          localStorage.removeItem('token');
        }
      }
      
      setIsInitialized(true);
    };

    initializeAuth();
  }, [dispatch]);

  const login = async (email: string, password: string) => {
    try {
      dispatch(loginStart());
      const response = await authService.login({ email, password });
      dispatch(loginSuccess(response));
      return { success: true };
    } catch (error: any) {
      const message = error.response?.data?.message || 'Login failed';
      dispatch(loginFailure(message));
      throw new Error(message);
    }
  };

  const register = async (userData: Omit<RegisterRequest, 'confirmPassword'>) => {
    try {
      dispatch(loginStart());
      const response = await authService.register({
        ...userData,
        confirmPassword: userData.password,
      });
      dispatch(loginSuccess(response));
      return { success: true };
    } catch (error: any) {
      const message = error.response?.data?.message || 'Registration failed';
      dispatch(loginFailure(message));
      throw new Error(message);
    }
  };

  const updateProfile = async (profileData: {
    name: string;
    email: string;
    phone?: string;
    currentPassword?: string;
    newPassword?: string;
  }) => {
    try {
      // This would be implemented when the backend API is ready
      // For now, we'll simulate the update
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Update user data in store
      const updatedUser = {
        ...user!,
        name: profileData.name,
        email: profileData.email,
        phone: profileData.phone,
      };
      
      dispatch(loginSuccess({ user: updatedUser, token: token! }));
      return { success: true };
    } catch (error: any) {
      const message = error.response?.data?.message || 'Profile update failed';
      throw new Error(message);
    }
  };

  const logoutUser = async () => {
    try {
      await authService.logout();
    } catch (error) {
      // Continue with logout even if API call fails
      console.error('Logout API call failed:', error);
    } finally {
      dispatch(logout());
    }
  };

  const refreshToken = async () => {
    try {
      const storedRefreshToken = localStorage.getItem('refreshToken');
      if (!storedRefreshToken) {
        throw new Error('No refresh token available');
      }

      const response = await authService.refreshToken(storedRefreshToken);
      dispatch(loginSuccess(response));
      return { success: true };
    } catch (error: any) {
      dispatch(logout());
      return { success: false, error: error.message };
    }
  };

  return {
    user,
    token,
    isAuthenticated,
    isLoading: isLoading || !isInitialized,
    error,
    login,
    register,
    updateProfile,
    logout: logoutUser,
    refreshToken,
  };
};