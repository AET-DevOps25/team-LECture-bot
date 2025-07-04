# Team LECture Bot

A full-stack GenAI application with Python FastAPI (genai-service), React (client), Java Spring Boot (server), Weaviate, and PostgreSQL. All services are orchestrated with Docker Compose.

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Node.js (for local frontend dev)
- Python 3.10+ (for local backend dev)

### Running All Services

```
docker compose up --build
```

- The **client** (React app) will be available at: http://localhost:5173
- The **genai-service** (FastAPI) will be available at: http://localhost:8000
- The **server** (Spring Boot) will be available at: http://localhost:8080

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

## Documentation
- Backend API: [`services/genai/README.md`](services/genai/README.md)
- Frontend: [`services/client/README.md`](services/client/README.md)
- Architecture: [`docs/architecture-description.md`](docs/architecture-description.md)

---

## License
MIT

## 🛠️ Monorepo Environments & Testing

This project uses a **polyglot monorepo** structure. Each service manages its own dependencies and environment:

- **Python services** (e.g., `genai`): Use Poetry. Each service has its own `pyproject.toml` and virtual environment. Run all Poetry commands from the service directory.
- **Node/React services**: Use their own `package.json` and `node_modules`.
- **Java services**: Use their own `build.gradle` or `pom.xml`.

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