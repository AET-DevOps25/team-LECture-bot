### `Backend Services`

# REST Services

This directory contains the REST services for the LECture-bot application.

## User-Course Microservice

The User-Course Microservice is responsible for managing users and courses. It provides endpoints for creating, retrieving, updating, and deleting users and courses.

### API Endpoints

#### User Registration

* **Endpoint:** `POST /api/v1/auth/register`
* **Description:** Allows new users to create an account.
* **Request Body (JSON):**
    * `name`: (String) User's full name.
    * `email`: (String) User's email address.
    * `password`: (String) User's password.
* **`curl` Example:**
    ```bash
    curl -X POST -H "Content-Type: application/json" \
    -d '{ "name": "Ada Lovelace", "email": "ada.lovelace@example.com", "password": "Password123!" }' \
    http://localhost:8080/api/v1/auth/register
    ```
* **Expected Success Response (200 OK):**
    ```text
    Registration successful for ada.lovelace@example.com
    ```

#### User Login

* **Endpoint:** `POST /api/v1/auth/login`
* **Description:** Allows users to log into their existing account and receive a JWT.
* **Request Body (JSON):**
    * `email`: (String) User's email address.
    * `password`: (String) User's password.
* **`curl` Example:**
    ```bash
    curl -X POST -H "Content-Type: application/json" \
    -d '{ "email": "ada.lovelace@example.com", "password": "Password123!" }' \
    http://localhost:8080/api/v1/auth/login
    ```
* **Expected Success Response (200 OK):**
    ```json
    {"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZGEubG92ZWxhY2VAZXhhbXBsZS5jb20iLCJpYXQiOjE2...etc..."}
    ```

#### Manage User Profile

* **Update Profile (Name/Email):**
    * **Endpoint:** `PUT /api/v1/profile`
    * **`curl` Example:**
        ```bash
        curl -X PUT http://localhost:8080/api/v1/profile \
          -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
          -H "Content-Type: application/json" \
          -d '{"name": "New Name", "email": "new.email@example.com"}'
        ```
* **Change Password:**
    * **Endpoint:** `PATCH /api/v1/profile/password`
    * **`curl` Example:**
        ```bash
        curl -X PATCH http://localhost:8080/api/v1/profile/password \
          -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
          -H "Content-Type: application/json" \
          -d '{"old_password": "oldpassword123", "new_password": "newpassword456"}'
        ```

#### Manage Course Spaces

* **Get All Course Spaces:**
    * **Endpoint:** `GET /api/v1/coursespaces`
    * **Description:** Retrieves a list of all course spaces for the currently authenticated user.
    * **`curl` Example:**
        ```bash
        curl -X GET http://localhost:8080/api/v1/coursespaces \
          -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
        ```
    * **Expected Success Response (200 OK):**
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
* **Create a new CourseSpace:**
    * **Endpoint:** `POST /api/v1/coursespaces`
    * **Description:** Creates a new course space for the user.
    * **Request Body (JSON):**
        * `name`: (String) The name of the new course space.
    * **`curl` Example:**
        ```bash
        curl -X POST http://localhost:8080/api/v1/coursespaces \
          -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
          -H "Content-Type: application/json" \
          -d '{"name": "Advanced Algorithms"}'
        ```
    * **Expected Success Response (201 Created):**
        ```json
        {
            "id": "newly-generated-uuid",
            "name": "Advanced Algorithms"
        }
        ```
* **Delete a Course Space:**
    * **Endpoint:** `DELETE /api/v1/coursespaces/{courseSpaceId}`
    * **Description:** Deletes a specific course space by its ID.
    * **`curl` Example:**
        ```bash
        curl -X DELETE http://localhost:8080/api/v1/coursespaces/{newly-generated-uuid} \
          -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
        ```
    * **Expected Success Response (204 No Content):** An empty response with a 204 status code indicates successful deletion.

## GenAI Backend Microservice

The GenAI Backend Microservice is responsible for handling all interactions with the GenAI service. It provides endpoints for indexing documents, submitting queries, and generating flashcards.

### Testing with `curl`

1.  **Test Indexing Document via Server:** This sends a request to the server, which then calls the genai-service.

    ```bash
    curl -X POST "http://localhost:8080/api/v1/genai/index" \
    -H "Content-Type: application/json" \
    -d '{ "document_id": "server-readme-test-doc-001", "course_space_id": "cs-readme-test-101", "text_content": "This is a test document sent via the server to the GenAI service for README instructions. It talks about Spring Boot and RestTemplate." }'
    ```

    *Expected Output*: JSON response from the genai-service relayed by the server, or an error message if the call failed.

2.  **Test Submitting Query via Server:**

    ```bash
    curl -X POST "http://localhost:8080/api/v1/genai/query" \
    -H "Content-Type: application/json" \
    -d '{ "query_text": "What does this document about README instructions talk about?", "course_space_id": "cs-readme-test-101" }'
    ```

    *Expected Output*: JSON response (answer and citations) from the genai-service relayed by the server.

### OpenAPI Docs

You can see the OpenAPI Swagger UI documentation by navigating to the following URL in your web browser once the server is running:

[`http://localhost:8080/api/v1/swagger-ui.html`](http://localhost:8080/api/v1/swagger-ui/index.html)
