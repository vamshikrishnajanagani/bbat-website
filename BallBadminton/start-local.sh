#!/bin/bash

echo "🐳 Starting Telangana Ball Badminton Association - Local Development"
echo "📡 Using Heroku PostgreSQL Database"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Build and start the services
echo "🔨 Building and starting services..."
docker-compose -f docker-compose.local.yml up --build

echo "🏁 Services stopped."