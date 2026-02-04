#!/bin/bash

echo "🗄️  Running database migrations on Heroku PostgreSQL..."

# Database connection details
DB_URL="postgres://uarq7169567h9a:p2a3d8927b39fd36827c371f06b66377c30f5c1c121f030bb9a1ec6f30990201b@c1erdbv5s7bd6i.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com:5432/df72l64ipgnnt9"

echo "📋 Available migration options:"
echo "1. Run Flyway migrations (recommended)"
echo "2. Run Hibernate schema update"
echo "3. Manual SQL execution"
echo ""

read -p "Choose option (1-3): " choice

case $choice in
    1)
        echo "🚀 Running Flyway migrations..."
        
        # Check if Flyway is installed
        if ! command -v flyway &> /dev/null; then
            echo "❌ Flyway not found. Installing..."
            
            # Download and install Flyway
            wget -qO- https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/9.22.3/flyway-commandline-9.22.3-linux-x64.tar.gz | tar xvz
            sudo mv flyway-9.22.3 /opt/flyway
            sudo ln -s /opt/flyway/flyway /usr/local/bin/flyway
        fi
        
        # Run migrations
        flyway -url="$DB_URL" -locations=filesystem:src/main/resources/db/migration migrate
        ;;
        
    2)
        echo "🔄 Running Hibernate schema update..."
        
        # Build and run with schema update
        ./gradlew clean build -x test
        
        java -jar build/libs/telangana-ball-badminton-association-*.jar \
            --spring.profiles.active=heroku-local \
            --spring.jpa.hibernate.ddl-auto=update \
            --spring.jpa.show-sql=true \
            --server.port=8081 &
        
        APP_PID=$!
        
        echo "⏳ Waiting for schema update to complete..."
        sleep 30
        
        echo "🛑 Stopping application..."
        kill $APP_PID
        
        echo "✅ Schema update completed!"
        ;;
        
    3)
        echo "📝 Manual SQL execution..."
        echo "Connect to the database using:"
        echo "psql \"$DB_URL\""
        echo ""
        echo "Then run your SQL commands manually."
        ;;
        
    *)
        echo "❌ Invalid option!"
        exit 1
        ;;
esac

echo "🎉 Migration process completed!"