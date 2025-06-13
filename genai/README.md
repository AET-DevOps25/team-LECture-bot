# GenAI Module for LECture-bot

docker run -d -p 8080:8080 -p 50051:50051 -e AUTHENTICATION_ANONYMOUS_ACCESS_ENABLED='true' -e PERSISTENCE_DATA_PATH='/var/lib/weaviate' -e DEFAULT_VECTORIZER_MODULE='none' -e ENABLE_MODULES='' --name weaviate-lecurebot semitechnologies/weaviate:latest

## GenAI Service for LECture-bot

This service is responsible for all AI-driven tasks, including document indexing (chunking, embedding, storing in Vector DB) and Retrieval-Augmented Generation (RAG) for Q&amp;A and other text generation tasks.

## Tech Stack

- Python 3.11+
- FastAPI
- LangChain
- Weaviate (Vector Database Client)
- Sentence-Transformers (for embeddings)
- Poetry (for dependency management)

## Setup and Installation

**Install Poetry:** If you don't have it, install it following the [official instructions](https://python-poetry.org/docs/).
**Install Dependencies:** Navigate to this directory (`/genai`) and run:
    ```bash
    poetry install
    ```
**Environment Variables:** Create a `.env` file in this directory by copying the `.env.example` (if it exists) or creating a new one. At a minimum, it should contain:
    ```env
    # For connecting to OpenAI for RAG OPENAI_API_KEY="your_openai_api_key_here"
    # URL for the Weaviate instance from docker-compose
    WEAVIATE_URL="http://localhost:8080"
    ```

## Running the Service

You can run the service directly with uvicorn for local development:

```bash
poetry run uvicorn genai.main:app --host 0.0.0.0 --port 8001 --reload
```

The service will be available at `http://localhost:8001`.

Run weaviate

    ```bash
    docker run -d -p 8080:8080 -p 50051:50051 -e AUTHENTICATION_ANONYMOUS_ACCESS_ENABLED='true' -e PERSISTENCE_DATA_PATH='/var/lib/weaviate' -e DEFAULT_VECTORIZER_MODULE='none' -e ENABLE_MODULES='' --name weaviate-lecturebot semitechnologies/weaviate:latest
    ```

## Testing

### Testing the Indexing Pipeline

Once the GenAI service and the Weaviate database are running (e.g., via `docker-compose`), you can test the document indexing pipeline by sending a `POST` request to the `/api/v1/index/index` endpoint.

Use the following `curl` command in your terminal:

```bash
curl -X POST "http://localhost:8001/api/v1/index/index"

-H "Content-Type: application/json"

-d '{
"document_id": "doc-001-test",
"course_space_id": "cs-101-intro-to-ai",
"text_content": "Vector databases are essential for AI. They store data as high-dimensional vectors, which are mathematical representations of features or attributes. LangChain is a popular framework for developing applications powered by language models. It simplifies the process of creating complex AI workflows, including Retrieval-Augmented Generation (RAG)."
}'
```

**Expected Successful Response:**

If successful, you will receive a `200 OK` or `207 Multi-Status` response with a JSON body detailing the outcome, like this:

```json
{
"status": "partial_success",
"message": "Document processed. 2 chunks were stored successfully. 0 chunks failed.",
"document_id": "doc-001-test",
"total_chunks_generated": 2,
"chunks_stored_successfully": 2,
"failed_chunk_indexes": []
}
```
