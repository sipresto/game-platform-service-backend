# Multi-stage build for Spring Boot application
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /build

# Copy Maven wrapper and pom.xml first (better caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable and fix line endings (important for Windows)
RUN chmod +x mvnw && \
    sed -i 's/\r$//' mvnw

# Download dependencies (cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests

# Production stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8095

ENTRYPOINT ["java", "-jar", "app.jar"]