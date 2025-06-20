from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Optional, List

class Settings(BaseSettings):
    APP_NAME: str = "GenAI Module for LECture-bot"
    API_V1_STR: str = "/api/v1"

    # Weaviate Configuration
    WEAVIATE_URL: str = "http://localhost:8080" # Default, adjust if your Weaviate runs elsewhere
    WEAVIATE_API_KEY: Optional[str] = None # If your Weaviate instance needs an API key
    WEAVIATE_CLASS_NAME: str = "LectureBotChunk"

    # LLM Configuration (for Sub-Issue 7)
    LLM_PROVIDER: str = "ollama" # Default provider

    # -- OpenAI Settings --
    OPENAI_API_KEY: Optional[str] = None # Make it optional
    OPENAI_MODEL_NAME: str = "gpt-4o-mini"
    # -- Ollama Settings --
    OLLAMA_BASE_URL: str = "http://localhost:11434" # Default Ollama API URL
    # OLLAMA_MODEL_NAME: str = "llama3" # Default model to use with Ollama
    OLLAMA_MODEL_NAME: str = "llama3:8b-instruct-q4_K_M" # Example of a quantized model

    
    # Embedding Model
    EMBEDDING_MODEL_NAME: str = "sentence-transformers/all-MiniLM-L6-v2"

    # Service Port for this GenAI module
    GENAI_PORT: int = 8001

    # Text Processing Settings
    CHUNK_SIZE: int = 1000
    CHUNK_OVERLAP: int = 200
    TEXT_SPLITTER_SEPARATORS: List[str] = ["\n\n", "\n", " ", "", "\t"] 

    # RAG Pipeline Settings (for Sub-Issue 6)
    RAG_TOP_K: int = 5
    RAG_LLM_TEMPERATURE: float = 0.1
    RAG_PROMPT_TEMPLATE: str = """
    You are an expert academic assistant. Use the following retrieved context from the user's uploaded documents to answer their question.
    Your answer must be based *only* on the provided context.
    If the context does not contain the answer, state that you cannot answer the question with the provided information.
    Do not use any outside knowledge.

    CONTEXT:
    {context}

    QUESTION:
    {question}

    ANSWER:
    """
    
    model_config = SettingsConfigDict(env_file=".env", extra="ignore", env_file_encoding='utf-8')


settings = Settings()
