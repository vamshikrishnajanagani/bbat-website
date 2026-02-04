#!/bin/bash

echo "🔍 Testing Heroku PostgreSQL connection..."

# Test the database connection using psql
DB_URL="postgres://uarq7169567h9a:p2a3d8927b39fd36827c371f06b66377c30f5c1c121f030bb9a1ec6f30990201b@c1erdbv5s7bd6i.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com:5432/df72l64ipgnnt9"

echo "📡 Attempting to connect to database..."

# Check if psql is available
if command -v psql &> /dev/null; then
    echo "✅ psql found, testing connection..."
    psql "$DB_URL" -c "SELECT version();" -c "SELECT current_database();" -c "SELECT current_user;"
    
    if [ $? -eq 0 ]; then
        echo "✅ Database connection successful!"
    else
        echo "❌ Database connection failed!"
        exit 1
    fi
else
    echo "⚠️  psql not found. Installing postgresql-client..."
    sudo apt-get update && sudo apt-get install -y postgresql-client
    
    if [ $? -eq 0 ]; then
        echo "✅ postgresql-client installed. Testing connection..."
        psql "$DB_URL" -c "SELECT version();" -c "SELECT current_database();" -c "SELECT current_user;"
    else
        echo "❌ Failed to install postgresql-client"
        exit 1
    fi
fi

echo "🎉 Database connection test complete!"