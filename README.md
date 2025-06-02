# LECture-bot: Full-Stack Application with GenAI Integration

Welcome to LECture-bot! This project involves the design, development, deployment, and scaling of a lightweight but technically complete web application with a meaningful Generative AI (GenAI) integration.

## 🎯 Project Overview & Objectives

This project aims to apply modern DevOps principles including containerization, CI/CD automation, cloud-native deployment, observability, and AI integration. The system is built as a client-server application, integrates a GenAI component, and is designed for collaborative development with documented ownership and clear workflows.

### Problem Addressed

Students across various university courses often struggle to efficiently locate specific information within a multitude of course materials like lecture slides, research papers, and textbooks. This application aims to solve this by providing a centralized, intelligent query interface for any uploaded course resources. Its main functionality is to allow users to upload diverse course documents (e.g., PDFs, Markdown files, text files) and then ask natural language questions about the content. The intended users are students enrolled in any university or online course seeking a faster way to find information and understand complex topics, ultimately saving significant study time and improving comprehension of their study materials. GenAI will be meaningfully integrated using LangChain and a vector database to implement Retrieval-Augmented Generation (RAG), enabling the assistant to answer user queries based *only* on the provided course-specific documents, with support for both cloud-based (e.g., OpenAI API) and local Large Language Models (LLMs).

### Main Functionalities

- User can sign up, log in, and manage their profile (e.g., associate with specific courses).
- User can create "course spaces" or collections to organize materials.
- User can upload various document types (PDF, MD, TXT) into a selected course space.
- System efficiently processes and indexes uploaded documents, making them immediately available for querying.
- System provides clear feedback to the user during document upload, processing, and indexing stages (e.g., progress indicators, success/error messages).



### GenAI Integration

(Based on "Problem Statement Template.pdf" and "Project Detail.pdf" - You'll need to describe your specific GenAI integration)

- How will you integrate GenAI meaningfully?
- The GenAI module should be well-embedded and fulfill a real user-facing purpose.
- It is implemented using LangChain and connects to cloud/local LLMs, designed as a modular microservice.

### Key Scenarios

(Based on "Problem Statement Template.pdf" - You'll need to describe your specific scenarios)

- Describe some scenarios how your app will function.

## 🏗️ Architecture & Technical Stack

### System Components

The system is divided into the following technical components:

- **Client:** A frontend application built with React (or Vue/Angular as per project options).
- **Server:** A Spring Boot REST API.
- **GenAI Service:** A Python microservice utilizing LangChain.
- **Database:** PostgreSQL (or MongoDB as per project options, though PostgreSQL is used in current setup).

### Key Technologies

- **Frontend:** React, Vite, TypeScript.
- **Backend Server:** Java 21, Spring Boot, Spring Data JPA, Spring Security, Gradle.
- **GenAI Service:** Python, LangChain.
- **Database:** PostgreSQL.
- **Containerization:** Docker, Docker Compose.

### Architecture Diagrams

Detailed architecture, including UML diagrams (Analysis Object Model, Use Cases, Top-Level Component Diagram/Subsystem Decomposition), should be available in the project's /docs directory. These diagrams visualize the system structure and interactions.

## ✨ Key Features (Planned/Implemented)

- **User Authentication:** Secure sign-up and login functionality.
- **Core Application Feature(s):** [Describe your application's central features here]. The system must include at least one meaningful core feature beyond CRUD.
- **GenAI Powered Feature:** [Describe the specific user-facing feature powered by the GenAI service].

## 📋 Prerequisites

### For Running the Entire Application with Docker Compose (Recommended)

- Docker installed and running.
- Docker Compose installed.

### For Running Frontend Locally (Standalone)

- Node.js and npm (or yarn). \

### For Running Backend Locally (Standalone)

- Java 21 JDK.

- Gradle (or use the provided Gradle Wrapper ./gradlew in the server directory). \

## 📁 Project Structure

A brief overview of the main directories:

- `/client`: Contains the frontend React application source code and its Dockerfile.
- `/server`: Contains the backend Spring Boot application source code, its Dockerfile, and database initialization scripts (db-init).
- `/docker-compose.yml`: (Located in this root directory) Orchestrates the client, server, and database services for a complete development environment.
- `/docs`: Contains additional documentation like architecture descriptions, analysis models, and problem statements.

## 🚀 Getting Started: Running the Full Application (with Root Docker Compose)

The primary way to run the entire LECture-bot application (frontend, backend, and database) is by using the docker-compose.yml file located in this project root directory.

### 1. Clone the Repository (If you haven't already)

```bash
git clone &lt;your-repository-url>
cd LECture-bot # Or your project's root directory name
```

### 2. Environment Configuration (Optional but Recommended)

You can create a .env file in this root directory to manage environment variables for the services. Docker Compose will automatically pick it up.

Example \.env file:

```env

Database Credentials & Settings
DB_USER=lecturebot_user
DB_PASSWORD=myverysecurepassword # Change this!
DB_NAME=lecturebot_db

Spring Boot / JPA Settings for Server
SPRING_JPA_HIBERNATE_DDL_AUTO=validate # (e.g., validate, update, none). 'validate' is good if init-users.sql manages schema.
SERVER_PORT=8080

Client URLs for CORS configuration on the server
LECTUREBOT_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

If this file is not present, the defaults specified in docker-compose.yml will be used.

### 3. Build and Start All Services

Navigate to the project root directory (where the main docker-compose.yml is located) and run:

```bash
docker-compose up --build
```

- --build: Ensures that Docker images for the client and server are built (or rebuilt if their Dockerfiles or source code have changed).
- To run in detached mode (in the background), add the -d flag: docker-compose up --build -d

### 4. Accessing the Services

Once all containers are up and running:

- **Frontend Application:** Open your browser and go to <http://localhost:3000> (this port is mapped from the client container in docker-compose.yml).
- **Backend API Server:** Accessible at <http://localhost:8080> (this port is mapped from the server container). API endpoints are documented further down and in server/README.md.
- **Database (PostgreSQL):** Running and accessible on port 5432 from your host machine (as mapped in docker-compose.yml).

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

- **PostgreSQL Version Incompatibility / Schema Issues:** If the database or server fails to start due to database file incompatibility or schema validation errors, it often means the Docker volume (lecturebot_db_data) contains data from a previous or different database state.
- **Solution:** Stop services (docker-compose down), remove the conflicting Docker volume (e.g., docker volume rm <projectname>_lecturebot_db_data or docker volume rm lecturebot_db_data - use docker volume ls to find the exact name used by your setup), and then run docker-compose up --build -d again. This allows PostgreSQL to initialize a fresh database and run the init-users.sql script.

## ⚙️ Backend Server Details (Spring Boot)

The backend is a Spring Boot application. For detailed setup and API testing instructions, refer to server/README.md.

## 🖥️ Frontend Client Details (React)

The frontend is a React application built with Vite and TypeScript. For detailed setup and UI testing, see client/README.md.

## ☁️ Deployment & Observability

- **Deployment:** The project is intended to be deployed to Kubernetes using GitHub Actions and Helm or raw manifests. (Further details would be in specific deployment guides).
- **Observability:** The system is planned to be observable with Prometheus and Grafana, monitoring key system metrics (server, GenAI). (Configuration files and dashboards are part of deliverables).

## 🧪 Testing

- **Unit Tests:** Must cover critical server and GenAI logic.
- **Client Tests:** Should cover core workflows and interactions.
- **CI Testing:** All tests must run automatically in the CI pipeline.
(Refer to individual client/README.md and server/README.md for specific test execution commands).

## 📚 API Documentation (Server)

The REST API should be clearly defined. API documentation is planned via Swagger/OpenAPI (typically accessible at /swagger-ui.html or /v3/api-docs on the running server once springdoc-openapi-starter-webmvc-ui is added and configured).

## 👥 Team & Responsibilities

(This section can be filled out by the team based on "Subsystem Ownership" from "Project Detail.pdf" and "student responsibilities" mentioned in "Project Grading.pdf")

- Student 1 (GitHub: @username1): Role/Subsystem (e.g., Backend Lead)
- Student 2 (GitHub: @username2): Role/Subsystem (e.g., Frontend Lead)
- Student 3 (GitHub: @username3): Role/Subsystem (e.g., GenAI Lead & DevOps)
