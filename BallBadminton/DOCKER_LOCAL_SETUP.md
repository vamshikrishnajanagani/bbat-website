# Docker Local Development Setup

This setup allows you to run the Telangana Ball Badminton Association website locally using Docker while connecting to the existing Heroku PostgreSQL database.

## Prerequisites

- Docker and Docker Compose installed
- Backend application built (`./gradlew build -x test` in backend directory)

## Quick Start

### Start the Application
```bash
./start-local.sh
```

### Stop the Application
```bash
./stop-local.sh
```

## Services

### Backend (Spring Boot)
- **URL**: http://localhost:8080/api/v1
- **Health Check**: http://localhost:8080/api/v1/health
- **Database**: Uses existing Heroku PostgreSQL database
- **Profile**: `local` (Redis disabled, minimal logging)

### Frontend (React)
- **URL**: http://localhost:3000
- **API Endpoint**: Configured to connect to backend at http://localhost:8080/api/v1

## Configuration

### Database Connection
The application uses the existing Heroku PostgreSQL database:
- **Host**: c1erdbv5s7bd6i.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com
- **Database**: df72l64ipgnnt9
- **User**: uarq7169567h9a

### Environment Variables
All database credentials are configured in the Docker Compose file and Spring Boot configuration.

## Manual Docker Commands

If you prefer to run Docker commands manually:

```bash
# Build and start services
docker-compose -f docker-compose.local.yml up --build

# Start services in background
docker-compose -f docker-compose.local.yml up -d

# Stop services
docker-compose -f docker-compose.local.yml down

# View logs
docker-compose -f docker-compose.local.yml logs -f

# View backend logs only
docker-compose -f docker-compose.local.yml logs -f backend

# View frontend logs only
docker-compose -f docker-compose.local.yml logs -f frontend
```

## Troubleshooting

### Backend Won't Start
1. Check if the JAR file exists: `backend/build/libs/telangana-ball-badminton-association-1.0.0.jar`
2. If not, build the backend: `cd backend && ./gradlew build -x test`

### Database Connection Issues
1. Verify the Heroku database is accessible
2. Check the database credentials in `docker-compose.local.yml`

### Frontend Issues
1. Check if `node_modules` exists in frontend directory
2. If not, run: `cd frontend && npm install`

## Development Workflow

1. Make code changes in your IDE
2. For backend changes: rebuild the JAR and restart Docker
3. For frontend changes: the development server will auto-reload

## Ports Used

- **3000**: Frontend (React development server)
- **8080**: Backend (Spring Boot API)

Make sure these ports are not in use by other applications.