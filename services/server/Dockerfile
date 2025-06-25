# Stage 1: Build the application with Gradle using Java 21
# Using an official Gradle image that includes a JDK.
# Check Docker Hub for the latest Gradle image that bundles a JDK 21 compatible version or use a base JDK 21 image and rely on gradlew.
# For simplicity, using a general purpose JDK image and relying on gradlew.
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle ./gradle

# Copy the build configuration files
COPY build.gradle .

COPY src ./src

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies first to leverage Docker cache
RUN ./gradlew dependencies --no-daemon

# Copy the source code
COPY src ./src

# Build the application, creating the executable JAR, and skip tests
RUN ./gradlew build -x test --no-daemon

# Stage 2: Create the runtime image using JRE for a smaller footprint
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Expose the application port
EXPOSE 8080

# Copy the JAR file from the build stage
# Gradle typically places the JAR in build/libs/
# The exact JAR name might include the version, ensure this matches your build.gradle output
COPY --from=build /app/build/libs/lecturebot-server-0.0.1-SNAPSHOT.jar app.jar

# Environment variables for configuration (can be overridden at runtime)
ENV SERVER_PORT=8080
# ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/lecturebot_db # Example for docker-compose
# ENV SPRING_DATASOURCE_USERNAME=lecturebot_user
# ENV SPRING_DATASOURCE_PASSWORD=secret

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: Add a healthcheck (adjust as needed)
# HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
#   CMD curl -f http://localhost:${SERVER_PORT}/api/health || exit 1
