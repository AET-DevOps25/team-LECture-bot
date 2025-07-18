
# Document Microservice

This is the Document Microservice for the LectureBot platform. It is responsible for handling PDF document uploads, processing, and requesting indexing and de-indexing for each course space.

## What it is
- A Spring Boot microservice that manages PDF documents for courses.
- Provides REST API endpoints for uploading, listing, and deleting documents.
- Integrates with a PostgreSQL database for document storage and status tracking.
- Supports JWT-based authentication and CORS for secure access.

## What it does
- Accepts PDF uploads (single or multiple) for a given course space.
- Extracts and cleans text from PDFs for further processing and indexing by the GenAI microservice.
- Tracks document processing status (pending, processing, completed, failed).
- Prevents duplicate uploads and enforces file size limits (max 50MB per file and per request).
- Provides endpoints to list all documents for a course and to delete a document (with automatic de-indexing in GenAI).

## How to use it

### Prerequisites
- Java 17+
- Docker (for running PostgreSQL and other services)
- Node.js (for frontend, if needed)

### Setup
1. **Configure Environment:**
   - Set environment variables as needed (e.g., `LECTUREBOT_CLIENT_ORIGIN`, JWT secret).
   - Edit `application.yml` for database and service configuration.
2. **Start Database:**
   - Run `docker-compose up -d lecturebot-postgres-db` from the project root.
3. **Build and Run the Service:**
   - From this folder, run: `./mvnw spring-boot:run` (or use your IDE)
   - The service will start on port 8083 by default.
4. **API Usage:**
   - Upload documents: `POST /api/v1/documents/{courseSpaceId}` (multipart/form-data)
   - List documents: `GET /api/v1/documents/{courseSpaceId}`
   - Delete document: `DELETE /api/v1/documents/{courseSpaceId}/{id}`
   - See `api/document-api.yaml` for full OpenAPI contract.

### Notes
- Only PDF files are accepted.
- Each file and the total upload must not exceed 50MB.
- JWT authentication is required for most endpoints.
- For development, CORS is enabled for localhost and configured origins.
- When deleting a document, the service will first attempt to de-index the document from the GenAI service. If de-indexing fails, the document will not be deleted from the database.

### Error Types

The API returns standard HTTP status codes for error handling:

- **400 Bad Request:** Invalid input (e.g., missing or malformed parameters, wrong file type).
- **401 Unauthorized:** Authentication is required or the JWT is missing/invalid.
- **409 Conflict:** The document was already uploaded in this course space (duplicate upload).
- **422 Unprocessable Entity:** PDF cannot be processed (may be empty, unreadable, or use unsupported encoding).
- **500 Internal Server Error:** General server error (unexpected failure). This status is also returned if the GenAI service returns an error (such as a 500 or other non-successful response).

Clients should handle these status codes and display appropriate messages to users.

---
For more details, see the source code and OpenAPI spec in the `api/` folder.
