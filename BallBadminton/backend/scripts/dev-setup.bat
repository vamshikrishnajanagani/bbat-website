@echo off
REM Development Environment Setup Script for Windows
REM Telangana Ball Badminton Association Website

setlocal enabledelayedexpansion

echo 🏸 Setting up Telangana Ball Badminton Association Website Development Environment
echo ==============================================================================

REM Check if Docker is installed and running
echo [INFO] Checking Docker installation...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not installed. Please install Docker Desktop first.
    pause
    exit /b 1
)

docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo [SUCCESS] Docker is installed and running

REM Check if Docker Compose is available
echo [INFO] Checking Docker Compose...
docker-compose --version >nul 2>&1
if errorlevel 1 (
    docker compose version >nul 2>&1
    if errorlevel 1 (
        echo [ERROR] Docker Compose is not available. Please install Docker Compose.
        pause
        exit /b 1
    )
)
echo [SUCCESS] Docker Compose is available

REM Create necessary directories
echo [INFO] Creating necessary directories...
if not exist "logs" mkdir logs
if not exist "uploads\dev" mkdir uploads\dev
if not exist "uploads\test" mkdir uploads\test
if not exist "data\postgres" mkdir data\postgres
if not exist "data\redis" mkdir data\redis
echo [SUCCESS] Directories created

REM Set up environment files
echo [INFO] Setting up environment configuration...
if not exist ".env" (
    (
        echo # Development Environment Configuration
        echo # Telangana Ball Badminton Association Website
        echo.
        echo # Database Configuration
        echo DB_HOST=localhost
        echo DB_PORT=5432
        echo DB_NAME=telangana_ball_badminton_dev
        echo DB_USERNAME=tbba_user
        echo DB_PASSWORD=tbba_password_2024
        echo.
        echo # Redis Configuration
        echo REDIS_HOST=localhost
        echo REDIS_PORT=6379
        echo REDIS_PASSWORD=redis_password_2024
        echo.
        echo # JWT Configuration
        echo JWT_SECRET=development-jwt-secret-key-not-for-production-use-2024
        echo JWT_EXPIRATION=86400000
        echo JWT_REFRESH_EXPIRATION=604800000
        echo.
        echo # CORS Configuration
        echo CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,http://localhost:8080
        echo.
        echo # File Upload Configuration
        echo FILE_UPLOAD_DIR=./uploads/dev
        echo FILE_MAX_SIZE=10485760
        echo.
        echo # Mail Configuration ^(MailHog for development^)
        echo MAIL_HOST=localhost
        echo MAIL_PORT=1025
        echo MAIL_USERNAME=
        echo MAIL_PASSWORD=
        echo.
        echo # Logging Configuration
        echo LOG_FILE=./logs/application.log
    ) > .env
    echo [SUCCESS] Environment file created
) else (
    echo [WARNING] Environment file already exists, skipping creation
)

REM Start Docker services
echo [INFO] Starting Docker services...
docker-compose down >nul 2>&1

REM Try docker-compose first, then docker compose
docker-compose up -d postgres redis mailhog >nul 2>&1
if errorlevel 1 (
    docker compose up -d postgres redis mailhog >nul 2>&1
    if errorlevel 1 (
        echo [ERROR] Failed to start Docker services
        pause
        exit /b 1
    )
)
echo [SUCCESS] Docker services started

REM Wait for services to be ready
echo [INFO] Waiting for services to be ready...

REM Wait for PostgreSQL
echo [INFO] Waiting for PostgreSQL...
set timeout=60
:wait_postgres
docker exec tbba-postgres pg_isready -U tbba_user -d telangana_ball_badminton_dev >nul 2>&1
if errorlevel 1 (
    timeout /t 2 /nobreak >nul
    set /a timeout-=2
    if !timeout! gtr 0 goto wait_postgres
    echo [ERROR] PostgreSQL failed to start within 60 seconds
    pause
    exit /b 1
)
echo [SUCCESS] PostgreSQL is ready

REM Wait for Redis
echo [INFO] Waiting for Redis...
set timeout=30
:wait_redis
docker exec tbba-redis redis-cli ping >nul 2>&1
if errorlevel 1 (
    timeout /t 2 /nobreak >nul
    set /a timeout-=2
    if !timeout! gtr 0 goto wait_redis
    echo [ERROR] Redis failed to start within 30 seconds
    pause
    exit /b 1
)
echo [SUCCESS] Redis is ready

REM Setup frontend dependencies
echo [INFO] Setting up frontend dependencies...
cd ..\frontend

REM Check for npm or yarn
npm --version >nul 2>&1
if not errorlevel 1 (
    npm install
    echo [SUCCESS] Frontend dependencies installed with npm
) else (
    yarn --version >nul 2>&1
    if not errorlevel 1 (
        yarn install
        echo [SUCCESS] Frontend dependencies installed with yarn
    ) else (
        echo [ERROR] Neither npm nor yarn is installed. Please install Node.js and npm/yarn.
        pause
        exit /b 1
    )
)

cd ..\backend

REM Display final information
echo.
echo 🎉 Development environment setup completed successfully!
echo.
echo Services are running on:
echo   📊 Backend API:        http://localhost:8080
echo   🌐 Frontend:           http://localhost:3000
echo   🗄️  PostgreSQL:        localhost:5432
echo   🔄 Redis:              localhost:6379
echo   📧 MailHog Web UI:     http://localhost:8025
echo   📚 API Documentation:  http://localhost:8080/swagger-ui.html
echo   ❤️  Health Check:      http://localhost:8080/api/v1/public/health
echo.
echo To start the application:
echo   Backend:  gradlew bootRun
echo   Frontend: cd ..\frontend ^&^& npm start
echo.
echo To stop services:
echo   docker-compose down
echo.
echo Default login credentials:
echo   Username: admin
echo   Password: password
echo.

pause