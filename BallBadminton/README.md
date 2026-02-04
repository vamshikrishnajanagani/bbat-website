# Telangana Ball Badminton Association Website

A comprehensive web application for managing the Telangana Ball Badminton Association, built with modern technologies and best practices.

## 🏗️ Architecture

### Backend
- **Framework**: Spring Boot 3.2.0
- **Database**: PostgreSQL (Heroku Postgres)
- **Authentication**: JWT with role-based access control
- **Caching**: Redis (production) / In-memory (local)
- **Documentation**: OpenAPI/Swagger
- **Testing**: JUnit 5, Mockito, TestContainers

### Frontend
- **Framework**: React 18 with TypeScript
- **State Management**: Redux Toolkit
- **Styling**: Styled Components
- **Internationalization**: i18next (English/Telugu)
- **Build Tool**: Create React App
- **Testing**: Jest, React Testing Library

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Docker (optional)

### Local Development

#### Option 1: Docker (Recommended)
```bash
# Start both backend and frontend
./start-local.sh

# Stop services
./stop-local.sh
```

#### Option 2: Manual Setup
```bash
# Backend
cd backend
./gradlew build -x test
java -jar build/libs/telangana-ball-badminton-association-1.0.0.jar --spring.profiles.active=local

# Frontend (in another terminal)
cd frontend
npm install
npm start
```

### Access Points
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/v1
- **API Documentation**: http://localhost:8080/api/v1/swagger-ui.html
- **Health Check**: http://localhost:8080/api/v1/health

## 📋 Features

### Core Functionality
- **Member Management**: Registration, profiles, directory
- **Player Management**: Player profiles, statistics, achievements
- **Tournament Management**: Tournament creation, registration, brackets
- **News & Media**: Articles, galleries, announcements
- **District Management**: Geographic organization
- **User Authentication**: Secure login with role-based permissions

### Technical Features
- **RESTful API**: Comprehensive endpoints with proper HTTP methods
- **Database Migrations**: Flyway for schema versioning
- **Audit Logging**: Track all system changes
- **Data Protection**: GDPR compliance features
- **File Upload**: Media management with validation
- **Email Notifications**: Automated communications
- **Caching**: Performance optimization
- **Security**: JWT, CORS, input validation
- **Monitoring**: Health checks and metrics

## 🗄️ Database Schema

### Key Entities
- **Users**: System authentication and authorization
- **Members**: Association members with contact details
- **Players**: Ball badminton players with statistics
- **Tournaments**: Competition management
- **Districts**: Geographic organization
- **News Articles**: Content management
- **Media Galleries**: Photo and video management
- **Audit Logs**: System activity tracking

## 🔒 Security

- JWT token-based authentication
- Role-based access control (RBAC)
- Password encryption with BCrypt
- CORS configuration
- Security headers
- Input validation and sanitization
- Rate limiting
- Audit logging

## 🌐 API Endpoints

### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/logout` - User logout
- `POST /api/v1/auth/refresh` - Token refresh

### Members
- `GET /api/v1/members` - List members
- `POST /api/v1/members` - Create member
- `GET /api/v1/members/{id}` - Get member details
- `PUT /api/v1/members/{id}` - Update member
- `DELETE /api/v1/members/{id}` - Delete member

### Players
- `GET /api/v1/players` - List players
- `POST /api/v1/players` - Create player
- `GET /api/v1/players/{id}` - Get player details
- `GET /api/v1/players/{id}/statistics` - Player statistics

### Tournaments
- `GET /api/v1/tournaments` - List tournaments
- `POST /api/v1/tournaments` - Create tournament
- `POST /api/v1/tournaments/{id}/register` - Register for tournament
- `GET /api/v1/tournaments/{id}/bracket` - Tournament bracket

### News
- `GET /api/v1/news` - List news articles
- `POST /api/v1/news` - Create article (admin)
- `GET /api/v1/news/{id}` - Get article details

## 🧪 Testing

### Backend Tests
```bash
cd backend
./gradlew test
```

### Frontend Tests
```bash
cd frontend
npm test
```

### E2E Tests
```bash
cd backend
./gradlew test --tests "*E2ETest"
```

## 🚀 Deployment

### Heroku (Production)
```bash
cd backend
./deploy-heroku.sh
```

### Environment Variables
- `DATABASE_URL`: PostgreSQL connection string
- `REDIS_URL`: Redis connection string (production)
- `JWT_SECRET`: JWT signing secret
- `MAIL_*`: Email configuration

## 📁 Project Structure

```
├── backend/                 # Spring Boot backend
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   ├── src/test/           # Test files
│   └── build.gradle        # Build configuration
├── frontend/               # React frontend
│   ├── src/                # TypeScript source code
│   ├── public/             # Static assets
│   └── package.json        # Dependencies
├── docker-compose.local.yml # Local development
└── README.md               # This file
```

## 🔧 Configuration

### Profiles
- `local`: Local development with in-memory cache
- `development`: Development with external services
- `production`: Production configuration
- `heroku`: Heroku-specific settings

### Database
The application uses PostgreSQL with Flyway migrations. The database schema is automatically created and updated.

## 📊 Monitoring

- Health endpoints at `/api/v1/health`
- Application metrics
- Audit logging
- Performance monitoring

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For support and questions, please contact the Telangana Ball Badminton Association.

---

**Built with ❤️ for the Telangana Ball Badminton Community**