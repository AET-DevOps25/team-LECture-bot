
# Server (LECture-bot Backend)

This directory contains the Spring Boot backend application for LECture-bot.

## Prerequisites

* Java 21 JDK
* Gradle (latest version recommended, or use the provided Gradle Wrapper ./gradlew)
* Docker & Docker Compose (for containerized build, run, and multi-container local development)

## Building and Running Locally (without Docker)

1. Navigate to the services/server directory.

2. Ensure your application.properties (or environment variables) are configured, especially for the database connection.

3. Build the application (this will also run tests; to skip tests, you can use ./gradlew build -x test):

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

Spring Security is enabled and configured in this project (see src/main/java/com/lecturebot/config/SecurityConfig.java). Key configurations include:

* BCrypt password hashing for user credentials.
* Public access permitted to /api/v1/auth/** and /api/v1/health endpoints.
* All other endpoints require JWT-based authentication by default.

## Building with Docker

1. Navigate to the services/server directory (where the Dockerfile is located).

2. Build the Docker image:

```bash
 docker build -t lecturebot-server:latest . 
```

(The Dockerfile uses Gradle for the build process within the container).

## Running with Docker Compose (Recommended for Local Development with Database)

For local development, it's easiest to manage the server and its database (PostgreSQL) using the main docker-compose.yml in the project root.

1. The docker-compose.yml file is located in the project root directory. It defines the server, client, genai-service, weaviate, and lecturebot-db services. Key environment variables for the server are set within this file to configure database connections and JPA settings, which will override values in application.properties.

2. Start the services using Docker Compose: From the project root directory, run:

```bash
 docker-compose up --build 
```

* --build ensures images are rebuilt if Dockerfiles have changed.
* To run in detached mode (in the background), use docker-compose up -d --build

3. Access the server: The server will be accessible at <http://localhost:8080>.

4. Stop the services:

```bash
 docker-compose down 
```

To also remove volumes (like the database data), use docker-compose down -v.

## Troubleshooting Database Issues

* PostgreSQL Version Incompatibility / Schema Issues: If the database or server fails to start due to database file incompatibility or schema validation errors, it often means the Docker volume (lecturebot_db_data) contains data from a previous or different database state.
* Solution: Stop services (docker-compose down), remove the conflicting Docker volume (e.g., docker volume rm <projectname>_lecturebot_db_data or docker volume rm lecturebot_db_data - use docker volume ls to find the exact name used by your setup), and then run docker-compose up --build -d again. This allows PostgreSQL to initialize a fresh database and run the init-users.sql script.


## Q&A Feature: Testing Instructions

This section explains how to test the Q&A (Ask Question About Course) feature via both the UI and API (using curl), including JWT authentication and expected responses.

### 1. Testing via the UI

1. Log in as an authenticated user.
2. Navigate to a course space.
3. Use the Q&A chat interface:
   - Type a natural language question in the input field.
   - Submit the question.
   - Observe the answer and any citations displayed in the chat bubbles.
   - If the answer cannot be generated, a fallback message will be shown.
   - Loading indicators and error messages are displayed as appropriate.

### 2. Testing via curl (API)

You can test the backend Q&A endpoint directly using curl. You must provide a valid JWT token in the Authorization header.

#### Example curl command

```bash
curl -X POST http://localhost:8080/api/v1/coursespaces/<COURSE_SPACE_ID>/query \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -d '{"queryText": "What is the course about?", "courseId": "<COURSE_SPACE_ID>"}'
```

- Replace `<COURSE_SPACE_ID>` with your actual courseSpaceId (e.g., `efa22df0-37ea-45e8-8f97-7c6eb7f1f555`).
- Replace `<YOUR_JWT_TOKEN>` with a valid JWT (obtainable from your app's login/session).

#### Expected Request Body

```json
{
  "queryText": "What is the course about?",
  "courseId": "efa22df0-37ea-45e8-8f97-7c6eb7f1f555"
}
```

#### Expected Response Structure

```json
{
  "answerText": "...answer string...",
  "citations": [
    {
      "document_id": "...",
      "document_name": "...",
      "page_number": 1,
      "context_snippet": "..."
    }
  ],
  "error": null
}
```

- If no answer can be generated, `answerText` will contain a fallback message and `citations` may be empty or contain nulls.
- If authentication fails, you will receive a 401 or 403 error.

### 3. Troubleshooting

- Ensure your JWT is valid and not expired.
- If you receive a static resource error, check your backend and gateway routing.
- If you receive a fallback answer, ensure your course space has documents/content indexed.

### 4. Additional Notes
docs(readme): add Q&A feature testing instructions with curl and UI examples

- Document how to test the Q&A endpoint via both the UI and curl
- Include JWT usage, example request/response, and troubleshooting tips
- Clarify expected request/response structure for Q&A API
- Satisfies documentation requirements for Q&A feature
- All Q&A endpoints require authentication (JWT).
- The UI and API both use the same backend endpoint for Q&A.
- For more details, see the main project README and the Q&A feature documentation.

---

## Testing API Endpoints

### 1. Health Check

A basic health check endpoint is available at: GET <http://localhost:8080/api/v1/health>

Using curl:

```bash
 curl http://localhost:8080/api/v1/health 
```

Expected Response:

```json {"status":"UP","message":"LECture-bot server is running!"}
```

### 2. User Registration (Sign Up)

* Endpoint: POST /api/v1/auth/register

* Description: Allows new users to create an account. Passwords will be securely hashed using BCrypt.

Request Body (JSON):

* name: (String) User's full name. Required.
* email: (String) User's email address. Must be valid and unique. Required.
* password: (String) User's password. Min 8 characters. Required.

Example curl for Successful Registration:

```bash
 curl -X POST -H "Content-Type: application/json"
-d '{ "name": "Ada Lovelace", "email": "ada.lovelace@example.com", "password": "Password123!" }'
http://localhost:8080/api/v1/auth/register 
```

Expected Success Response (200 OK):

```text
Registration successful for ada.lovelace@example.com
```

Example curl for Attempting to Register an Existing Email:

```bash
 curl -X POST -H "Content-Type: application/json" -v
-d '{ "name": "Ada Lovelace", "email": "ada.lovelace@example.com", "password": "NewPassword456!" }'
http://localhost:8080/api/v1/auth/register 
```

Expected Error Response (400 Bad Request):

```text
Email already exists
```

### 3. User Login (Sign In)

* Endpoint: POST /api/v1/auth/login

* Description: Allows users to log into their existing account and receive a JWT.

Request Body (JSON):

* email: (String) User's email address.
* password: (String) User's password.

Example curl for Successful Login:

```bash
 curl -X POST -H "Content-Type: application/json"
-d '{ "email": "ada.lovelace@example.com", "password": "Password123!" }'
http://localhost:8080/api/v1/auth/login 
```

Expected Success Response (200 OK):

```json {"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZGEubG92ZWxhY2VAZXhhbXBsZS5jb20iLCJpYXQiOjE2...etc..."}
```

Example curl for Failed Login:

```bash
 curl -X POST -H "Content-Type: application/json" -i
-d '{ "email": "ada.lovelace@example.com", "password": "WrongPassword!"}'
http://localhost:8080/api/v1/auth/login 
```

Expected Error Response (400 Bad Request):

```json
{"token":"Invalid credentials"}
```

### 4. Manage User Profile (Update Profile & Change Password)

a. **Update Profile (Name/Email)**
   - **Endpoint:** `PUT /api/v1/profile`
   - **`curl` Example:**
     ```bash
     curl -X PUT http://localhost:8080/api/v1/profile \
       -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
       -H "Content-Type: application/json" \
       -d '{"name": "New Name", "email": "new.email@example.com"}'
     ```

b. **Change Password**
   - **Endpoint:** `PATCH /api/v1/profile/password`
   - **`curl` Example:**
     ```bash
     curl -X PATCH http://localhost:8080/api/v1/profile/password \
       -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
       -H "Content-Type: application/json" \
       -d '{"old_password": "oldpassword123", "new_password": "newpassword456"}'
     ```

c. Using the Frontend UI

* Log in via the frontend to obtain a JWT (handled automatically).
* Navigate to /profile to view and update your profile or change your password.
* All requests from the UI will automatically include the JWT if you are logged in.
Note:
If you receive a 401 Unauthorized error, ensure your JWT is valid

## Testing Server + GenAI Integration Endpoints
The server includes endpoints under `/api/v1/genai/` to test communication with the `genai-service` via the `GenAiClient`.

### Temporarily Disabling Security for Test Endpoints (Development Only)

By default, Spring Security protects most endpoints. For easier testing of the `/api/v1/genai/**` endpoints without handling authentication tokens, you can temporarily modify the `SecurityConfig.java` in the server application.

File: services/server/src/main/java/com/lecturebot/config/SecurityConfig.java

Add .requestMatchers("/genai/**").permitAll() to your security configuration. Example:

```java
// Inside SecurityConfig.java, within the securityFilterChain method: http .csrf(csrf -> csrf.disable()) .authorizeHttpRequests(authz -> authz .requestMatchers("/genai/**").permitAll() // <<< ADD THIS LINE .requestMatchers("/auth/", "/health").permitAll() .anyRequest().authenticated() ) // ... other configurations ... 
```

Important: After adding this line, rebuild and restart the server service:

```bash
docker-compose up --build server
# or if all services are down:
docker-compose up --build
```

Remember to remove or properly secure this path before any production deployment.

Testing with `curl`
Ensure all services are running via docker-compose up --build.

1. Test Indexing Document via Server: This sends a request to the server, which then calls the genai-service.

```bash
 curl -X POST "http://localhost:8080/api/v1/genai/index" \
-H "Content-Type: application/json" \
-d '{ "document_id": "server-readme-test-doc-001", "course_space_id": "cs-readme-test-101", "text_content": "This is a test document sent via the server to the GenAI service for README instructions. It talks about Spring Boot and RestTemplate." }'
```

*Expected Output*: JSON response from the genai-service relayed by the server, or an error message if the call failed. Check server and genai-service logs.

2. Test Submitting Query via Server:

```bash
 curl -X POST "http://localhost:8080/api/v1/genai/query" \
-H "Content-Type: application/json" \
-d '{ "query_text": "What does this document about README instructions talk about?", "course_space_id": "cs-readme-test-101" }'
```

*Expected Output*: JSON response (answer and citations) from the genai-service relayed by the server. Check server and genai-service logs.


## 5. Manage Course Spaces
All endpoints in this section require authentication. You must include the Authorization: Bearer <YOUR_JWT_TOKEN> header in your requests.

1. Get All Course Spaces


`curl` Example:
```bash
curl -X GET http://localhost:8080/api/v1/coursespaces \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```
*Expected Success Response (200 OK)*: 
```json
[
    {
        "id": "generated-uuid-1",
        "name": "Introduction to AI"
    },
    {
        "id": "generated-uuid-2",
        "name": "Software Engineering"
    }
]
```


---

## 6. Manage Course Spaces: Frontend UI & Logic

The LECture-bot frontend provides a user-friendly interface for managing course spaces, allowing authenticated users to create, view, edit, and delete their course spaces. This functionality is accessible from the dashboard and is designed for efficient organization of lecture materials.

### Features

- **Create Course Space:** Users can create a new course space using a modal form with fields for title (required) and description (optional). Validation ensures the title is not empty.
- **Edit Course Space:** Existing course spaces can be edited via the same modal, pre-filled with current data. Changes are saved and reflected in the list.
- **Delete Course Space:** Users can delete a course space after confirming the action. The course space is removed from the list upon success.
- **List Course Spaces:** All course spaces for the authenticated user are displayed in a dashboard view, showing key details for each space.
- **Feedback & Validation:** The UI provides clear feedback for all actions, including success and error messages, and prevents invalid submissions.

### How to Use

1. **Access the Dashboard:** Log in and navigate to the dashboard to see your course spaces.
2. **Create:** Click "Create New Course Space", fill in the form, and submit. The new course space will appear in your list.
3. **Edit:** Click the edit button on a course space, update the details, and submit. Changes are saved and shown immediately.
4. **Delete:** Click the delete button, confirm the action, and the course space will be removed.
5. **Validation:** Attempting to submit an empty title will show an error. Backend errors (e.g., unauthorized, duplicate) are also displayed.

### Technical Details

- The frontend uses React context to manage course space state and API interactions.
- All API requests are authenticated using JWT (handled automatically if logged in).
- The UI is styled with TailwindCSS for a modern look.
- API endpoints used:
  - `GET /api/v1/coursespaces` — fetch all course spaces
  - `POST /api/v1/coursespaces` — create new course space
  - `PUT /api/v1/coursespaces/{id}` — update course space
  - `DELETE /api/v1/coursespaces/{id}` — delete course space

### Testing

To test course space management:

1. Log in via the frontend.
2. Create, edit, and delete course spaces using the dashboard UI.
3. Verify that the list updates immediately after each action.
4. Try submitting invalid data (e.g., empty title) to see validation in action.
5. Simulate backend errors (e.g., by using an expired JWT) to verify error handling.
6. Refresh the page to confirm the list persists (fetched from backend).

All actions require a valid JWT and are only available to authenticated users.


2. Create a new CourseSpace

- Endpoint: `POST /api/v1/coursespaces`
- Description: Creates a new course space for the user.
- Request Body (JSON):
- name: (String) The name of the new course space. Required.

`curl` Example:
```bash
curl -X POST http://localhost:8080/api/v1/coursespaces \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name": "Advanced Algorithms"}'
```

*Expected Success Response (201 Created):*
```json
{
    "id": "newly-generated-uuid",
    "name": "Advanced Algorithms"
}
```

3. Delete a Course Space
- Endpoint: `DELETE /api/v1/coursespaces/{courseSpaceId}`
- Description: Deletes a specific course space by its ID.
- `curl` Example:
```bash
curl -X DELETE http://localhost:8080/api/v1/coursespaces/{newly-generated-uuid} \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

*Expected Success Response (204 No Content):* An empty response with a 204 status code indicates successful deletion.



##6. Check OPENAPI docs
You can see the OpenAPI Swagger UI documentation by navigating to the following URL in your web browser once the server is running:

[`http://localhost:8080/api/v1/swagger-ui.html`](http://localhost:8080/api/v1/swagger-ui/index.html) (when started)
