# ========================================================
# STAGE 1: Build Java 21 Maven Multi-Module Project
# ========================================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy root pom.xml and all source code
COPY . .

# Package the multi-module project (skipping unit tests for fast build)
RUN mvn clean package -DskipTests

# ========================================================
# STAGE 2: Lightweight Runtime Image (Eclipse Temurin JRE 21)
# ========================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S trainup && adduser -S trainup -G trainup

# Copy compiled JAR from api-portal-service
COPY --from=builder /app/api-portal-service/target/*.jar app.jar

# Set permissions
RUN chown -R trainup:trainup /app
USER trainup

# Expose default port
EXPOSE 8080

# Configure JVM options for containerized environments
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Duser.timezone=Asia/Ho_Chi_Minh"

# Run Spring Boot application with dynamic PORT env variable support for Render/Heroku
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
