# LECture-bot: Full-Stack Application with GenAI Integration

Welcome to LECture-bot! This project involves the design, development, deployment, and scaling of a lightweight but technically complete web application with a meaningful Generative AI (GenAI) integration.

## Project Overview & Objectives

This project aims to apply modern DevOps principles including containerization, CI/CD automation, cloud-native deployment, observability, and AI integration. The system is built as a client-server application, integrates a GenAI component, and is designed for collaborative development with documented ownership and clear workflows.

### Problem Addressed

Students across various university courses often struggle to efficiently locate specific information within a multitude of course materials like lecture slides, research papers, and textbooks. This application aims to solve this by providing a centralized, intelligent query interface for any uploaded course resources. Its main functionality is to allow users to upload diverse course documents (e.g., PDFs, Markdown files, text files) and then ask natural language questions about the content. The intended users are students enrolled in any university or online course seeking a faster way to find information and understand complex topics, ultimately saving significant study time and improving comprehension of their study materials. GenAI will be meaningfully integrated using LangChain and a vector database to implement Retrieval-Augmented Generation (RAG), enabling the assistant to answer user queries based *only* on the provided course-specific documents, with support for both cloud-based (e.g., OpenAI API) and local Large Language Models (LLMs).

### Main Functionalities

* User can sign up, log in, and manage their profile (e.g., associate with specific courses).
* User can create "course spaces" or collections to organize materials. (TBA)
* User can upload various document types (PDF, MD, TXT) into a selected course space.
* System efficiently processes and indexes uploaded documents, making them immediately available for querying.
* System provides clear feedback to the user during document upload, processing, and indexing stages (e.g., progress indicators, success/error messages).

### GenAI Integration

The GenAI service enables users to ask natural language questions about uploaded course documents and receive answers synthesized *only* from that specific content. This is achieved through a Retrieval-Augmented Generation (RAG) pipeline:

* **Indexing:** Documents are chunked, embedded, and stored in Weaviate with metadata.
* **Querying/Retrieval:** User queries are embedded, and relevant chunks are retrieved from Weaviate based on semantic similarity within the specified course space.
* **Generation:** A prompt, constructed from the query and retrieved context, is sent to a configured LLM (OpenAI by default, or a local Ollama instance) to generate an answer.
* **Citation:** The service aims to provide citations linking answers back to source documents.

The GenAI component is a modular Python microservice using FastAPI and LangChain, with configuration managed via environment variables.

### Key Scenarios

### Document Upload and Indexing for a New Course

* A student logs into LECture-bot.
* They create a new "course space" (e.g., "Advanced Algorithms CS701").
* The student uploads lecture slides (PDFs) and textbook chapters (TXT files) into this space.
* The system processes these documents, chunking text, generating embeddings, and storing them in Weaviate, associated with the "CS701" course space.

### Asking a Specific Question about Course Content

* While studying, the student navigates to their "CS701" course space.
* They ask: "What are the main applications of Dijkstra's algorithm discussed in the lectures?"
* The GenAI service retrieves relevant information from the indexed "CS701" documents and uses the configured LLM to generate an answer based solely on this context.
* The answer is displayed with citations to the source materials.

### Querying with a Different LLM Provider (e.g., Local Ollama)

* The system administrator (or user, if allowed by design) reconfigures the GenAI service (via `.env` file) to use a locally running Ollama model instead of OpenAI.
* The student asks another question. The RAG process is the same, but the LLM interaction now uses the local Ollama instance.

## Architecture & Technical Stack

### System Components

The system is divided into the following technical components:

* **Client:** A frontend application built with React.
* **Server:** A Spring Boot REST API.
* **GenAI Service:** A Python microservice utilizing LangChain.
* **Vector Database:** Weaviate.
* **Database:** PostgreSQL (or MongoDB as per project options, though PostgreSQL is used in current setup).

### Key Technologies

* **Frontend:** React, Vite, TypeScript.
* **Backend Server:** Java 21, Spring Boot, Spring Data JPA, Spring Security, Gradle.
* **GenAI Service:** Python, LangChain.
* **Database:** PostgreSQL.
* **Vector Database:** Weaviate.
* **Containerization:** Docker, Docker Compose.

### Architecture Diagrams

Detailed architecture, including UML diagrams (Analysis Object Model, Use Cases, Top-Level Component Diagram/Subsystem Decomposition), should be available in the project's `/docs` directory. These diagrams visualize the system structure and interactions.

## API-Driven Development Workflow

This project follows an **API-Driven Design** methodology. This means the `api/openapi.yaml` specification is the single source of truth for the entire application's API contract. All server and client code that handles API communication is *generated* from this file.

**The Golden Rule:** Before implementing any feature that involves API changes, you **must** first update the `api/openapi.yaml` specification.

**Workflow for API Changes:**

**Define the Contract:** Modify `api/openapi.yaml` to add or change endpoints, schemas, or parameters. Ensure your changes are well-documented and follow OpenAPI standards. **Lint the Specification (Optional but Recommended):** Before generating code, you can validate your changes using a linter. Our CI pipeline does this automatically, but you can run it locally:

You might need to install it first:

```bash
sudo npm install -g @redocly/cli --force
sudo redocly lint api/openapi.yaml
```

**Generate Code:** Run the master script from the project root to regenerate the server interfaces and the client-side API library.

```bash
sudo chmod +x ./api/scripts/gen-all.sh
./api/scripts/gen-all.sh
```

This script will:

* Generate Java interfaces and models for the Spring Boot server.
* Generate a TypeScript client library for the React frontend. **Implement the Logic:**
* **On the server:** Your controller classes will now need to implement the newly generated Java interfaces.
* **On the client:** Use the new functions and types available in the generated TypeScript client to build your UI features. **Commit Everything:** Commit the changes to `openapi.yaml` **along with all the newly generated code**. This is crucial for ensuring the repository is always in a consistent state. This process ensures that the frontend and backend are always aligned, reduces integration bugs, and provides clear, type-safe contracts for development.

## Project Structure

The project is organized into services and a central API definition, following modern microservice best practices.

* `/api`: The heart of our API-Driven Design.
* `openapi.yaml`: The single source of truth for the entire API contract.
* `scripts/gen-all.sh`: The master script to generate code for all services.
* `/services`: Contains the source code for each independent microservice.
* `/client`: The frontend React application.
* `/server`: The backend Spring Boot application.
* `/genai`: The Python-based GenAI service.
* `/docker-compose.yml`: Orchestrates all services for a complete local development environment.
* `/.github/workflows`: Contains CI/CD pipeline definitions for GitHub Actions.
* `/docs`: Contains additional documentation like architecture diagrams and problem statements.

## Getting Started: Running the Full Application (with Root Docker Compose)

The primary way to run the LECture-bot application (frontend, backend, database, GenAI service, and Weaviate) is by using the `docker-compose.yml` file located in this project root directory. By default, this setup configures the GenAI service to use OpenAI.

### 1. Clone the Repository (If you haven't already)

### 2. Environment Configuration (Optional but Recommended)

You can create a `.env` file in this root directory to manage environment variables for the services. Docker Compose will automatically pick it up. This is **highly recommended** for API keys.

Example `.env` file:

```dotenv

This is an example .env file. Copy to .env in the project root and fill in your values.
--- Database Credentials & Settings ---
DB_USER=lecturebot_user DB_PASSWORD=myverysecurepassword # Change this! DB_NAME=lecturebot_db

--- Spring Boot / JPA Settings for Server ---
SPRING_JPA_HIBERNATE_DDL_AUTO=none # (e.g., validate, update, none). 'validate' is good if init-users.sql manages schema. SERVER_PORT=8080

--- Client URLs for CORS configuration on the server ---
LECTUREBOT_CLIENT_ORIGIN=http://localhost:3000
LECTUREBOT_CLIENT_ORIGIN=<http://localhost:3000>
--- GenAI Service Configuration ---
Select the provider: "openai", "ollama", or "tum_aet"
LLM_PROVIDER=openai

-- OpenAI Settings (if LLM_PROVIDER is "openai") --
OPENAI_API_KEY=sk-your-actual-openai-api-key-here OPENAI_MODEL_NAME=gpt-4o-mini

-- TUM AET Settings (if LLM_PROVIDER is "tum_aet") --
TUM_AET_LLM_API_BASE=https://gpu.aet.cit.tum.de/api TUM_AET_LLM_API_KEY=sk-your-tum-aet-key-here TUM_AET_LLM_MODEL_NAME=llama3.3:latest
TUM_AET_LLM_API_BASE=<https://gpu.aet.cit.tum.de/api>
TUM_AET_LLM_API_KEY=sk-your-tum-aet-key-here
TUM_AET_LLM_MODEL_NAME=llama3.3:latest
-- Ollama Settings (if LLM_PROVIDER is "ollama") --
OLLAMA_BASE_URL=<http://host.docker.internal:11434> # For Docker Desktop on Mac/Windows
OLLAMA_BASE_URL=http://host.docker.internal:11434 # For Docker Desktop on Mac/Windows
```

If this file is not present, the defaults specified in `docker-compose.yml` will be used. **You must provide an `OPENAI_API_KEY` if using the default OpenAI setup.**

### 3. Build and Start All Services

Navigate to the project root directory (where the main `docker-compose.yml` is located) and run:

```bash
docker-compose up --build
```

* `--build`: Ensures that Docker images for the client, server, and genai-service are built (or rebuilt if their Dockerfiles or source code have changed).
* To run in detached mode (in the background), add the `-d` flag: `docker-compose up --build -d`

### 4. Accessing the Services

Once all containers are up and running:

* **Frontend Application:** Open your browser and go to http://localhost:3000 (this port is mapped from the client container in `docker-compose.yml`).
* **Backend API Server:** Accessible at http://localhost:8080 (this port is mapped from the server container).
* **GenAI Service API:** Accessible at http://localhost:8001 (this port is mapped from the `genai-service` container).
* **Weaviate Vector DB:** Accessible at http://localhost:8081 (this port is mapped from the `weaviate` container).
* **Database (PostgreSQL):** Running and accessible on port 5432 from your host machine.

### 5. Stopping All Services

To stop all running services:

```bash
docker-compose down
```

To stop services AND remove the database data volume (useful for a completely fresh start):

```bash
docker-compose down -v
```

### Troubleshooting Database Issues

* **PostgreSQL Version Incompatibility / Schema Issues:** If the database or server fails to start due to database file incompatibility or schema validation errors, it often means the Docker volume (`lecturebot_db_data`) contains data from a previous or different database state.
* **Solution:** Stop services (`docker-compose down`), remove the conflicting Docker volume (e.g., `docker volume rm _lecturebot_db_data` or `docker volume rm lecturebot_db_data` - use `docker volume ls` to find the exact name used by your setup), and then run `docker-compose up --build -d` again. This allows PostgreSQL to initialize a fresh database and run the init-users.sql script.

## ⚙️ Backend Server Details (Spring Boot)

The backend is a Spring Boot application. For detailed setup and general API testing instructions (like auth endpoints), refer to `services/server/README.md`.

### Testing Server + GenAI Integration Endpoints

The server includes endpoints under `/api/v1/genai/` to specifically test the communication with the `genai-service`. These endpoints are generated from the `openapi.yaml` specification.

#### Temporarily Disabling Security for Test Endpoints (Development Only)

By default, Spring Security protects most endpoints. For easier testing of the `/api/test/genai/**` endpoints without handling authentication tokens or basic auth, you can temporarily modify the `SecurityConfig.java` in the `server` application.

**File:** `server/src/main/java/com/lecturebot/server/config/SecurityConfig.java`

Add `.requestMatchers("/api/v1/genai/**").permitAll()` to your security configuration. Example:

```java
// Inside SecurityConfig.java, within the securityFilterChain method: http.csrf(csrf -> csrf.disable()) .authorizeHttpRequests(authz -> authz.requestMatchers("/api/v1/genai/").permitAll()
// <<< ADD THIS LINE .requestMatchers("/api/v1/auth/", "/api/v1/health").permitAll().anyRequest().authenticated())
// ... other configurations ...
```

**Important:** After adding this line, rebuild and restart the `server` service:

```bash
docker-compose up --build server

#or if all services are down:
docker-compose up --build
```

**Remember to remove or properly secure this path before any production deployment.**

#### Testing with `curl`

Ensure all services are running via `docker-compose up --build`.

**Test Indexing Document via Server:** This sends a request to the server, which then calls the `genai-service`.

```bash
curl -X POST "http://localhost:8080/api/test/genai/index" -H "Content-Type: application/json" -d '{ "documentId": "server-readme-test-doc-001", "courseSpaceId": "cs-readme-test-101", "textContent": "This is a test document sent via the server to the GenAI service for README instructions. It talks about Spring Boot and RestTemplate." }'
```

*Expected Output:* JSON response from the `genai-service` relayed by the server, or an error message if the call failed. Check server and genai-service logs.

**Test Submitting Query via Server:**

```bash
curl -X POST "http://localhost:8080/api/test/genai/query" -H "Content-Type: application/json" -d '{ "queryText": "What does this document about README instructions talk about?", "courseSpaceId": "cs-readme-test-101" }'
```

*Expected Output:* JSON response (answer and citations) from the `genai-service` relayed by the server. Check server and genai-service logs.

The backend is a Spring Boot application. For detailed setup and API testing instructions, refer to `server/README.md`.

## Frontend Client Details (React)

The frontend is a React application built with Vite and TypeScript. For detailed setup and UI testing, see `client/README.md`.

## GenAI Service Details (Python, LangChain)

The GenAI service is a Python application using FastAPI and LangChain.

### Running with Local Ollama (Manual Setup)

If you prefer to use a local LLM with Ollama instead of OpenAI:

**Install and Run Ollama:** Follow the official Ollama installation instructions for your operating system from ollama.com.

**Pull a Model:** Once Ollama is running, pull the desired model. For example, for `llama3:8b-instruct-q4_K_M`:

```bash
ollama pull llama3:8b-instruct-q4_K_M
```

Ensure the Ollama server is running and accessible (typically at `http://localhost:11434`).

**Configure Environment Variables:** Update your `.env` file (in the project root) to configure the `genai-service` for Ollama:

```env
... other variables GenAI Service Configuration (for Local Ollama) plaintext LLM_PROVIDER=ollama OLLAMA_MODEL_NAME=llama3:8b-instruct-q4_K_M # Or the model you pulled OLLAMA_BASE_URL=http://host.docker.internal:11434 # For Docker Desktop (Mac/Windows) For Linux, if host.docker.internal doesn't work, use your host's IP on the Docker bridge (e.g., http://172.17.0.1:11434) OPENAI_API_KEY= # Not needed for Ollama plaintext
```

* `host.docker.internal` allows containers to connect to services running on the host machine in Docker Desktop (Mac/Windows).
* For Linux, you might need to find your host's IP address on the `docker0` bridge network (often `172.17.0.1`) or configure Docker networking appropriately. **Restart Docker Compose:** If Docker Compose is already running, stop it (`docker-compose down`) and then restart it:

```bash
docker-compose up --build
```

The `genai-service` will now attempt to connect to your locally running Ollama instance.

## Deployment & Observability

* **Deployment:** The project is intended to be deployed to Kubernetes using GitHub Actions and Helm or raw manifests. (Further details would be in specific deployment guides).
* **Observability:** The system is planned to be observable with Prometheus and Grafana, monitoring key system metrics (server, GenAI). (Configuration files and dashboards are part of deliverables).

## Testing

* **Unit Tests:** Must cover critical server and GenAI logic.
* **Client Tests:** Should cover core workflows and interactions.
* **CI Testing:** All tests must run automatically in the CI pipeline. (Refer to individual `client/README.md` and `server/README.md` for specific test execution commands).

## API Documentation (Server)

The REST API should be clearly defined. API documentation is planned via Swagger/OpenAPI (typically accessible at `/swagger-ui.html` or `/v3/api-docs` on the running server once `springdoc-openapi-starter-webmvc-ui` is added and configured).