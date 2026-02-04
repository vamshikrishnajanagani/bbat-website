# API Code Samples

## Table of Contents

1. [JavaScript/TypeScript Examples](#javascripttypescript-examples)
2. [Python Examples](#python-examples)
3. [Java Examples](#java-examples)
4. [cURL Examples](#curl-examples)
5. [Postman Collection](#postman-collection)

## JavaScript/TypeScript Examples

### Basic API Client

```typescript
// api-client.ts
import axios, { AxiosInstance } from 'axios';

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

class TelanganaAPIClient {
  private client: AxiosInstance;
  private accessToken: string | null = null;
  private refreshToken: string | null = null;

  constructor(baseURL: string = 'http://localhost:8080/api/v1') {
    this.client = axios.create({
      baseURL,
      timeout: 30000,
      headers: { 'Content-Type': 'application/json' }
    });

    // Request interceptor
    this.client.interceptors.request.use(
      (config) => {
        if (this.accessToken) {
          config.headers.Authorization = `Bearer ${this.accessToken}`;
        }
        return config;
      }
    );

    // Response interceptor for token refresh
    this.client.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;
        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;
          await this.refreshAccessToken();
          return this.client(originalRequest);
        }
        return Promise.reject(error);
      }
    );
  }

  async login(usernameOrEmail: string, password: string): Promise<LoginResponse> {
    const response = await this.client.post<LoginResponse>('/auth/login', {
      usernameOrEmail,
      password
    });
    this.accessToken = response.data.accessToken;
    this.refreshToken = response.data.refreshToken;
    return response.data;
  }

  async refreshAccessToken(): Promise<void> {
    if (!this.refreshToken) throw new Error('No refresh token');
    const response = await this.client.post<LoginResponse>('/auth/refresh', {
      refreshToken: this.refreshToken
    });
    this.accessToken = response.data.accessToken;
  }
}

export default TelanganaAPIClient;
```


### React Hooks Example

```typescript
// hooks/useMembers.ts
import { useState, useEffect } from 'react';
import { apiClient } from '../api/client';

export function useMembers() {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchMembers() {
      try {
        setLoading(true);
        const data = await apiClient.get('/members');
        setMembers(data);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    }
    fetchMembers();
  }, []);

  return { members, loading, error };
}
```

## Python Examples

### Python API Client

```python
import requests
from typing import Optional, Dict, Any
from datetime import datetime, timedelta

class TelanganaAPIClient:
    def __init__(self, base_url: str = 'http://localhost:8080/api/v1'):
        self.base_url = base_url
        self.access_token: Optional[str] = None
        self.refresh_token: Optional[str] = None
        self.token_expiry: Optional[datetime] = None

    def _get_headers(self) -> Dict[str, str]:
        headers = {'Content-Type': 'application/json'}
        if self.access_token:
            headers['Authorization'] = f'Bearer {self.access_token}'
        return headers

    def login(self, username_or_email: str, password: str) -> Dict[str, Any]:
        response = requests.post(
            f'{self.base_url}/auth/login',
            json={'usernameOrEmail': username_or_email, 'password': password}
        )
        response.raise_for_status()
        data = response.json()
        self.access_token = data['accessToken']
        self.refresh_token = data['refreshToken']
        self.token_expiry = datetime.now() + timedelta(seconds=data['expiresIn'])
        return data

    def get_members(self) -> list:
        response = requests.get(
            f'{self.base_url}/members',
            headers=self._get_headers()
        )
        response.raise_for_status()
        return response.json()
```

## Java Examples

### Java API Client

```java
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TelanganaAPIClient {
    private final String baseURL;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String accessToken;

    public TelanganaAPIClient(String baseURL) {
        this.baseURL = baseURL;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public LoginResponse login(String usernameOrEmail, String password) throws Exception {
        String requestBody = objectMapper.writeValueAsString(
            Map.of("usernameOrEmail", usernameOrEmail, "password", password)
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseURL + "/auth/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(request, 
            HttpResponse.BodyHandlers.ofString());

        LoginResponse loginResponse = objectMapper.readValue(
            response.body(), LoginResponse.class);
        this.accessToken = loginResponse.getAccessToken();
        return loginResponse;
    }
}
```

## cURL Examples

### Authentication

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin@example.com",
    "password": "password"
  }'

# Refresh Token
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### Member Operations

```bash
# Get all members
curl -X GET http://localhost:8080/api/v1/members \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# Create member
curl -X POST http://localhost:8080/api/v1/members \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "John Doe",
    "position": "Secretary",
    "email": "john@example.com",
    "phone": "+91-9876543210",
    "hierarchyLevel": 2
  }'
```

### Player Operations

```bash
# Search players
curl -X GET "http://localhost:8080/api/v1/players/search?q=john&page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# Get player achievements
curl -X GET http://localhost:8080/api/v1/players/{playerId}/achievements \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Tournament Operations

```bash
# Get upcoming tournaments
curl -X GET http://localhost:8080/api/v1/tournaments/upcoming \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# Register for tournament
curl -X POST http://localhost:8080/api/v1/tournaments/{tournamentId}/registrations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "playerId": "player-uuid",
    "categoryId": "category-uuid"
  }'
```

## Postman Collection

Import this collection into Postman for easy API testing:

```json
{
  "info": {
    "name": "Telangana Ball Badminton API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "auth": {
    "type": "bearer",
    "bearer": [{"key": "token", "value": "{{accessToken}}", "type": "string"}]
  },
  "variable": [
    {"key": "baseUrl", "value": "http://localhost:8080/api/v1"},
    {"key": "accessToken", "value": ""}
  ]
}
```

---

**Last Updated**: December 2023  
**Document Version**: 1.0.0
