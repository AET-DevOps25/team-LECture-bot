# API Directory Overview

This directory contains the OpenAPI specifications and code generation scripts for the Team LECture Bot platform. Each file documents the interfaces for a major subsystem, enabling consistent API design, automated code generation, and clear integration points between microservices and the frontend.

## Contents

- `openapi.yaml` — Main API specification for the LECture-bot application, aggregating core endpoints and schemas.
- `user-course-openapi.yaml` — API specification for the User & Course microservice, covering user authentication, profile management, and course space operations.
- `genai-backend-openapi.yaml` — API specification for the GenAI microservice, including endpoints for document indexing, querying, and flashcard generation.
- `document-api.yaml` — API specification for the Document microservice, detailing endpoints for uploading, listing, and deleting documents within course spaces.
- `scripts/` — Contains code generation scripts and configuration files for generating server and client code from OpenAPI specs.
    - `gen-all-v2.sh` — Bash script to generate backend (Java Spring Boot), frontend (TypeScript), and Python schemas from OpenAPI specs for all services.
    - `openapitools.json` — Configuration for the OpenAPI Generator CLI version and options.

## How to Use

- **View API Contracts:**
  - Each `.yaml` file is an OpenAPI 3.0.3 specification. You can view these files in Swagger UI, Redoc, or any OpenAPI-compatible tool to explore endpoints, request/response schemas, and authentication requirements.

- **Generate Code:**
  - Run `scripts/gen-all-v2.sh` from the project root to generate server stubs, client libraries, and Python models for all subsystems. See comments in the script for details.

- **Subsystems & Interfaces:**
  - **User & Course Microservice:**
    - Spec: `user-course-openapi.yaml`
    - Endpoints: User registration, login, profile, password change, course space CRUD, Q&A for course spaces.
  - **GenAI Microservice:**
    - Spec: `genai-backend-openapi.yaml`
    - Endpoints: Document indexing/de-indexing, RAG queries, flashcard generation.
  - **Document Microservice:**
    - Spec: `document-api.yaml`
    - Endpoints: PDF upload, document listing, document deletion.
  - **Main API:**
    - Spec: `openapi.yaml`
    - Aggregates and documents the overall application API, including health checks, authentication, profile, course spaces, documents, and GenAI endpoints.

## Best Practices

- Keep OpenAPI specs up to date with implementation changes.
- Use code generation scripts to avoid manual errors and ensure consistency across services.
- Document authentication requirements and error responses in each spec.
- Use examples in request/response schemas to clarify expected payloads.

## References

- [OpenAPI Specification](https://swagger.io/specification/)
- [OpenAPI Generator CLI](https://openapi-generator.tech/)
- [openapi-typescript](https://github.com/drwpow/openapi-typescript)
- [datamodel-code-generator](https://github.com/koxudaxi/datamodel-code-generator)

---

For details on each subsystem's implementation and integration, see the corresponding service README files and the main project documentation.
