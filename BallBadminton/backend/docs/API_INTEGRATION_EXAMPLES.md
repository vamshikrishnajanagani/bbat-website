# API Integration Examples

## Table of Contents

1. [React Integration](#react-integration)
2. [Angular Integration](#angular-integration)
3. [Vue.js Integration](#vuejs-integration)
4. [Node.js Backend Integration](#nodejs-backend-integration)
5. [Mobile App Integration](#mobile-app-integration)
6. [Webhook Integration](#webhook-integration)
7. [Batch Operations](#batch-operations)
8. [Real-time Updates](#real-time-updates)

## React Integration

### Setup API Client with Axios

```typescript
// src/api/client.ts
import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';

class APIClient {
  private client: AxiosInstance;
  private accessToken: string | null = null;
  private refreshToken: string | null = null;

  constructor(baseURL: string = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1') {
    this.client = axios.create({
      baseURL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        if (this.accessToken) {
          config.headers.Authorization = `Bearer ${this.accessToken}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor to handle token refresh
    this.client.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            const newToken = await this.refreshAccessToken();
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            return this.client(originalRequest);
          } catch (refreshError) {
            // Redirect to login
            window.location.href = '/login';
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      }
    );
  }

  async login(usernameOrEmail: string, password: string) {
    const response = await this.client.post('/auth/login', {
      usernameOrEmail,
      password,
    });
    this.accessToken = response.data.accessToken;
    this.refreshToken = response.data.refreshToken;
    localStorage.setItem('accessToken', this.accessToken);
    localStorage.setItem('refreshToken', this.refreshToken);
    return response.data;
  }

  async refreshAccessToken() {
    if (!this.refreshToken) {
      throw new Error('No refresh token available');
    }
    const response = await this.client.post('/auth/refresh', {
      refreshToken: this.refreshToken,
    });
    this.accessToken = response.data.accessToken;
    localStorage.setItem('accessToken', this.accessToken);
    return this.accessToken;
  }

  async logout() {
    if (this.refreshToken) {
      await this.client.post('/auth/logout', {
        refreshToken: this.refreshToken,
      });
    }
    this.accessToken = null;
    this.refreshToken = null;
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  // Member API
  async getMembers() {
    const response = await this.client.get('/members');
    return response.data;
  }

  async getMemberById(id: string) {
    const response = await this.client.get(`/members/${id}`);
    return response.data;
  }

  async createMember(data: any) {
    const response = await this.client.post('/members', data);
    return response.data;
  }

  // Player API
  async getPlayers(params?: { page?: number; size?: number; sortBy?: string; sortDir?: string }) {
    const response = await this.client.get('/players/paginated', { params });
    return response.data;
  }

  async searchPlayers(query: string, page: number = 0, size: number = 20) {
    const response = await this.client.get('/players/search', {
      params: { q: query, page, size },
    });
    return response.data;
  }

  // Tournament API
  async getTournaments() {
    const response = await this.client.get('/tournaments');
    return response.data;
  }

  async registerForTournament(tournamentId: string, data: any) {
    const response = await this.client.post(`/tournaments/${tournamentId}/register`, data);
    return response.data;
  }
}

export const apiClient = new APIClient();
```

### React Hooks for API Integration

```typescript
// src/hooks/useMembers.ts
import { useState, useEffect } from 'react';
import { apiClient } from '../api/client';

export interface Member {
  id: string;
  name: string;
  position: string;
  email: string;
  phone: string;
  photoUrl?: string;
  hierarchyLevel: number;
}

export function useMembers() {
  const [members, setMembers] = useState<Member[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    async function fetchMembers() {
      try {
        setLoading(true);
        const data = await apiClient.getMembers();
        setMembers(data);
        setError(null);
      } catch (err) {
        setError(err as Error);
      } finally {
        setLoading(false);
      }
    }

    fetchMembers();
  }, []);

  const refetch = async () => {
    try {
      setLoading(true);
      const data = await apiClient.getMembers();
      setMembers(data);
      setError(null);
    } catch (err) {
      setError(err as Error);
    } finally {
      setLoading(fa