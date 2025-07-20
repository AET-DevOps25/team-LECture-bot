# GitHub Workflows Overview

>This directory contains the main CI/CD workflows for this project. Below is a summary of each workflow, when it is triggered, and its responsibilities.


| Workflow             | Main Purpose                                 | When is it Triggered?                      |
|----------------------|----------------------------------------------|--------------------------------------------|
| `ci.yml`             | Code quality, tests, builds, E2E checks      | On push or pull request to `main`/`develop`|
| `build_docker.yml`   | Builds and pushes Docker images              | On every push                              |
| `deploy_docker.yml`  | Deploys images to AWS EC2 and Kubernetes     | Manually (workflow_dispatch)               |

---

## 1. `ci.yml`
**Purpose:**
> Main continuous integration pipeline. Ensures code quality, runs tests, builds, and performs end-to-end tests.

**When is it triggered?**
- On every push or pull request to the `main` or `develop` branches.

**Key Jobs:**
- **OpenAPI Lint & Code Generation:** Lints OpenAPI specs, runs code generation scripts for API models.
- **Backend Java Unit Tests:** Runs unit tests for each backend microservice using Gradle.
- **Client Unit Tests:** Runs React client unit tests with npm.
- **GenAI Python Unit Tests:** Runs Python unit tests for the GenAI service using pytest.
- **Backend Build:** Builds each backend microservice (without running tests) to ensure compilation.
- **E2E Tests:** Runs most Playwright end-to-end tests against the full stack, using Docker Compose to start services.

---

## 2. `build_docker.yml`
**Purpose:**
> Builds and pushes Docker images for all services to the GitHub Container Registry (GHCR).

**When is it triggered?**
- On every push to any branch.

**Key Jobs:**
- **test:**
  - Runs Java unit tests for all backend microservices before building images.
- **build:**
  - Builds Docker images for all services (client, backend microservices, genai).
  - Logs in to the container registry.
  - Sets up build tools (QEMU, Buildx).
  - Extracts image metadata and tags.
  - Builds and pushes multi-architecture images (`amd64`, `arm64`) to GHCR.

---

## 3. `deploy_docker.yml`
**Purpose:**
> Deploys Docker images to AWS EC2 (using Docker Compose) and to a Kubernetes cluster (using Helm).

**When is it triggered?**
- Manually, via the GitHub Actions UI (workflow_dispatch).

**Key Jobs:**
- **docker-to-aws:**
  - Copies Docker Compose files and environment variables to an AWS EC2 VM.
  - Logs in to the Docker registry and starts the stack with Docker Compose.
- **helm-to-aet:**
  - Waits for the Docker deployment to finish.
  - Sets up Kubernetes config and Helm.
  - Deploys/updates the application in the AET Kubernetes cluster using Helm charts.
