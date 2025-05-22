# Server (LECture-bot Backend)

This directory contains the Spring Boot backend application for LECture-bot.

## Prerequisites

* Java 21 JDK
* Gradle (latest version recommended, or use the provided Gradle Wrapper `./gradlew`)
* Docker & Docker Compose (for containerized build, run, and multi-container local development)

## Building and Running Locally (without Docker)

1. Navigate to the `server` directory.
2. Ensure your `application.properties` (or environment variables) are configured, especially for the database connection.
3. Build the application (this will also run tests; to skip tests, you can use `./gradlew build -x test`):

    ```bash
    ./gradlew build
    ```

4. Run the application:

    ```bash
    ./gradlew bootRun
    ```

    Alternatively, after building, you can run the JAR directly:

    ```bash
    java -jar build/libs/lecturebot-server-0.0.1-SNAPSHOT.jar 
    ```

    The server will typically start on port 8080.

## Managing Spring Security (for Initial Testing)

By default, if `spring-boot-starter-security` is included in your `build.gradle`, Spring Security will protect all endpoints. For initial testing, especially of the health endpoint, the simplest approach is to temporarily exclude this dependency:

1. In your `server/build.gradle`, comment out or remove the security dependencies:

    ```gradle
    // implementation 'org.springframework.boot:spring-boot-starter-security'
    // testImplementation 'org.springframework.security:spring-security-test'
    ```

2. Refresh your Gradle project (e.g., in IntelliJ/VSCode or by running `./gradlew clean build --refresh-dependencies` or simply re-importing the Gradle project in your IDE).
3. Restart your application. All endpoints will now be open.

**Important: Remember to re-enable and properly configure security before implementing features that handle user data or require authentication.**

## Building with Docker

1. Navigate to the `server` directory (where the `Dockerfile` is located).
2. Build the Docker image:

    ```bash
    docker build -t lecturebot-server:latest .
    ```

    *(The Dockerfile uses Gradle for the build process within the container).*

## Running with Docker Compose (Recommended for Local Development with Database)

For local development, it's easiest to manage the server and its database (PostgreSQL) using Docker Compose.

1. **Create a `docker-compose.yml` file** in the `LECture-bot/server/` directory (or at the project root if you plan to include client and genai services later):

    ```yaml
    version: '3.8'

    services:
      lecturebot-db:
        image: postgres:16 # Or your preferred PostgreSQL version
        container_name: lecturebot-postgres-db
        environment:
          POSTGRES_DB: lecturebot_db
          POSTGRES_USER: lecturebot_user
          POSTGRES_PASSWORD: mysecretpassword # Change this in a real scenario
        ports:
          - "5432:5432" # Expose PostgreSQL port to host (optional, for external tools)
        volumes:
          - lecturebot_db_data:/var/lib/postgresql/data # Persistent storage for DB
        networks:
          - lecturebot-net

      lecturebot-server:
        build:
          context: . # Assumes docker-compose.yml is in the server directory
          dockerfile: Dockerfile
        container_name: lecturebot-app-server
        ports:
          - "8080:8080"
        environment:
          # These environment variables will override application.properties in the container
          - SERVER_PORT=8080
          - SPRING_DATASOURCE_URL=jdbc:postgresql://lecturebot-db:5432/lecturebot_db
          - SPRING_DATASOURCE_USERNAME=lecturebot_user
          - SPRING_DATASOURCE_PASSWORD=mysecretpassword # Should match POSTGRES_PASSWORD above
          - SPRING_JPA_HIBERNATE_DDL_AUTO=update # Or create-drop for dev, validate for prod
        depends_on:
          - lecturebot-db
        networks:
          - lecturebot-net

    volumes:
      lecturebot_db_data: # Defines a named volume for data persistence

    networks:
      lecturebot-net:
        driver: bridge
    ```

2. **Start the services using Docker Compose:**
    From the directory containing your `docker-compose.yml` file, run:

    ```bash
    docker-compose up --build
    ```

    * `--build` ensures images are rebuilt if Dockerfiles have changed.
    * To run in detached mode (in the background), use `docker-compose up -d --build`.

3. **Access the server:**
    The server will be accessible at `http://localhost:8080`.

4. **Stop the services:**

    ```bash
    docker-compose down
    ```

    To also remove volumes (like the database data), use `docker-compose down -v`.

### Testing API Endpoints

#### 1. Health Check

A basic health check endpoint is available at:
`GET http://localhost:8080/api/health`

**Using curl:**

```bash
curl http://localhost:8080/api/health
```

**Expected Response:**

```json
{"status":"UP","message":"LECture-bot server is running!"}
```

#### 2. User Login

The login endpoint is available at:
`POST http://localhost:8080/api/auth/login`

**Prerequisites:**

* Ensure the server is running (either locally via `./gradlew bootRun` or via Docker/Docker Compose).
* Ensure users exist in the database. If using Docker Compose with the `init-users.sql` script, the 5 sample users will be created automatically on the first run with an empty database volume.

**Using curl:**
Replace `user@example.com` and `password123` with credentials of a user present in your database.

```bash
curl -X POST -H "Content-Type: application/json" \
-d '{
  "email": "carlos@example.com",
  "password": "carlospass"
}' \
http://localhost:8080/api/auth/login
```

**Expected Success Response (current simplified version):**

```json
{
  "message": "Login successful (SIMPLIFIED - NO JWT, NO HASHING!)",
  "token": "placeholder-jwt-token-for-carlos@example.com",
  "user": {
    "id": 3, // Example ID
    "email": "carlos@example.com",
    "name": "Carlos Mejia"
  }
}
```

**Expected Failure Response (e.g., wrong password or user not found):**

```json
{
  "message": "Error: Invalid password." 
  // or "Error: User not found with email: incorrect@example.com"
}
```

**Important Reminders for Login:**

* The current login implementation uses **plain text password comparison**. This is highly insecure and for initial setup/testing only.
* Proper password hashing (e.g., BCrypt) must be implemented.
* JWT generation and validation for stateless authentication will be the next step after basic login works.

### API Documentation

API documentation will be provided via Swagger/OpenAPI. Once implemented (by adding the `springdoc-openapi-starter-webmvc-ui` dependency to `build.gradle` and basic configuration), it will typically be accessible at `/swagger-ui.html` or `/v3/api-docs`.

### Project Structure

* `src/main/java/com/lecturebot`: Main Java source code.
  * `config`: Spring configuration classes.
  * `controller`: REST API controllers.
  * `dto`: Data Transfer Objects.
  * `entity`: JPA entities.
  * `repository`: Spring Data JPA repositories.
  * `security`: Spring Security configuration (like `SecurityConfig.java`).
  * `service`: Business logic services.
* `src/main/resources`: Application resources.
  * `application.properties`: Main application configuration.
  * `static/`: Static web resources (if any).
  * `templates/`: Server-side templates (if any).
* `db-init/`: Contains SQL scripts for database initialization.
  * `init-users.sql`: Script to create initial users.
* `build.gradle`: Gradle project configuration file.
* `settings.gradle`: Gradle settings file (if present, typically defines project name).
* `gradlew`, `gradlew.bat`: Gradle wrapper scripts.
* `gradle/`: Gradle wrapper helper files.
* `Dockerfile`: Instructions to build the Docker image for the server.
* `docker-compose.yml`: (Recommended) For multi-container local development.
