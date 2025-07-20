# Team LECture Bot

[![Build Status](https://img.shields.io/github/actions/workflow/status/your-repo/ci.yml?branch=main)](https://github.com/your-repo/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A full-stack, AI-powered Q&A platform for educational content. This project uses a microservices architecture with a React frontend, a Java Spring Boot backend, and a Python FastAPI service for Generative AI capabilities.

## Table of Contents

- [Architecture Overview](docs/architecture-description.md)
- [API Documentation](api/README.md)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Monitoring](#-monitoring)
- [License](#-license)

---

## 🏛️ Architecture Overview

The application is composed of several containerized services orchestrated by Docker Compose:

- **`client`**: A React single-page application that provides the user interface.
- **`server`**: A Java Spring Boot application that handles business logic, user authentication, and data management.
- **`genai-service`**: A Python FastAPI microservice that performs Retrieval-Augmented Generation (RAG) for the Q&A feature.
- **`postgres-db`**: A PostgreSQL database for storing user and course data.
- **`weaviate`**: A Weaviate vector database for storing document embeddings used by the `genai-service`.

**Workflow:** The `Client` communicates with the `Server` via a REST API. For AI-related tasks (like asking a question), the `Server` calls the `GenAI Service`, which uses the `Weaviate` vector store to find relevant context and generate an answer.

---

## 🚀 Getting Started

These instructions will get the entire application running on your local machine for development and testing purposes.

### Prerequisites

- Docker
- Docker Compose

### One-Command Local Setup

Clone the repository and run the following command from the project root:

```bash
docker compose up --build
```

- The **client** (React app) will be available at: <http://localhost:5173>
- The **genai-service** (FastAPI) will be available at: <http://localhost:8000>
- The **server** (Spring Boot) will be available at: <http://localhost:8080>

### Stopping Services

```
docker compose down
```

---

## GenAI Service API Usage

### Health Check

```
curl http://localhost:8000/health
```

### Index Documents

```
curl -X POST http://localhost:8000/api/v1/index \
  -H 'Content-Type: application/json' \
  -d '{"documents": [{"id": "doc1", "text": "This is a test document."}]}'
```

### Query Documents

```
curl -X POST http://localhost:8000/api/v1/query \
  -H 'Content-Type: application/json' \
  -d '{"query": "test document"}'
```

See [`services/genai/README.md`](services/genai/README.md) for more details and payload examples.

---

## Frontend (React) Usage & Testing

- The Profile page allows users to view and update their name/email and change their password.
- To test:
  1. Log in and navigate to `/profile`.
  2. Update your name/email and submit. If you change your email, you will be logged out and must log in again.
  3. Change your password using the form. You must enter your current password and confirm the new password.

If you encounter build errors, run:

```
cd services/client
npm install
npm run dev
```

---

## User Profile Page

The Profile page allows users to update their name, email, and password. It uses the following API endpoints:

- `GET /api/v1/user/profile` – Fetch user profile data
- `PUT /api/v1/user/profile` – Update user profile (name/email)
- `POST /api/v1/user/change-password` – Change user password

### Testing the Profile Page

1. Log in to the frontend.
2. Navigate to the Profile page.
3. Update your name or email and click **Update Profile**.
   - If you change your email, you may be logged out and required to log in again.
4. Change your password using the form. You must enter your current password and confirm the new password.

### Example API Usage (from browser or curl)

#### Get Profile

```bash
curl -X GET http://localhost:8000/api/v1/user/profile -H "Authorization: Bearer <token>"
```

#### Update Profile

```bash
curl -X PUT http://localhost:8000/api/v1/user/profile \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name": "New Name", "email": "new@email.com"}'
```

#### Change Password

```bash
curl -X POST http://localhost:8000/api/v1/user/change-password \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"old_password": "currentpass", "new_password": "newpass"}'
```

---

## Troubleshooting

- Ensure all dependencies are installed (see above).
- If Poetry lock errors occur in genai-service, run `poetry lock` inside `services/genai`.
- If you see import errors in genai-service, ensure `PYTHONPATH` is set (handled in Dockerfile).
- For React/TypeScript errors, ensure all `@types/*` packages are installed.

---

## 📚 Documentation & Project Structure

- **Setup Instructions:** See below for quick local setup. Each service has its own README for service-specific instructions.
- **Architecture Overview:** [`docs/architecture-description.md`](docs/architecture-description.md) includes diagrams and models.
- **Usage Guide:** This README and service READMEs provide step-by-step usage and testing instructions.
- **Student Responsibilities:** Team roles and contributions are documented in [`docs/team-responsibilities.md`](docs/team-responsibilities.md) (or update with your actual file).
- **API Documentation:**
  - Backend: OpenAPI/Swagger docs at [`services/genai/README.md`](services/genai/README.md), [`services/backend/README.md`](services/backend/README.md), and API Gateway setup/routing details at [`services/backend/api-gateway/README.md`](services/backend/api-gateway/README.md)
    - Discovery Service registration and configuration: [`services/backend/discovery-service/README.md`](services/backend/discovery-service/README.md)
    - Document Microservice API and management: [`services/backend/document-microservice/README.md`](services/backend/document-microservice/README.md)
    - User-Course Microservice API and management: [`services/backend/user-course-microservice/README.md`](services/backend/user-course-microservice/README.md)
    - Discovery Service registration and configuration: [`services/backend/discovery-service/README.md`](services/backend/discovery-service/README.md)
  - Frontend: [`services/client/README.md`](services/client/README.md)
**Monitoring Documentation:**
  - Prometheus and Grafana setup instructions are in [`docs/monitoring.md`](docs/monitoring.md) (or update with your actual file).
  - Alerting rules and dashboard exports included.
**CI/CD and GenAI Documentation:**
  - CI/CD pipeline details in [`docs/cicd-pipeline.md`](docs/cicd-pipeline.md) (or update with your actual file).
  - GitHub Actions workflow documentation in [`.github/workflows/README.md`](.github/workflows/README.md)
  - GenAI usage and integration in [`services/genai/README.md`](services/genai/README.md).
**Reproducible Setup:**
  - Deployment and local setup instructions are clear, reproducible, and platform-specific. You can set up the entire project locally with three or fewer commands, using sane defaults for all services (see Getting Started above).
**Weekly Reporting:**
  - Weekly progress and engineering process are documented in [`docs/weekly-reports.md`](docs/weekly-reports.md) (or update with your actual file).

---

## 📝 API Documentation

- All major subsystems expose documented APIs using OpenAPI/Swagger. See service-specific READMEs for endpoint details and example payloads.

## 📈 Monitoring & Observability

- Prometheus is integrated for metrics collection.
- Grafana dashboards visualize system behavior and include at least one alert rule.
- See [`docs/monitoring.md`](docs/monitoring.md) for setup and dashboard exports.

## 🔄 CI/CD Pipeline & Automated Testing

- GitHub Actions CI pipeline builds, tests, and generates Docker images for all services.
- CD pipeline deploys to Kubernetes (Rancher/AWS) on main branch merges.
- Automated tests run in CI and cover key functionality for server, client, and GenAI logic.
- See [`docs/cicd-pipeline.md`](docs/cicd-pipeline.md) for details.

## 👥 Team Responsibilities & Weekly Reporting

- Team roles, contributions, and weekly progress are documented in [`docs/team-responsibilities.md`](docs/team-responsibilities.md) and [`docs/weekly-reports.md`](docs/weekly-reports.md).

When a document is deleted via the backend API, the following workflow is triggered:

1. The backend first calls the GenAI service's de-index endpoint to remove all vector data for the document from the vector database (Weaviate).
2. Only if de-indexing succeeds, the document is deleted from the main database.
3. If de-indexing fails, the document is not deleted from the database, ensuring consistency between the main data store and the vector store.

**Endpoints involved:**

- Backend: `DELETE /api/v1/documents/{courseSpaceId}/{id}` (see [`services/backend/document-microservice/README.md`](services/backend/document-microservice/README.md))
- GenAI: `DELETE /api/v1/index/{course_space_id}/{document_id}` (see [`services/genai/README.md`](services/genai/README.md))

For more details and payload examples, see the linked service READMEs.

---

## License

MIT

## 🛠️ Monorepo Environments & Testing

This project uses a **polyglot monorepo** structure. Each service manages its own dependencies and environment:

## Monorepo Environments and Microservice Testing

This repository uses a polyglot monorepo structure. Each microservice is fully isolated and manages its own dependencies and environment. The following conventions apply:

**Python Microservices** (e.g., `genai`)

- Dependency management: Poetry
- Each service contains its own `pyproject.toml` and virtual environment
- All Poetry commands should be executed from the respective service directory

**Node.js/React Microservices**

- Dependency management: npm
- Each service contains its own `package.json` and `node_modules` directory
- All npm commands should be executed from the respective service directory

**Java Microservices**

- Dependency management: Gradle or Maven
- Each service contains its own `build.gradle` or `pom.xml`
- All build and test commands should be executed from the respective service directory

### Running Python Tests (Example: `genai` Service)

1. Change to the service directory:

   ```bash
   cd services/genai
   ```

2. Set the Python path and run tests:

   ```bash
   PYTHONPATH=src poetry run pytest
   ```

   This ensures the `genai` package is found by Python.

3. (Optional) Run a specific test file:

   ```bash
   PYTHONPATH=src poetry run pytest tests/unit/test_qa_pipeline.py
   ```

**Note:** Each microservice is isolated. You should maintain separate Poetry, npm, or Gradle environments for each service.

### Running Python Tests (e.g., for `genai`)

1. **Change to the service directory:**

   ```bash
   cd services/genai
   ```

2. **Set the Python path and run tests:**

   ```bash
   PYTHONPATH=src poetry run pytest
   ```

   This ensures the `genai` package is found by Python.

3. **(Optional) Run a specific test file:**

   ```bash
   PYTHONPATH=src poetry run pytest tests/unit/test_qa_pipeline.py
   ```

> **Note:** Each service is isolated. You can (and should) have multiple Poetry, npm, or Gradle environments—one per service.
