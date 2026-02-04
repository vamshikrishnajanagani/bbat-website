# Telangana Ball Badminton Association API - Developer Guide

## Table of Contents

1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
3. [Authentication](#authentication)
4. [API Endpoints Overview](#api-endpoints-overview)
5. [Request & Response Formats](#request--response-formats)
6. [Error Handling](#error-handling)
7. [Pagination & Filtering](#pagination--filtering)
8. [Rate Limiting](#rate-limiting)
9. [Caching](#caching)
10. [Code Examples](#code-examples)
11. [Best Practices](#best-practices)
12. [API Versioning](#api-versioning)
13. [Deprecation Policy](#deprecation-policy)

## Introduction

The Telangana Ball Badminton Association API is a RESTful API that provides comprehensive functionality for managing association members, players, tournaments, geographic information, news, and media content.

### Base URL

- **Development**: `http://localhost:8080/api/v1`
- **Production**: `https://api.telanganaballbadminton.org/api/v1`

### API Documentation

Interactive API documentation is available at:
- **Swagger UI**: `{BASE_URL}/swagger-ui.html`
- **OpenAPI Spec**: `{BASE_URL}/v3/api-docs`

## Getting Started

### Prerequisites

- Java 17 or higher
- PostgreSQL 14+ or MySQL 8+
- Redis (for caching)
- Valid API credentials

### Quick Start

1. **Obtain API Credentials**: Contact the administrator to get your API credentials.

2. **Authenticate**: Use the `/auth/login` endpoint to obtain a JWT token.

3. **Make API Calls**: Include the JWT token in the `Authorization` header for protected endpoints.

### Example: First API Call

```bash
# 1. Login to get JWT token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin@example.com",
    "password": "your-password"
  }'

# Response:
# {
#   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "tokenType": "Bearer",
#   "expiresIn": 3600
# }

# 2. Use the token to access protected endpoints
curl -X GET http://localhost:8080/api/v1/members \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Authentication

### JWT Authentication

The API uses JWT (JSON Web Tokens) for authentication. Most endpoints require a valid JWT token in the `Authorization` header.

#### Login

```http
POST /auth/login
Content-Type: application/json

{
  "usernameOrEmail": "user@example.com",
  "password": "your-password"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "username": "admin",
    "email": "user@example.com",
    "roles": ["ADMIN"]
  }
}
```

#### Using the Token

Include the access token in the `Authorization` header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Token Refresh

When the access token expires, use the refresh token to obtain a new one:

```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Logout

```http
POST /auth/logout
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Authorization

The API uses role-based access control (RBAC). Different endpoints require different permissions:

- **Public**: No authentication required
- **USER**: Basic authenticated user
- **MODERATOR**: Content management permissions
- **ADMIN**: Full system access

## API Endpoints Overview

### Member Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/members` | Get all active members | No |
| GET | `/members/paginated` | Get members with pagination | No |
| GET | `/members/{id}` | Get member by ID | No |
| GET | `/members/prominent` | Get prominent members | No |
| POST | `/members` | Create new member | Yes (ADMIN) |
| PUT | `/members/{id}` | Update member | Yes (ADMIN) |
| DELETE | `/members/{id}` | Delete member | Yes (ADMIN) |
| POST | `/members/{id}/photo` | Upload member photo | Yes (ADMIN) |
| POST | `/members/contact` | Submit contact form | No |

### Player Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/players` | Get all active players | No |
| GET | `/players/paginated` | Get players with pagination | No |
| GET | `/players/{id}` | Get player by ID | No |
| GET | `/players/prominent` | Get prominent players | No |
| GET | `/players/category/{category}` | Get players by category | No |
| GET | `/players/district/{districtId}` | Get players by district | No |
| GET | `/players/search` | Search players | No |
| POST | `/players` | Create new player | Yes (MODERATOR) |
| PUT | `/players/{id}` | Update player | Yes (MODERATOR) |
| DELETE | `/players/{id}` | Delete player | Yes (ADMIN) |
| GET | `/players/{id}/achievements` | Get player achievements | No |
| POST | `/players/{id}/achievements` | Add achievement | Yes (MODERATOR) |

### Tournament Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/tournaments` | Get all tournaments | No |
| GET | `/tournaments/{id}` | Get tournament by ID | No |
| GET | `/tournaments/upcoming` | Get upcoming tournaments | No |
| GET | `/tournaments/status/{status}` | Get tournaments by status | No |
| POST | `/tournaments` | Create tournament | Yes (MODERATOR) |
| PUT | `/tournaments/{id}` | Update tournament | Yes (MODERATOR) |
| DELETE | `/tournaments/{id}` | Delete tournament | Yes (ADMIN) |
| POST | `/tournaments/{id}/register` | Register for tournament | Yes (USER) |
| GET | `/tournaments/{id}/registrations` | Get registrations | Yes (MODERATOR) |

### District Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/districts` | Get all districts | No |
| GET | `/districts/{id}` | Get district by ID | No |
| GET | `/districts/{id}/statistics` | Get district statistics | No |
| POST | `/districts` | Create district | Yes (ADMIN) |
| PUT | `/districts/{id}` | Update district | Yes (ADMIN) |

### News & Media

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/news` | Get all news articles | No |
| GET | `/news/{id}` | Get news article by ID | No |
| GET | `/news/category/{category}` | Get news by category | No |
| POST | `/news` | Create news article | Yes (MODERATOR) |
| PUT | `/news/{id}` | Update news article | Yes (MODERATOR) |
| DELETE | `/news/{id}` | Delete news article | Yes (ADMIN) |
| GET | `/media/galleries` | Get media galleries | No |
| POST | `/media/galleries` | Create gallery | Yes (MODERATOR) |
| POST | `/media/upload` | Upload media file | Yes (MODERATOR) |

## Request & Response Formats

### Content Type

All requests and responses use JSON format:

```http
Content-Type: application/json
```

### Request Body Example

```json
{
  "name": "John Doe",
  "position": "President",
  "email": "john.doe@example.com",
  "phone": "+91-9876543210",
  "biography": "Experienced sports administrator...",
  "hierarchyLevel": 1,
  "tenureStartDate": "2023-01-01",
  "tenureEndDate": "2025-12-31",
  "isProminent": true
}
```

### Response Format

#### Success Response

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "John Doe",
  "position": "President",
  "email": "john.doe@example.com",
  "phone": "+91-9876543210",
  "biography": "Experienced sports administrator...",
  "photoUrl": "https://cdn.example.com/photos/john-doe.jpg",
  "hierarchyLevel": 1,
  "tenureStartDate": "2023-01-01",
  "tenureEndDate": "2025-12-31",
  "isProminent": true,
  "isActive": true,
  "createdAt": "2023-01-01T10:00:00Z",
  "updatedAt": "2023-01-01T10:00:00Z"
}
```

#### Error Response

```json
{
  "error": true,
  "message": "Member not found",
  "timestamp": "2023-12-01T10:00:00Z",
  "path": "/api/v1/members/123",
  "status": 404
}
```

## Error Handling

### HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Request successful, no content to return |
| 400 | Bad Request - Invalid request data |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists |
| 422 | Unprocessable Entity - Validation error |
| 429 | Too Many Requests - Rate limit exceeded |
| 500 | Internal Server Error - Server error |

### Error Response Structure

```json
{
  "error": true,
  "message": "Validation failed",
  "timestamp": "2023-12-01T10:00:00Z",
  "path": "/api/v1/members",
  "status": 400,
  "errors": [
    {
      "field": "email",
      "message": "Email is required"
    },
    {
      "field": "name",
      "message": "Name must be at least 3 characters"
    }
  ]
}
```

## Pagination & Filtering

### Pagination Parameters

Most list endpoints support pagination:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | integer | 0 | Page number (0-based) |
| `size` | integer | 20 | Number of items per page |
| `sortBy` | string | varies | Field to sort by |
| `sortDir` | string | asc | Sort direction (asc/desc) |

### Example

```http
GET /api/v1/players/paginated?page=0&size=10&sortBy=name&sortDir=asc
```

### Paginated Response

```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "Player 1",
      ...
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalElements": 100,
  "totalPages": 10,
  "last": false,
  "first": true,
  "numberOfElements": 10,
  "size": 10,
  "number": 0,
  "empty": false
}
```

### Filtering

Use query parameters for filtering:

```http
GET /api/v1/players/filter?category=MEN&gender=MALE&isProminent=true
```

### Search

Use the `q` parameter for text search:

```http
GET /api/v1/players/search?q=john&page=0&size=10
```

## Rate Limiting

The API implements rate limiting to ensure fair usage:

- **Anonymous users**: 100 requests per hour
- **Authenticated users**: 1000 requests per hour
- **Admin users**: 5000 requests per hour

### Rate Limit Headers

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1638360000
```

### Rate Limit Exceeded

```json
{
  "error": true,
  "message": "Rate limit exceeded. Please try again later.",
  "timestamp": "2023-12-01T10:00:00Z",
  "status": 429,
  "retryAfter": 3600
}
```

## Caching

The API uses caching for improved performance:

### Cache Headers

```http
Cache-Control: public, max-age=300
ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"
Last-Modified: Wed, 01 Dec 2023 10:00:00 GMT
```

### Conditional Requests

Use `If-None-Match` or `If-Modified-Since` headers:

```http
GET /api/v1/members
If-None-Match: "33a64df551425fcc55e4d42a148795d9f25f89d4"
```

**Response (304 Not Modified):**
```http
HTTP/1.1 304 Not Modified
Cache-Control: public, max-age=300
ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"
```

## Code Examples

### JavaScript/TypeScript

```javascript
// API Client Class
class TelanganaAPI {
  constructor(baseURL = 'http://localhost:8080/api/v1') {
    this.baseURL = baseURL;
    this.accessToken = null;
  }

  async login(usernameOrEmail, password) {
    const response = await fetch(`${this.baseURL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ usernameOrEmail, password }),
    });

    if (!response.ok) {
      throw new Error('Login failed');
    }

    const data = await response.json();
    this.accessToken = data.accessToken;
    return data;
  }

  async getMembers() {
    const response = await fetch(`${this.baseURL}/members`, {
      headers: {
        'Authorization': `Bearer ${this.accessToken}`,
      },
    });

    if (!response.ok) {
      throw new Error('Failed to fetch members');
    }

    return response.json();
  }

  async createPlayer(playerData) {
    const response = await fetch(`${this.baseURL}/players`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${this.accessToken}`,
      },
      body: JSON.stringify(playerData),
    });

    if (!response.ok) {
      throw new Error('Failed to create player');
    }

    return response.json();
  }
}

// Usage
const api = new TelanganaAPI();

async function example() {
  try {
    // Login
    await api.login('admin@example.com', 'password');

    // Get members
    const members = await api.getMembers();
    console.log('Members:', members);

    // Create player
    const newPlayer = await api.createPlayer({
      name: 'John Doe',
      email: 'john@example.com',
      category: 'MEN',
      gender: 'MALE',
      dateOfBirth: '1995-01-01',
      districtId: '123e4567-e89b-12d3-a456-426614174000',
    });
    console.log('New Player:', newPlayer);
  } catch (error) {
    console.error('Error:', error);
  }
}
```

### Python

```python
import requests
from typing import Optional, Dict, Any

class TelanganaAPI:
    def __init__(self, base_url: str = 'http://localhost:8080/api/v1'):
        self.base_url = base_url
        self.access_token: Optional[str] = None

    def login(self, username_or_email: str, password: str) -> Dict[str, Any]:
        """Login and obtain JWT token"""
        response = requests.post(
            f'{self.base_url}/auth/login',
            json={
                'usernameOrEmail': username_or_email,
                'password': password
            }
        )
        response.raise_for_status()
        data = response.json()
        self.access_token = data['accessToken']
        return data

    def get_headers(self) -> Dict[str, str]:
        """Get headers with authorization"""
        headers = {'Content-Type': 'application/json'}
        if self.access_token:
            headers['Authorization'] = f'Bearer {self.access_token}'
        return headers

    def get_members(self) -> list:
        """Get all active members"""
        response = requests.get(
            f'{self.base_url}/members',
            headers=self.get_headers()
        )
        response.raise_for_status()
        return response.json()

    def create_player(self, player_data: Dict[str, Any]) -> Dict[str, Any]:
        """Create a new player"""
        response = requests.post(
            f'{self.base_url}/players',
            json=player_data,
            headers=self.get_headers()
        )
        response.raise_for_status()
        return response.json()

    def get_tournaments(self, page: int = 0, size: int = 20) -> Dict[str, Any]:
        """Get tournaments with pagination"""
        response = requests.get(
            f'{self.base_url}/tournaments/paginated',
            params={'page': page, 'size': size},
            headers=self.get_headers()
        )
        response.raise_for_status()
        return response.json()

# Usage
if __name__ == '__main__':
    api = TelanganaAPI()
    
    # Login
    api.login('admin@example.com', 'password')
    
    # Get members
    members = api.get_members()
    print(f'Found {len(members)} members')
    
    # Create player
    new_player = api.create_player({
        'name': 'John Doe',
        'email': 'john@example.com',
        'category': 'MEN',
        'gender': 'MALE',
        'dateOfBirth': '1995-01-01',
        'districtId': '123e4567-e89b-12d3-a456-426614174000'
    })
    print(f'Created player: {new_player["name"]}')
```

### Java

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
        LoginRequest loginRequest = new LoginRequest(usernameOrEmail, password);
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Login failed: " + response.body());
        }

        LoginResponse loginResponse = objectMapper.readValue(response.body(), 
                LoginResponse.class);
        this.accessToken = loginResponse.getAccessToken();
        return loginResponse;
    }

    public List<MemberResponse> getMembers() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + "/members"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch members: " + response.body());
        }

        return objectMapper.readValue(response.body(), 
                new TypeReference<List<MemberResponse>>() {});
    }

    // Usage
    public static void main(String[] args) {
        try {
            TelanganaAPIClient api = new TelanganaAPIClient(
                    "http://localhost:8080/api/v1");

            // Login
            api.login("admin@example.com", "password");

            // Get members
            List<MemberResponse> members = api.getMembers();
            System.out.println("Found " + members.size() + " members");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### cURL Examples

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin@example.com",
    "password": "password"
  }'

# Get all members
curl -X GET http://localhost:8080/api/v1/members \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# Create a player
curl -X POST http://localhost:8080/api/v1/players \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "category": "MEN",
    "gender": "MALE",
    "dateOfBirth": "1995-01-01",
    "districtId": "123e4567-e89b-12d3-a456-426614174000"
  }'

# Get tournaments with pagination
curl -X GET "http://localhost:8080/api/v1/tournaments/paginated?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# Search players
curl -X GET "http://localhost:8080/api/v1/players/search?q=john&page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"

# Upload member photo
curl -X POST http://localhost:8080/api/v1/members/123e4567-e89b-12d3-a456-426614174000/photo \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -F "file=@/path/to/photo.jpg"
```

## Best Practices

### 1. Always Use HTTPS in Production

```javascript
// Good
const api = new TelanganaAPI('https://api.telanganaballbadminton.org/api/v1');

// Bad (only for development)
const api = new TelanganaAPI('http://api.telanganaballbadminton.org/api/v1');
```

### 2. Handle Token Expiration

```javascript
async function makeAuthenticatedRequest(url, options = {}) {
  let response = await fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      'Authorization': `Bearer ${accessToken}`,
    },
  });

  // If token expired, refresh and retry
  if (response.status === 401) {
    await refreshToken();
    response = await fetch(url, {
      ...options,
      headers: {
        ...options.headers,
        'Authorization': `Bearer ${accessToken}`,
      },
    });
  }

  return response;
}
```

### 3. Implement Retry Logic

```javascript
async function fetchWithRetry(url, options = {}, maxRetries = 3) {
  for (let i = 0; i < maxRetries; i++) {
    try {
      const response = await fetch(url, options);
      if (response.ok) {
        return response;
      }
      if (response.status >= 500 && i < maxRetries - 1) {
        await new Promise(resolve => setTimeout(resolve, 1000 * (i + 1)));
        continue;
      }
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    } catch (error) {
      if (i === maxRetries - 1) throw error;
      await new Promise(resolve => setTimeout(resolve, 1000 * (i + 1)));
    }
  }
}
```

### 4. Use Pagination for Large Datasets

```javascript
async function getAllPlayers() {
  const allPlayers = [];
  let page = 0;
  let hasMore = true;

  while (hasMore) {
    const response = await api.getPlayers({ page, size: 100 });
    allPlayers.push(...response.content);
    hasMore = !response.last;
    page++;
  }

  return allPlayers;
}
```

### 5. Validate Input Data

```javascript
function validatePlayerData(data) {
  const errors = [];

  if (!data.name || data.name.length < 3) {
    errors.push('Name must be at least 3 characters');
  }

  if (!data.email || !isValidEmail(data.email)) {
    errors.push('Valid email is required');
  }

  if (!data.category || !['MEN', 'WOMEN', 'JUNIOR', 'SENIOR'].includes(data.category)) {
    errors.push('Valid category is required');
  }

  if (errors.length > 0) {
    throw new ValidationError(errors);
  }
}
```

### 6. Use Environment Variables

```javascript
// .env file
API_BASE_URL=https://api.telanganaballbadminton.org/api/v1
API_TIMEOUT=30000

// Usage
const api = new TelanganaAPI(process.env.API_BASE_URL);
```

## API Versioning

### Current Version: v1

The API uses URL-based versioning. The current version is `v1`, included in the base URL:

```
https://api.telanganaballbadminton.org/api/v1
```

### Version Header

You can also specify the API version using the `Accept` header:

```http
Accept: application/vnd.telangana-ball-badminton.v1+json
```

### Version Lifecycle

- **v1 (Current)**: Stable, fully supported
- **v2 (Planned)**: Under development, not yet available

### Backward Compatibility

We maintain backward compatibility within major versions. Minor changes (bug fixes, new optional fields) do not require version changes.

### Breaking Changes

Breaking changes will be introduced in new major versions (v2, v3, etc.) with:
- 6 months advance notice
- Migration guide
- Parallel support for old and new versions during transition

## Deprecation Policy

### Deprecation Notice

When an endpoint or feature is deprecated, we provide:

1. **Deprecation Header**: `Deprecation: true`
2. **Sunset Header**: `Sunset: Sat, 01 Jun 2024 00:00:00 GMT`
3. **Link to Migration Guide**: `Link: <https://docs.telanganaballbadminton.org/migration>; rel="deprecation"`

### Example Deprecated Endpoint

```http
GET /api/v1/members/old-endpoint
Deprecation: true
Sunset: Sat, 01 Jun 2024 00:00:00 GMT
Link: <https://docs.telanganaballbadminton.org/migration>; rel="deprecation"
Warning: This endpoint is deprecated and will be removed on 2024-06-01. Use /api/v1/members instead.
```

### Deprecation Timeline

1. **Announcement**: 6 months before removal
2. **Deprecation Headers**: Added immediately
3. **Documentation Update**: Migration guide published
4. **Warning Period**: 3 months with increased warnings
5. **Removal**: After 6 months

### Currently Deprecated Endpoints

| Endpoint | Deprecated Since | Removal Date | Alternative |
|----------|------------------|--------------|-------------|
| None | - | - | - |

### Checking for Deprecations

Monitor response headers for deprecation warnings:

```javascript
const response = await fetch(url);
if (response.headers.get('Deprecation')) {
  console.warn('Warning: This endpoint is deprecated');
  console.warn('Sunset:', response.headers.get('Sunset'));
  console.warn('Migration guide:', response.headers.get('Link'));
}
```

## Support

### Documentation

- **API Documentation**: https://api.telanganaballbadminton.org/api/v1/swagger-ui.html
- **Developer Portal**: https://developers.telanganaballbadminton.org
- **GitHub**: https://github.com/telangana-ball-badminton/api

### Contact

- **Email**: api-support@telanganaballbadminton.org
- **Issue Tracker**: https://github.com/telangana-ball-badminton/api/issues

### Status Page

Check API status and uptime: https://status.telanganaballbadminton.org

---

**Last Updated**: December 2023  
**API Version**: 1.0.0  
**Document Version**: 1.0.0
