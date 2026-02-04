# Development Environment Setup

This document provides comprehensive instructions for setting up the development environment for the Telangana Ball Badminton Association Website.

## Prerequisites

Before setting up the development environment, ensure you have the following installed:

### Required Software

1. **Docker Desktop** (v20.10+)
   - [Download for Windows](https://docs.docker.com/desktop/windows/install/)
   - [Download for macOS](https://docs.docker.com/desktop/mac/install/)
   - [Download for Linux](https://docs.docker.com/desktop/linux/install/)

2. **Node.js** (v18+ LTS)
   - [Download from nodejs.org](https://nodejs.org/)
   - Includes npm package manager

3. **Java Development Kit** (JDK 17+)
   - [Download OpenJDK](https://adoptium.net/)
   - Or use [SDKMAN](https://sdkman.io/) for easy Java version management

4. **Git** (v2.30+)
   - [Download from git-scm.com](https://git-scm.com/)

### Optional but Recommended

- **Visual Studio Code** with extensions:
  - Java Extension Pack
  - Spring Boot Extension Pack
  - ES7+ React/Redux/React-Native snippets
  - Prettier - Code formatter
  - ESLint
- **IntelliJ IDEA** (Community or Ultimate)
- **Postman** or **Insomnia** for API testing

## Quick Setup

### Automated Setup (Recommended)

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd telangana-ball-badminton-website
   ```

2. **Run the setup script:**

   **For Linux/macOS:**
   ```bash
   cd backend
   chmod +x scripts/dev-setup.sh
   ./scripts/dev-setup.sh
   ```

   **For Windows:**
   ```cmd
   cd backend
   scripts\dev-setup.bat
   ```

3. **Start the applications:**
   ```bash
   # Terminal 1 - Backend
   cd backend
   ./gradlew bootRun

   # Terminal 2 - Frontend
   cd frontend
   npm start
   ```

### Manual Setup

If you prefer to set up manually or the automated script fails:

#### 1. Backend Setup

1. **Navigate to backend directory:**
   ```bash
   cd backend
   ```

2. **Create environment file:**
   ```bash
   cp .env.example .env
   # Edit .env with your preferred settings
   ```

3. **Start Docker services:**
   ```bash
   docker-compose up -d postgres redis mailhog
   ```

4. **Wait for services to be ready:**
   ```bash
   # Check PostgreSQL
   docker exec tbba-postgres pg_isready -U tbba_user -d telangana_ball_badminton_dev

   # Check Redis
   docker exec tbba-redis redis-cli ping
   ```

5. **Build and run the application:**
   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```

#### 2. Frontend Setup

1. **Navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   # or
   yarn install
   ```

3. **Start the development server:**
   ```bash
   npm start
   # or
   yarn start
   ```

## Service URLs

Once the development environment is running, you can access:

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:3000 | React application |
| Backend API | http://localhost:8080 | Spring Boot API |
| API Documentation | http://localhost:8080/swagger-ui.html | Swagger UI |
| Health Check | http://localhost:8080/api/v1/public/health | Application health |
| PostgreSQL | localhost:5432 | Database server |
| Redis | localhost:6379 | Cache server |
| MailHog UI | http://localhost:8025 | Email testing |

## Default Credentials

### Database
- **Host:** localhost:5432
- **Database:** telangana_ball_badminton_dev
- **Username:** tbba_user
- **Password:** tbba_password_2024

### Application Users
- **Admin:** admin / password
- **Editor:** editor / password
- **Moderator:** moderator / password

### Redis
- **Host:** localhost:6379
- **Password:** redis_password_2024

## Development Workflow

### Backend Development

1. **Code Structure:**
   ```
   backend/src/main/java/com/telangana/ballbadminton/
   ├── config/          # Configuration classes
   ├── controller/      # REST controllers
   ├── service/         # Business logic
   ├── repository/      # Data access layer
   ├── entity/          # JPA entities
   ├── dto/             # Data transfer objects
   └── util/            # Utility classes
   ```

2. **Running Tests:**
   ```bash
   # All tests
   ./gradlew test

   # Specific test class
   ./gradlew test --tests "HealthControllerTest"

   # Integration tests only
   ./gradlew test --tests "*IntegrationTest"
   ```

3. **Database Operations:**
   ```bash
   # Connect to PostgreSQL
   docker exec -it tbba-postgres psql -U tbba_user -d telangana_ball_badminton_dev

   # View logs
   docker logs tbba-postgres

   # Reset database
   docker-compose down
   docker volume rm backend_postgres_data
   docker-compose up -d postgres
   ```

### Frontend Development

1. **Code Structure:**
   ```
   frontend/src/
   ├── components/      # Reusable components
   ├── pages/           # Page components
   ├── services/        # API services
   ├── store/           # Redux store and slices
   ├── hooks/           # Custom React hooks
   ├── styles/          # Styled components and themes
   └── i18n/            # Internationalization
   ```

2. **Running Tests:**
   ```bash
   # All tests
   npm test

   # Watch mode
   npm test -- --watch

   # Coverage report
   npm test -- --coverage
   ```

3. **Linting and Formatting:**
   ```bash
   # Lint code
   npm run lint

   # Fix linting issues
   npm run lint:fix

   # Type checking
   npm run type-check
   ```

## Testing

### Backend Testing

The backend uses JUnit 5 with the following test types:

1. **Unit Tests:** Test individual components in isolation
2. **Integration Tests:** Test component interactions with real database
3. **Web Layer Tests:** Test REST controllers with MockMvc

**Test Configuration:**
- H2 in-memory database for unit tests
- Testcontainers for integration tests
- MockMvc for web layer testing

### Frontend Testing

The frontend uses Jest and React Testing Library:

1. **Component Tests:** Test individual React components
2. **Hook Tests:** Test custom React hooks
3. **Integration Tests:** Test component interactions
4. **E2E Tests:** End-to-end testing (planned)

## Troubleshooting

### Common Issues

1. **Docker services not starting:**
   ```bash
   # Check Docker status
   docker info

   # Restart Docker Desktop
   # Check port conflicts
   netstat -tulpn | grep :5432
   ```

2. **Database connection issues:**
   ```bash
   # Check PostgreSQL logs
   docker logs tbba-postgres

   # Verify connection
   docker exec tbba-postgres pg_isready -U tbba_user
   ```

3. **Frontend build issues:**
   ```bash
   # Clear npm cache
   npm cache clean --force

   # Delete node_modules and reinstall
   rm -rf node_modules package-lock.json
   npm install
   ```

4. **Backend build issues:**
   ```bash
   # Clean Gradle cache
   ./gradlew clean

   # Refresh dependencies
   ./gradlew build --refresh-dependencies
   ```

### Port Conflicts

If you encounter port conflicts, you can modify the ports in:
- `docker-compose.yml` for database services
- `application-development.yml` for backend port
- `package.json` proxy setting for frontend

### Performance Issues

1. **Increase Docker memory allocation** (Docker Desktop → Settings → Resources)
2. **Use Gradle daemon:** Add `org.gradle.daemon=true` to `gradle.properties`
3. **Enable parallel builds:** Add `org.gradle.parallel=true` to `gradle.properties`

## Environment Variables

### Backend Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | Database host |
| `DB_PORT` | 5432 | Database port |
| `DB_NAME` | telangana_ball_badminton_dev | Database name |
| `DB_USERNAME` | tbba_user | Database username |
| `DB_PASSWORD` | tbba_password_2024 | Database password |
| `REDIS_HOST` | localhost | Redis host |
| `REDIS_PORT` | 6379 | Redis port |
| `JWT_SECRET` | dev-secret | JWT signing secret |
| `CORS_ALLOWED_ORIGINS` | http://localhost:3000 | Allowed CORS origins |

### Frontend Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `REACT_APP_API_URL` | http://localhost:8080 | Backend API URL |
| `REACT_APP_ENVIRONMENT` | development | Environment name |

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://reactjs.org/docs)
- [Docker Documentation](https://docs.docker.com/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Redis Documentation](https://redis.io/documentation)

## Getting Help

If you encounter issues not covered in this guide:

1. Check the application logs
2. Review the troubleshooting section
3. Search existing issues in the repository
4. Create a new issue with detailed information about the problem

## Next Steps

After setting up the development environment:

1. Review the [API Documentation](http://localhost:8080/swagger-ui.html)
2. Explore the codebase structure
3. Run the test suites to ensure everything works
4. Start implementing new features following the project guidelines