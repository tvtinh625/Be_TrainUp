# ========================================================
# STAGE 1: Build Java 21 Maven Multi-Module Project
# ========================================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy full source code
COPY . .

# Build api-portal-service and its dependent modules (-pl api-portal-service -am)
RUN mvn clean package -pl api-portal-service -am -DskipTests

# ========================================================
# STAGE 2: Lightweight Production Runtime Image (JRE 21 Alpine)
# ========================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a secure non-root user
RUN addgroup -S trainup && adduser -S trainup -G trainup

# Copy executable repackaged JAR from api-portal-service target directory
COPY --from=builder /app/api-portal-service/target/api-portal-service-*.jar app.jar

# Set permissions for non-root user
RUN chown -R trainup:trainup /app
USER trainup

# Expose default HTTP port
EXPOSE 8080

# Production JVM optimizations for cloud environments (Render, Railway, Docker)
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Duser.timezone=Asia/Ho_Chi_Minh"

# Run Spring Boot app with dynamic PORT binding
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
