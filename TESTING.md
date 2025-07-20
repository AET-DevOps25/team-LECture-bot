# Testing in This Project

This project uses multiple testing frameworks and strategies to ensure code quality across all services. Below is an overview of the testing setup, frameworks used, and instructions for running tests manually for each service.

---

## What are Unit Tests?

Unit tests are automated tests that verify the smallest testable parts of an application (called "units") in isolation from the rest of the system. They are designed to check that individual functions, methods, or classes work as expected, without relying on external systems or integration with other components. Unit tests help catch bugs early, make refactoring safer, and improve code quality.

---

## Overview

- **Backend Microservices (Java):** JUnit (via Gradle)
- **Client (React):** Jest
- **GenAI Service (Python):** Pytest
- **End-to-End (E2E):** Playwright

---

## 1. Backend Microservices (Java)

**Framework:** JUnit (run via Gradle)

**Services:**
- api-gateway
- user-course-microservice
- genai-backend-microservice
- document-microservice
- discovery-service

**How to Run Unit Tests Manually:**

```
cd services/backend/<service-name>
chmod +x ./gradlew
./gradlew test
```
Replace `<service-name>` with the desired microservice (e.g., `api-gateway`).

---

## 2. Client (React)

**Framework:** Jest

**How to Run Unit Tests Manually:**

```
cd services/client
npm install
npm run test
```

---

## 3. GenAI Service (Python)

**Framework:** Pytest

**How to Run Unit Tests Manually:**

```
cd services/genai
pip install -r requirements.txt
pytest tests/unit
```

---

## 4. End-to-End (E2E) Tests

**Framework:** Playwright

**How to Run Tests Manually:**

```
npm install
npx playwright install --with-deps
docker compose -f docker-compose.yml up --build -d
npx wait-on http://localhost:3000
npm run test:e2e:ordered:ci
```

- Make sure all required services are running (via Docker Compose) before running E2E tests.
- To stop services after testing:

```
docker compose down
```

---

## Notes
- All test commands should be run from the project root unless otherwise specified.
- Ensure you have the necessary dependencies installed (Node.js, Python, Docker, etc.).
- For backend services, Java 21 is required.
- For GenAI, Python 3.11 is recommended.

---

For a practical example, see the ci.yml file in `.github/workflows/`.
