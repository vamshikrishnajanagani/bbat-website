# Multi-stage Dockerfile for Telangana Ball Badminton Association Website Backend
# Optimized for production deployment with security and performance considerations

# Build stage
FROM gradle:8.5-jdk17-alpine AS build

# Set working directory
WORKDIR /app

# Copy Gradle configuration files
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY gradle gradle
COPY gradlew .

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew clean build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine AS runtime

# Install additional packages for production
RUN apk add --no-cache \
    curl \
    tzdata \
    && rm -rf /var/cache/apk/*

# Set timezone
ENV TZ=Asia/Kolkata
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create application user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy JAR file from build stage
COPY --from=build /app/build/libs/telangana-ball-badminton-association-*.jar app.jar

# Create directories for logs and uploads
RUN mkdir -p /app/logs /app/uploads && \
    chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/v1/public/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+OptimizeStringConcat \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.backgroundpreinitializer.ignore=true"

# Application configuration
ENV SPRING_PROFILES_ACTIVE=production
ENV SERVER_PORT=8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Labels for metadata
LABEL maintainer="Telangana Ball Badminton Association <admin@telanganaballbadminton.org>"
LABEL version="1.0.0"
LABEL description="Official backend API for Telangana Ball Badminton Association Website"
LABEL org.opencontainers.image.source="https://github.com/telangana-ball-badminton/website"
LABEL org.opencontainers.image.documentation="https://api.telanganaballbadminton.org/swagger-ui.html"