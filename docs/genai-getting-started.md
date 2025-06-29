## Getting Started with the GenAI Service

### 1. Introduction

This guide will help you get the **GenAI Service** running so you can test it and start building new features like Flashcard generation.

The GenAI Service is a Python-based microservice responsible for all AI-driven tasks, including document indexing and Retrieval-Augmented Generation (RAG) for Q&A. For more details, refer to the service's dedicated README file.

### 2. Prerequisites

Before you begin, please ensure you have the following installed:

* **Docker and Docker Compose:** For running the entire application stack.
* **An IDE:** Such as VS Code.
* **OpenAI API Key:** You will need this to test the Q&A functionality with the default OpenAI provider.
* **(Optional) Ollama:** If you wish to test with a local LLM.

### 3. Running the Full System (Recommended Workflow)

This is the easiest way to get everything running together. The main project `README.md` has the most up-to-date information on this process.

1. **Clone the Repository:** If you haven't already, clone the `LECture-bot` repository to your local machine.
2. **Configure Environment:** In the project's root directory, create a `.env` file. You can copy `env.example` if it exists. The most important variable to add is your OpenAI API key:

    ```
    OPENAI_API_KEY="sk-..."
    ```

3. **Launch:** Navigate to the project's root directory in your terminal and run:

    ```bash
    docker-compose up --build
    ```

    This command builds the images for all services (including `genai-service`) and starts them. The services will be connected on a shared Docker network.

### 4. How to Test the GenAI Service

Once the system is running, you can test the core features of the GenAI service directly using `curl` or a tool like Postman. This is useful for verifying its functionality independently from the frontend or Java backend.

The full API documentation can be found in the `genai/README.md`.

**A. Index a Document:**
Send some text content to the service to be chunked, embedded, and stored in the Weaviate vector database.

```bash
curl -X POST "http://localhost:8001/api/v1/index" \
-H "Content-Type: application/json" \
-d '{
  "document_id": "test-doc-01",
  "course_space_id": "cs101",
  "text_content": "LangChain is a framework for developing applications powered by language models. It provides tools for chaining together different AI components."
}'
```

**B. Ask a Question:**
Use the query endpoint to ask a question related to the content you just indexed.

```bash
curl -X POST "http://localhost:8001/api/v1/query" \
-H "Content-Type: application/json" \
-d '{
  "query_text": "What is LangChain?",
  "course_space_id": "cs101"
}'
```

You should receive a JSON response containing an answer and a citation pointing to `test-doc-01`.

### 5. Developing New Features (e.g., Flashcards)

If your task is to add a new feature *to the GenAI service itself* (like flashcard generation), it's often easier to run and debug it as a standalone service.

The `genai/README.md` file contains detailed instructions for this standalone setup. Here’s a summary of the workflow:

1. **Create a Feature Branch:** Start by creating a new branch for your feature (e.g., `feature/genai-flashcards`).
2. **Install Dependencies:** Navigate to the `/genai` directory and run `poetry install`.
3. **Run Standalone:** In the `/genai` directory, create a local `.env` file and run `poetry run uvicorn genai.main:app --reload`.
4. **Add Your Feature:**
    * **Schemas:** Define your request and response models in `genai/src/genai/api/schemas.py`.
    * **Pipeline:** Create a new pipeline (e.g., `flashcard_pipeline.py`) for your business logic. Use `qa_pipeline.py` as a reference.
    * **Router:** Create a new API router (e.g., `flashcard.py`) to define your new endpoint (e.g., `POST /api/v1/flashcards/`).
    * **Main App:** Register your new router in `genai/src/genai/main.py`.

### 6. Key Files for Reference

* `README.md` (Project Root): For overall project setup.
* `genai/README.md`: **Your primary reference for the GenAI service.** It has detailed information on configuration, API endpoints, and project structure.
* `docker-compose.yml` (Project Root): To understand how all the microservices connect and are configured.
