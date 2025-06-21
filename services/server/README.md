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

## Spring Security Configuration

Spring Security is enabled and configured in this project (see `src/main/java/com/lecturebot/config/SecurityConfig.java`).
Key configurations include:

* BCrypt password hashing for user credentials.
* Public access permitted to `/api/auth/**` endpoints (e.g., for registration and login).
* All other endpoints require authentication by default.

## Building with Docker

1. Navigate to the `server` directory (where the `Dockerfile` is located).
2. Build the Docker image:

    ```bash
    docker build -t lecturebot-server:latest .
    ```

    *(The Dockerfile uses Gradle for the build process within the container).*

## Running with Docker Compose (Recommended for Local Development with Database)

For local development, it's easiest to manage the server and its database (PostgreSQL) using Docker Compose.

1. **The `docker-compose.yml` file** is provided in the `LECture-bot/server/` directory. It defines the `lecturebot-db` and `lecturebot-server` services.
    Key environment variables for the server are set within this file to configure database connections and JPA settings, which will override values in `application.properties`.

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

Expected Response:

```bash
{"status":"UP","message":"LECture-bot server is running!"}
```

#### 2. User Registration (Sign Up)

* **Endpoint:** POST /api/auth/register

* **Description:** Allows new users to create an account. Passwords will be securely hashed using BCrypt. \

**Request Body (JSON):**

* name: (String) User's full name. Required.
* email: (String) User's email address. Must be valid and unique. Required.
* password: (String) User's password. Min 8 characters. Required.

**Example curl for Successful Registration:**

```bash
curl -X POST -H "Content-Type: application/json" 

-d '{
"name": "Ada Lovelace",
"email": "ada.lovelace@example.com",
"password": "Password123!"
}' 

http://localhost:8080/api/auth/register
```

**Expected Success Response (200 OK):**

```text
Registration successful for ada.lovelace@example.com
```

**Example curl for Attempting to Register an Existing Email:**

```bash
curl -X POST -H "Content-Type: application/json" 

-d '{
"name": "Ada Lovelace",
"email": "ada.lovelace@example.com",
"password": "NewPassword456!"
}' 

http://localhost:8080/api/auth/register -v
```

**Expected Error Response (400 Bad Request):**

```text
Email already exists
```

**Other Validation Error Examples (400 Bad Request):**

* name: Name is required
* email: Email format is invalid
* password: Password must be at least 8 characters



### 3. User Login (Sign In)

* **Endpoint:** POST /api/auth/login

* **Description:** Allows users to log into their existing account.

**Request Body (JSON):**

* email: (String) User's email address. 
* password: (String) User's password. 

**Example curl for Successful Login:**

```bash
curl -X POST -H "Content-Type: application/json" 

-d '{
"email": "ada.lovelace@example.com",
"password": "Password123!"
}' 

http://localhost:8080/api/auth/login
```

**Expected Success Response (200 OK):**

```text
Login successful for ada.lovelace@example.com, token: <JWT> # currently set for debuging purposes
```

**Example curl for Failed Login:**

```bash
curl -X POST -H "Content-Type: application/json" -i \

-d '{ "email": "ada.lovelace@example.com", "password": "NewPassword456!"}' 
http://localhost:8080/api/auth/login 
```

**Expected Error Response (401 Unauthorized):**

```text
Bad Credentials (possible error message)
```

**Other Validation Error Examples (400 Bad Request):**

* email: Email format is invalid
* password: Password must be at least 8 characters

### 4. Manage User Profile (Update Profile & Change Password)

After logging in and obtaining your JWT token, you can update your profile information or change your password using the following endpoints. All requests require the `Authorization` header with your JWT as `Bearer <token>`.

#### a. Update Profile (Name/Email)

* **Endpoint:** PUT /api/users/me

**Request Body Example:**
```json
{
  "name": "New Name",
  "email": "new.email@example.com"
}
```

**Curl Example**
```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{"name": "New Name", "email": "new.email@example.com"}'
```

**Expected Response:**  
200 OK with the updated user object, or an error message if validation fails or the email is already taken.

Example successful response:
```json
{
  "id": 6,
  "email": "new.email@example.com",
  "passwordHash": "$2a$10$JCnEfokhtT4igB3manzAtuidgWwE3jlpSEsRfHgq/JXgvnmcoqOUO",
  "name": "New Name"
}
```

#### b. Change Password

* **Endpoint:** POST /api/users/me/change-password

**Request Body Example:**
```json
{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword456"
}
```
**Curl Example**
```bash
curl -X POST http://localhost:8080/api/users/me/change-password \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{"currentPassword": "oldpassword123", "newPassword": "newpassword456"}'
```

**Expected Response:**  
200 OK on success, or an error message if the current password is incorrect or validation fails.

#### c. Using the Frontend UI

- Log in via the frontend to obtain a JWT (handled automatically).
- Navigate to `/profile` to view and update your profile or change your password.
- All requests from the UI will automatically include the JWT if you are logged in.

**Note:**  
If you receive a 401 Unauthorized error, ensure your JWT is valid

