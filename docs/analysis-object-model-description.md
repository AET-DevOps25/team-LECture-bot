# Overview of the Analysis Object Model (first simplified draft): Intelligent Course Material Assistant

## Purpose

This file serves as a concise technical overview of the system's architecture, helping developers and stakeholders understand how key components interact and what roles they play. It's useful for onboarding, documentation, and maintaining alignment across teams.

**Note:** 

This is a simplified draft and may not cover all edge cases or detailed interactions. This file is subject to change.

---

## System Overview – Analysis Object Model

This system supports document upload, processing, and intelligent content retrieval (e.g. Q&A, flashcards) for users within course spaces.

## Core Components & Interactions

### 🧑 User Management
- **User, UserService, UserRepository**
  - Handle registration, login, and user data persistence.
  - A `User` owns multiple `CourseSpaces`.

### 📚 Course Management
- **CourseSpace, CourseRepository**
  - Organize documents by topic.
  - Allow listing and flashcard generation per course.
- **CourseSpace ↔ Document**
  - Each `CourseSpace` acts as a container for related `Document`s. This association allows users to organize learning materials thematically and retrieve flashcards or Q&A content within a focused context.
### 📄 Document Processing
- **Document, DocumentChunk, DocumentRepository**
  - Store uploaded files and their parsed content.
- **DocumentProcessingService**
  - Uploads, extracts text, and sends it to GenAI for indexing.
 - **Document ↔ DocumentProcessingService**
    -  When a user uploads a file, the `DocumentProcessingService` saves it as a `Document`, extracts text, and potentially breaks it into `DocumentChunks`. This modular processing supports downstream indexing and querying.
### 🤖 GenAI Integration
- **GenAiClient**
  - Sends documents and queries to a generative AI module.
  - Used for flashcard and Q&A generation.

### 🔍 Query Handling
- **QueryOrchestrationService**
  - Coordinates queries across services (User, Document).
  - Returns results from GenAI based on user context.
- **QueryOrchestrationService ↔ GenAiClient**
  - The orchestration service acts as a middleware that interprets user queries and delegates them to the `GenAiClient`, which interfaces with an external AI model to produce flashcards or answers.

### 🌐 External Interfaces
- **UserServiceClient, DocumentServiceClient**
  - Internal API clients for accessing user/course and document data.
- **API Gateway**
  - Entry point for all client-server communication.

### 💾 Repositories and Services
- **Repositories (e.g., UserRepository, DocumentRepository)**
    - These are abstraction layers for data persistence, separating storage concerns from business logic.
- **Service Clients (UserServiceClient, DocumentServiceClient)**
    - Allow internal communication between services, helping maintain modularity and enabling microservice patterns if needed.



## Workflow Summary
1. User uploads a document to a course.
2. Text is extracted and sent to GenAI.
3. User queries (e.g. Q&A or flashcards) are processed via orchestration.
4. Results are returned using indexed document content.

