import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { store } from '../store';
import { logout } from '../store/slices/authSlice';
import { addNotification } from '../store/slices/uiSlice';

// API Configuration
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api/v1';

// Cache configuration
const CACHE_DURATION = {
  short: 5 * 60 * 1000, // 5 minutes
  medium: 15 * 60 * 1000, // 15 minutes
  long: 60 * 60 * 1000, // 1 hour
};

// Simple in-memory cache
const cache = new Map<string, { data: any; timestamp: number; duration: number }>();

// Create axios instance
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const state = store.getState();
    const token = state.auth.token;
    
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Add cache control headers for GET requests
    if (config.method === 'get') {
      config.headers['Cache-Control'] = 'public, max-age=3600';
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    // Cache GET responses if cache headers are present
    if (response.config.method === 'get') {
      const cacheControl = response.headers['cache-control'];
      if (cacheControl && cacheControl.includes('max-age')) {
        const maxAge = parseInt(cacheControl.match(/max-age=(\d+)/)?.[1] || '0', 10);
        if (maxAge > 0) {
          const cacheKey = `${response.config.url}?${JSON.stringify(response.config.params || {})}`;
          cache.set(cacheKey, {
            data: response.data,
            timestamp: Date.now(),
            duration: maxAge * 1000,
          });
        }
      }
    }
    
    return response;
  },
  (error) => {
    const { response } = error;
    
    if (response?.status === 401) {
      // Token expired or invalid
      store.dispatch(logout());
      store.dispatch(addNotification({
        type: 'error',
        message: 'Session expired. Please login again.',
      }));
    } else if (response?.status === 403) {
      store.dispatch(addNotification({
        type: 'error',
        message: 'You do not have permission to perform this action.',
      }));
    } else if (response?.status >= 500) {
      store.dispatch(addNotification({
        type: 'error',
        message: 'Server error. Please try again later.',
      }));
    } else if (response?.status === 404) {
      store.dispatch(addNotification({
        type: 'error',
        message: 'Resource not found.',
      }));
    } else if (error.code === 'ECONNABORTED') {
      store.dispatch(addNotification({
        type: 'error',
        message: 'Request timeout. Please check your connection.',
      }));
    } else if (!response) {
      store.dispatch(addNotification({
        type: 'error',
        message: 'Network error. Please check your connection.',
      }));
    }
    
    return Promise.reject(error);
  }
);

// API Response types
export interface ApiResponse<T = any> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T = any> {
  data: T[];
  pagination: {
    page: number;
    size: number;
    total: number;
    totalPages: number;
  };
}

/**
 * Check if cached data is still valid
 */
const isCacheValid = (cacheKey: string): boolean => {
  const cached = cache.get(cacheKey);
  if (!cached) return false;
  
  const age = Date.now() - cached.timestamp;
  return age < cached.duration;
};

/**
 * Get data from cache
 */
const getFromCache = <T = any>(cacheKey: string): T | null => {
  if (isCacheValid(cacheKey)) {
    return cache.get(cacheKey)?.data || null;
  }
  cache.delete(cacheKey);
  return null;
};

/**
 * Clear cache for a specific key or all cache
 */
export const clearCache = (key?: string): void => {
  if (key) {
    cache.delete(key);
  } else {
    cache.clear();
  }
};

// Generic API methods with caching support
export const api = {
  get: async <T = any>(
    url: string,
    config?: AxiosRequestConfig & { useCache?: boolean; cacheDuration?: number }
  ): Promise<AxiosResponse<ApiResponse<T>>> => {
    const { useCache = true, cacheDuration = CACHE_DURATION.medium, ...axiosConfig } = config || {};
    
    if (useCache) {
      const cacheKey = `${url}?${JSON.stringify(axiosConfig.params || {})}`;
      const cachedData = getFromCache<ApiResponse<T>>(cacheKey);
      
      if (cachedData) {
        return {
          data: cachedData,
          status: 200,
          statusText: 'OK',
          headers: {},
          config: axiosConfig as InternalAxiosRequestConfig,
        } as AxiosResponse<ApiResponse<T>>;
      }
    }
    
    return apiClient.get(url, axiosConfig);
  },
    
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResponse<T>>> => {
    // Clear relevant cache on POST
    clearCache();
    return apiClient.post(url, data, config);
  },
    
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResponse<T>>> => {
    // Clear relevant cache on PUT
    clearCache();
    return apiClient.put(url, data, config);
  },
    
  patch: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResponse<T>>> => {
    // Clear relevant cache on PATCH
    clearCache();
    return apiClient.patch(url, data, config);
  },
    
  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResponse<T>>> => {
    // Clear relevant cache on DELETE
    clearCache();
    return apiClient.delete(url, config);
  },
};

export { CACHE_DURATION };
export default apiClient;