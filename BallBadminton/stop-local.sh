#!/bin/bash

echo "🛑 Stopping Telangana Ball Badminton Association - Local Development"

# Stop and remove containers
docker-compose -f docker-compose.local.yml down

echo "✅ All services stopped and containers removed."