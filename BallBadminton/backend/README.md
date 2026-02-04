# Telangana Ball Badminton Association Website - Backend

Official REST API backend for the Telangana Ball Badminton Association Website, built with Spring Boot 3.2.0 and Java 17.

## Overview

This backend provides a comprehensive API-driven platform for managing:
- Association members and organizational hierarchy
- Player profiles, achievements, and statistics
- Tournament management and registration
- Geographic information for Telangana state and districts
- News, announcements, and media galleries
- BWF-style features and multilingual support

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Primary database (MySQL also supported)
- **Redis** - Caching and session management
- **JWT** - Token-based authentication
- **OpenAPI 3** - API documentation
- **Maven** - Build tool
- **Docker** - Containerization support

## Features

### Core Functionality
- ✅ RESTful API architecture with OpenAPI documentation
- ✅ JWT-based authentication and authorization
- ✅ Dynamic content management through APIs
- ✅ Real-time updates and notifications
- ✅ Comprehensive monitoring and logging
- ✅ Redis-based caching for performance
- ✅ File upload and media management
- ✅ Multilingual support (English/Telugu)

### Security Features
- ✅ HTTPS enforcement and security headers
- ✅ CORS configuration for cross-origin requests
- ✅ Rate limiting and request throttling
- ✅ Input validation and sanitization
- ✅ Audit logging for all operations
- ✅ Role-based access control

### Monitoring & Observability
- ✅ Spring Boot Actuator endpoints
- ✅ Prometheus metrics integration
- ✅ Structured JSON logging
- ✅ Custom health indicators
- ✅ Performance monitoring with @Timed

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher (or MySQL 8.0+)
- Redis 6.0 or higher

### Environment Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd backend
   ```

2. **Configure Database**
   
   Create a PostgreSQL database:
   ```sql
   CREATE DATABASE telangana_ball_badminton_dev;
   CREATE USER tbba_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE telangana_ball_badminton_dev TO tbba_user;
   ```

3. **Set Environment Variables**
   ```bash
   export DB_HOST=localhost
   export DB_PORT=5432
   export DB_NAME=telangana_ball_badminton_dev
   export DB_USERNAME=tbba_user
   export DB_PASSWORD=your_password
   export REDIS_HOST=localhost
   export REDIS_PORT=6379
   export JWT_SECRET=your-super-secret-jwt-key-here
   ```

4. **Run the Application**
   ```bash
   # Development mode
   mvn spring-boot:run -Dspring-boot.run.profiles=development
   
   # Or build and run JAR
   mvn clean package
   java -jar target/association-website-1.0.0.jar --spring.profiles.active=development
   ```

### Docker Setup

1. **Using Docker Compose (Recommended)**
   ```bash
   # Start all services (app, database, redis)
   docker-compose up -d
   
   # View logs
   docker-compose logs -f app
   
   # Stop services
   docker-compose down
   ```

2. **Manual Docker Build**
   ```bash
   # Build image
   docker build -t telangana-ball-badminton-api .
   
   # Run container
   docker run -p 8080:8080 \
     -e SPRING_PROFILES_ACTIVE=development \
     -e DB_HOST=host.docker.internal \
     telangana-ball-badminton-api
   ```

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v1/api-docs
- **Health Check**: http://localhost:8080/api/v1/public/health

## Configuration

### Application Profiles

- **development** - Local development with debug logging
- **production** - Production-ready configuration with security
- **test** - Testing configuration with H2 in-memory database

### Key Configuration Files

- `application.yml` - Base configuration
- `application-development.yml` - Development environment
- `application-production.yml` - Production environment
- `application-test.yml` - Test environment
- `logback-spring.xml` - Logging configuration

### Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | Database host | localhost | Yes |
| `DB_PORT` | Database port | 5432 | No |
| `DB_NAME` | Database name | - | Yes |
| `DB_USERNAME` | Database username | - | Yes |
| `DB_PASSWORD` | Database password | - | Yes |
| `REDIS_HOST` | Redis host | localhost | Yes |
| `REDIS_PORT` | Redis port | 6379 | No |
| `JWT_SECRET` | JWT signing secret | - | Yes (Production) |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | - | Yes (Production) |

## Database

### Schema Management

The application uses Flyway for database migrations:

- Migration files: `src/main/resources/db/migration/`
- Initial schema: `V1__Initial_Schema.sql`
- Automatic migration on startup

### Supported Databases

- **PostgreSQL** (Recommended) - Primary database for production
- **MySQL** - Alternative database option
- **H2** - In-memory database for testing

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=HealthControllerTest

# Run tests with coverage
mvn test jacoco:report
```

### Test Categories

- **Unit Tests** - Individual component testing
- **Integration Tests** - API endpoint testing
- **Security Tests** - Authentication and authorization testing
- **Performance Tests** - Load and stress testing

## Monitoring

### Health Checks

- **Application Health**: `/actuator/health`
- **Database Health**: Custom indicator for database connectivity
- **Redis Health**: Custom indicator for cache connectivity
- **Custom Health**: `/api/v1/public/health`

### Metrics

- **Prometheus Metrics**: `/actuator/prometheus`
- **Application Metrics**: `/actuator/metrics`
- **Custom Metrics**: Performance timing with @Timed

### Logging

- **Structured Logging**: JSON format in production
- **Log Levels**: Configurable per environment
- **Log Files**: Rotating file appenders
- **Audit Logging**: Security and data change tracking

## Security

### Authentication

- JWT-based stateless authentication
- Token expiration and refresh mechanism
- Secure password hashing with BCrypt

### Authorization

- Role-based access control (RBAC)
- Method-level security annotations
- Resource-based permissions

### Security Headers

- HTTPS enforcement
- CORS configuration
- Content Security Policy
- XSS protection
- Frame options

## Performance

### Caching Strategy

- **Redis Caching**: Distributed cache for scalability
- **Cache Configurations**: Different TTL per data type
- **Cache Eviction**: Automatic and manual cache invalidation

### Database Optimization

- **Connection Pooling**: HikariCP for efficient connections
- **Indexing**: Strategic database indexes
- **Query Optimization**: JPA query optimization

## Deployment

### Production Checklist

- [ ] Set secure JWT secret
- [ ] Configure production database
- [ ] Set up Redis cluster
- [ ] Configure HTTPS/SSL
- [ ] Set up monitoring and alerting
- [ ] Configure log aggregation
- [ ] Set up backup procedures
- [ ] Configure rate limiting
- [ ] Review security settings

### CI/CD Pipeline

The application is designed for automated deployment with:

- Maven build and test
- Docker image creation
- Database migration
- Health check verification
- Rolling deployment support

## Contributing

### Development Guidelines

1. Follow Java coding standards
2. Write comprehensive tests
3. Update API documentation
4. Follow Git commit conventions
5. Ensure security best practices

### Code Quality

- **Checkstyle** - Code style enforcement
- **SpotBugs** - Static analysis
- **JaCoCo** - Test coverage reporting
- **SonarQube** - Code quality analysis

## Support

For technical support or questions:

- **Email**: admin@telanganaballbadminton.org
- **Documentation**: API documentation at `/swagger-ui.html`
- **Issues**: Create GitHub issues for bug reports

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Telangana Ball Badminton Association**  
Official Website Backend API  
Version 1.0.0