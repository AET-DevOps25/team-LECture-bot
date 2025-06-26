# GenAI Service for LECture-bot

This service is responsible for all AI-driven tasks within the LECture-bot application, including document indexing (chunking, embedding, storing in Vector DB) and Retrieval-Augmented Generation (RAG) for Q&A. It's a Python-based microservice using FastAPI and LangChain.

## Configuration (Environment Variables)

The service is configured using environment variables. These are typically set in the root `.env` file when running via the main `docker-compose.yml`.

* `LLM_PROVIDER`: Specifies the LLM provider to use.
  * Values: `"openai"` or `"ollama"`.
  * Default (in `docker-compose.yml`): `openai`
* `OPENAI_API_KEY`: Your API key for OpenAI. Required if `LLM_PROVIDER="openai"`.
* `OPENAI_MODEL_NAME`: The OpenAI model to use (e.g., `"gpt-4o-mini"`, `"gpt-3.5-turbo"`).
  * Default (in `docker-compose.yml`): `gpt-4o-mini`
* `OLLAMA_MODEL_NAME`: The Ollama model to use (e.g., `"llama3:8b-instruct-q4_K_M"`). Required if `LLM_PROVIDER="ollama"`.
  * Default (in `docker-compose.yml`): `llama3:8b-instruct-q4_K_M`
* `OLLAMA_BASE_URL`: The base URL for your Ollama instance. Required if `LLM_PROVIDER="ollama"`.
  * Example for Ollama running on host (Docker Desktop): `http://host.docker.internal:11434`
  * Default (in `docker-compose.yml`): `http://host.docker.internal:11434`
* `WEAVIATE_URL`: The URL for the Weaviate vector database instance.
  * When run via main `docker-compose.yml`: `http://weaviate:8080` (service name)
  * Default (in `docker-compose.yml`): `http://weaviate:8080`
* `TOKENIZERS_PARALLELISM`: Set to `"false"` to avoid potential issues with Hugging Face tokenizers in some environments.
  * Default (in `docker-compose.yml`): `false`
* `GENAI_PORT`: The port on which the GenAI service listens internally.
  * Default (in `genai/core/config.py`): `8001` (This is mapped by Docker Compose).
* `EMBEDDING_MODEL_NAME`: The sentence-transformer model used for generating embeddings.
  * Default (in `genai/core/config.py`): `"all-MiniLM-L6-v2"`
* `CHUNK_SIZE`: The target size for text chunks.
  * Default (in `genai/core/config.py`): `1000`
* `CHUNK_OVERLAP`: The overlap between consecutive text chunks.
  * Default (in `genai/core/config.py`): `200`

## Running the Service

### Via Main Docker Compose (Recommended)

The GenAI service is designed to be run as part of the main LECture-bot application stack using the `docker-compose.yml` file in the project root.

1. Ensure Docker and Docker Compose are installed.
2. Configure necessary environment variables in the root `.env` file (especially `OPENAI_API_KEY` if using OpenAI).
3. Navigate to the project root directory.
4. Run:

   ```bash
   docker-compose up --build
   ```

The service will be available internally to other Docker services (like the `server`) at `http://genai-service:8001`. It's also exposed on the host at `http://localhost:8001`.

### Standalone (for Development/Testing)

You can run the GenAI service locally for development without the full Docker Compose stack.

1. **Prerequisites:**
    * Python 3.11+
    * Poetry
    * A running Weaviate instance.
    * If using Ollama, a running Ollama instance with the desired model pulled.
    * If using OpenAI, a valid API key.

2. **Setup:**
    * Navigate to the `/genai` directory.
    * Install dependencies: `poetry install`
    * Create a `.env` file in the `/genai` directory (or set environment variables manually) with the necessary configurations (e.g., `WEAVIATE_URL`, `LLM_PROVIDER`, API keys/Ollama URLs). For Weaviate/Ollama running on your host, you'd typically use `http://localhost:8080` and `http://localhost:11434` respectively.

3. **Run with Uvicorn:**
    From the `/genai` directory:

    ```bash
    poetry run uvicorn src.genai.main:app --host 0.0.0.0 --port 8001 --reload
    ```

## API Endpoints

The API is versioned under `/api/v1`.

### 1. Index Document

* **Endpoint:** `POST /api/v1/index`
* **Summary:** Receives document text, chunks it, generates embeddings, and stores them in the vector DB.
* **Request Body:** `application/json`

    ```json
    {
        "document_id": "string (UUID or unique identifier)",
        "course_space_id": "string (identifier for the course or context)",
        "text_content": "string (the full text content of the document)"
    }
    ```

* **Example curl:**

    ```bash
    curl -X POST "http://localhost:8001/api/v1/index" \
      -H "Content-Type: application/json" \
      -d '{
        "document_id": "test-doc-001",
        "course_space_id": "cs-test-101",
        "text_content": "This is a test document for indexing via the GenAI service API."
    }'
    ```

### 2. Query Document

* **Endpoint:** `POST /api/v1/query`
* **Summary:** Receives a query, retrieves relevant context from the vector DB, and generates an answer using an LLM.
* **Request Body:** `application/json`

    ```json
    {
        "query_text": "string (the user's question)",
        "course_space_id": "string (identifier for the course or context to search within)"
    }
    ```

* **Example curl:**

    ```bash
    curl -X POST "http://localhost:8001/api/v1/query" \
      -H "Content-Type: application/json" \
      -d '{
        "query_text": "What is this document about?",
        "course_space_id": "cs-test-101"
    }'
    ```

### 3. Health Check

* **Endpoint:** `GET /health`
* **Summary:** Basic health check for the service.
* **Example curl:**

    ```bash
    curl http://localhost:8001/health
    ```

A healthy response will look like:

```json
{"status": "healthy", "module_name": "GenAI Module for LECture-bot", "version": "0.1.0"}
```

## Project Structure (within `/genai`)

* `src/genai/`: Main source code directory.
  * `api/`: Contains FastAPI routers and Pydantic schemas.
  * `core/`: Core application configuration (e.g., environment variables).
  * `pipelines/`: High-level logic for indexing and question-answering pipelines.
  * `services/`: Contains clients for external services (LLMs, embeddings, vector DB).
  * `utils/`: Helper functions and utilities.
  * `main.py`: The FastAPI application entry point.
* `Dockerfile`: For containerizing the service.
* `pyproject.toml`: Project metadata and dependencies (Poetry).
* `.env.example`: Example environment variables.
