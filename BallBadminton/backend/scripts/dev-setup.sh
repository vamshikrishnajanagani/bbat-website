#!/bin/bash

# Development Environment Setup Script
# Telangana Ball Badminton Association Website

set -e

echo "🏸 Setting up Telangana Ball Badminton Association Website Development Environment"
echo "=============================================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is installed and running
check_docker() {
    print_status "Checking Docker installation..."
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi

    if ! docker info &> /dev/null; then
        print_error "Docker is not running. Please start Docker first."
        exit 1
    fi

    print_success "Docker is installed and running"
}

# Check if Docker Compose is available
check_docker_compose() {
    print_status "Checking Docker Compose..."
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose is not available. Please install Docker Compose."
        exit 1
    fi
    print_success "Docker Compose is available"
}

# Create necessary directories
create_directories() {
    print_status "Creating necessary directories..."
    
    mkdir -p logs
    mkdir -p uploads/dev
    mkdir -p uploads/test
    mkdir -p data/postgres
    mkdir -p data/redis
    
    print_success "Directories created"
}

# Set up environment files
setup_environment() {
    print_status "Setting up environment configuration..."
    
    # Create .env file if it doesn't exist
    if [ ! -f .env ]; then
        cat > .env << EOF
# Development Environment Configuration
# Telangana Ball Badminton Association Website

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=telangana_ball_badminton_dev
DB_USERNAME=tbba_user
DB_PASSWORD=tbba_password_2024

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=redis_password_2024

# JWT Configuration
JWT_SECRET=development-jwt-secret-key-not-for-production-use-2024
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,http://localhost:8080

# File Upload Configuration
FILE_UPLOAD_DIR=./uploads/dev
FILE_MAX_SIZE=10485760

# Mail Configuration (MailHog for development)
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_USERNAME=
MAIL_PASSWORD=

# Logging Configuration
LOG_FILE=./logs/application.log
EOF
        print_success "Environment file created"
    else
        print_warning "Environment file already exists, skipping creation"
    fi
}

# Start Docker services
start_services() {
    print_status "Starting Docker services..."
    
    # Stop any existing services
    docker-compose down 2>/dev/null || true
    
    # Start services
    if command -v docker-compose &> /dev/null; then
        docker-compose up -d postgres redis mailhog
    else
        docker compose up -d postgres redis mailhog
    fi
    
    print_success "Docker services started"
}

# Wait for services to be ready
wait_for_services() {
    print_status "Waiting for services to be ready..."
    
    # Wait for PostgreSQL
    print_status "Waiting for PostgreSQL..."
    timeout=60
    while ! docker exec tbba-postgres pg_isready -U tbba_user -d telangana_ball_badminton_dev &> /dev/null; do
        sleep 2
        timeout=$((timeout - 2))
        if [ $timeout -le 0 ]; then
            print_error "PostgreSQL failed to start within 60 seconds"
            exit 1
        fi
    done
    print_success "PostgreSQL is ready"
    
    # Wait for Redis
    print_status "Waiting for Redis..."
    timeout=30
    while ! docker exec tbba-redis redis-cli ping &> /dev/null; do
        sleep 2
        timeout=$((timeout - 2))
        if [ $timeout -le 0 ]; then
            print_error "Redis failed to start within 30 seconds"
            exit 1
        fi
    done
    print_success "Redis is ready"
}

# Run database migrations
run_migrations() {
    print_status "Running database migrations..."
    
    # Build the application first
    ./gradlew build -x test
    
    # Run migrations using Flyway or Spring Boot
    ./gradlew flywayMigrate || print_warning "Flyway migration failed, will use JPA DDL"
    
    print_success "Database setup completed"
}

# Install frontend dependencies
setup_frontend() {
    print_status "Setting up frontend dependencies..."
    
    cd ../frontend
    
    if command -v npm &> /dev/null; then
        npm install
        print_success "Frontend dependencies installed with npm"
    elif command -v yarn &> /dev/null; then
        yarn install
        print_success "Frontend dependencies installed with yarn"
    else
        print_error "Neither npm nor yarn is installed. Please install Node.js and npm/yarn."
        exit 1
    fi
    
    cd ../backend
}

# Run tests to verify setup
run_tests() {
    print_status "Running tests to verify setup..."
    
    # Run backend tests
    ./gradlew test --info
    
    # Run frontend tests
    cd ../frontend
    if command -v npm &> /dev/null; then
        npm test -- --watchAll=false
    elif command -v yarn &> /dev/null; then
        yarn test --watchAll=false
    fi
    cd ../backend
    
    print_success "Tests completed successfully"
}

# Display final information
show_info() {
    echo ""
    echo "🎉 Development environment setup completed successfully!"
    echo ""
    echo "Services are running on:"
    echo "  📊 Backend API:        http://localhost:8080"
    echo "  🌐 Frontend:           http://localhost:3000"
    echo "  🗄️  PostgreSQL:        localhost:5432"
    echo "  🔄 Redis:              localhost:6379"
    echo "  📧 MailHog Web UI:     http://localhost:8025"
    echo "  📚 API Documentation:  http://localhost:8080/swagger-ui.html"
    echo "  ❤️  Health Check:      http://localhost:8080/api/v1/public/health"
    echo ""
    echo "To start the application:"
    echo "  Backend:  ./gradlew bootRun"
    echo "  Frontend: cd ../frontend && npm start"
    echo ""
    echo "To stop services:"
    echo "  docker-compose down"
    echo ""
    echo "Default login credentials:"
    echo "  Username: admin"
    echo "  Password: password"
    echo ""
}

# Main execution
main() {
    check_docker
    check_docker_compose
    create_directories
    setup_environment
    start_services
    wait_for_services
    setup_frontend
    
    # Optional: run migrations and tests
    if [ "$1" != "--skip-tests" ]; then
        run_migrations
        run_tests
    fi
    
    show_info
}

# Run main function with all arguments
main "$@"