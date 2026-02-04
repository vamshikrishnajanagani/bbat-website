# Telangana Ball Badminton Association API Documentation

## Welcome

Welcome to the comprehensive documentation for the Telangana Ball Badminton Association API. This documentation provides everything you need to integrate with our API, from getting started guides to advanced integration patterns.

## Documentation Structure

### 📚 Core Documentation

1. **[API Developer Guide](./API_DEVELOPER_GUIDE.md)**
   - Complete guide to using the API
   - Authentication and authorization
   - Request/response formats
   - Error handling
   - Pagination and filtering
   - Rate limiting and caching
   - Best practices

2. **[API Integration Examples](./API_INTEGRATION_EXAMPLES.md)**
   - React integration with hooks
   - Angular integration with services
   - Vue.js integration
   - Node.js backend integration
   - Mobile app integration
   - Webhook integration
   - Batch operations

3. **[API Code Samples](./API_CODE_SAMPLES.md)**
   - JavaScript/TypeScript examples
   - Python examples
   - Java examples
   - cURL examples
   - Postman collection

4. **[API Versioning Strategy](./API_VERSIONING_STRATEGY.md)**
   - Versioning approach
   - Version lifecycle
   - Breaking vs non-breaking changes
   - Deprecation policy
   - Migration guides

### 🚀 Quick Start

#### 1. Access Interactive Documentation

The API provides interactive documentation powered by Swagger UI:

- **Local Development**: http://localhost:8080/swagger-ui.html
- **Production**: https://api.telanganaballbadminton.org/swagger-ui.html

#### 2. Obtain API Credentials

Contact the administrator to get your API credentials or create an account through the registration endpoint.

#### 3. Authenticate

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "your-email@example.com",
    "password": "your-password"
  }'
```

#### 4. Make Your First API Call

```bash
curl -X GET http://localhost:8080/api/v1/members \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## API Overview

### Base URLs

- **Development**: `http://localhost:8080/api/v1`
- **Staging**: `https://api-staging.telanganaballbadminton.org/api/v1`
- **Production**: `https://api.telanganaballbadminton.org/api/v1`

### Current Version

**Version**: 1.0.0  
**Status**: Stable  
**Release Date**: December 1, 2023

### Key Features

- ✅ **RESTful Design**: Standard HTTP methods and status codes
- ✅ **JWT Authentication**: Secure token-based authentication
- ✅ **Role-Based Access Control**: Fine-grained permissions
- ✅ **Comprehensive Pagination**: Efficient data retrieval
- ✅ **Advanced Filtering**: Flexible query capabilities
- ✅ **Full-Text Search**: Search across multiple fields
- ✅ **File Upload**: Media and document management
- ✅ **Email Notifications**: Automated communication
- ✅ **Audit Logging**: Complete activity tracking
- ✅ **Caching**: Optimized performance
- ✅ **Rate Limiting**: Fair usage policies
- ✅ **Multilingual Support**: English and Telugu

## API Endpoints

### Authentication
- `POST /auth/login` - User login
- `POST /auth/refresh` - Refresh access token
- `POST /auth/logout` - User logout

### Member Management
- `GET /members` - Get all members
- `GET /members/{id}` - Get member by ID
- `POST /members` - Create member
- `PUT /members/{id}` - Update member
- `DELETE /members/{id}` - Delete member

### Player Management
- `GET /players` - Get all players
- `GET /players/{id}` - Get player by ID
- `GET /players/search` - Search players
- `POST /players` - Create player
- `GET /players/{id}/achievements` - Get achievements

### Tournament Management
- `GET /tournaments` - Get all tournaments
- `GET /tournaments/upcoming` - Get upcoming tournaments
- `POST /tournaments` - Create tournament
- `POST /tournaments/{id}/registrations` - Register for tournament

### District Management
- `GET /districts` - Get all districts
- `GET /districts/{id}` - Get district by ID
- `GET /districts/{id}/statistics` - Get district statistics

### News & Media
- `GET /news/articles` - Get news articles
- `GET /media/galleries` - Get media galleries
- `POST /media/upload` - Upload media file

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Token Lifecycle

- **Access Token**: Expires after 1 hour
- **Refresh Token**: Expires after 7 days
- **Token Refresh**: Use refresh token to obtain new access token

## Error Handling

The API uses standard HTTP status codes:

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created |
| 400 | Bad Request - Invalid data |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 429 | Too Many Requests - Rate limit exceeded |
| 500 | Internal Server Error |

## Rate Limiting

| User Type | Requests/Hour | Burst Limit |
|-----------|---------------|-------------|
| Anonymous | 100 | 20 |
| Authenticated | 1,000 | 100 |
| Admin | 5,000 | 500 |

## Support

### Documentation Resources

- **Interactive API Docs**: http://localhost:8080/swagger-ui.html
- **OpenAPI Specification**: http://localhost:8080/v3/api-docs
- **Developer Portal**: https://developers.telanganaballbadminton.org
- **Changelog**: https://www.telanganaballbadminton.org/api/changelog

### Support Channels

- **Email**: api-support@telanganaballbadminton.org
- **GitHub Issues**: https://github.com/telangana-ball-badminton/api/issues
- **Status Page**: https://status.telanganaballbadminton.org

### Community

- **Developer Forum**: https://forum.telanganaballbadminton.org
- **Twitter**: @TelanganaAPI
- **Newsletter**: Subscribe at https://www.telanganaballbadminton.org/api/subscribe

## Contributing

We welcome contributions to improve the API and documentation. Please see our contributing guidelines for more information.

## License

This API is licensed under the MIT License. See LICENSE file for details.

---

**Last Updated**: December 2023  
**Documentation Version**: 1.0.0  
**API Version**: 1.0.0
