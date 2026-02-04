#!/bin/bash

echo "🔍 Testing Heroku PostgreSQL connection with Spring Boot..."

# Build the application
echo "🔨 Building application..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build successful!"

# Run the connection test
echo "🚀 Starting connection test application..."
echo "📝 This will only test the connection, no schema changes will be made"

java -jar build/libs/telangana-ball-badminton-association-*.jar \
    --spring.profiles.active=test-connection \
    --spring.main.class=com.telangana.ballbadminton.TestConnectionApplication \
    --server.port=8080

echo "🏁 Connection test completed!"