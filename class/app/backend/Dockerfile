# Build stage
FROM gradle:9.5-jdk21 AS builder

WORKDIR /app

# Copy build files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew build -x test

# Runtime stage
FROM openjdk:21-slim

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/build/libs/computify-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Start the app
CMD ["java", "-jar", "app.jar"]
