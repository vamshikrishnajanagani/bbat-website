#!/bin/bash

echo "🔍 Simple database connection test..."

# Test using Spring Boot with minimal configuration
echo "🚀 Starting minimal Spring Boot app to test connection..."

java -jar build/libs/telangana-ball-badminton-association-*.jar \
    --spring.profiles.active=test-connection \
    --spring.jpa.hibernate.ddl-auto=none \
    --spring.jpa.show-sql=false \
    --logging.level.org.hibernate=WARN \
    --logging.level.com.zaxxer.hikari=INFO \
    --server.port=8080 \
    --management.endpoints.web.exposure.include=health \
    --spring.main.lazy-initialization=true &

APP_PID=$!

echo "⏳ Waiting for application to start..."
sleep 15

echo "🔍 Testing health endpoint..."
curl -s http://localhost:8080/api/v1/health

echo ""
echo "🛑 Stopping application..."
kill $APP_PID

echo "✅ Connection test completed!"