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
    LLM_PROVIDER: str = "tum_aet"#ollama" # Default provider
    print("LLM_PROVIDER:", LLM_PROVIDER)

    # -- OpenAI Settings --
    OPENAI_API_KEY: Optional[str] = None # Make it optional
    OPENAI_MODEL_NAME: str = "gpt-4o-mini"
    # -- Ollama Settings --
    OLLAMA_BASE_URL: str = "http://localhost:11434" # Default Ollama API URL
    # OLLAMA_MODEL_NAME: str = "llama3" # Default model to use with Ollama
    OLLAMA_MODEL_NAME: str = "llama3:8b-instruct-q4_K_M" # Example of a quantized model

    # -- TUM AET GPU LLM Settings --
    TUM_AET_LLM_API_BASE: Optional[str] = None
    TUM_AET_LLM_API_KEY: Optional[str] = None
    print("TUM_AET_LLM_API_BASE:", TUM_AET_LLM_API_BASE)
    print("TUM_AET_LLM_API_KEY:", TUM_AET_LLM_API_KEY)
    TUM_AET_LLM_MODEL_NAME: str = "llama3.3:latest" # Default model for TUM AET LLM

    
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
    FLASHCARD_PROMPT_TEMPLATE: str = """
    You are an expert in creating study materials. Based on the following context from a document, generate a list of concise question-and-answer flashcards that cover the key concepts.
    Your response MUST be a valid JSON list of objects. Each object in the list MUST contain two string keys: "question" and "answer".
    Do not include any other text or explanations outside of the JSON list itself.

    CONTEXT:
    {context}

    JSON_FLASHCARDS:
    """
    
    model_config = SettingsConfigDict(env_file=".env", extra="ignore", env_file_encoding='utf-8')


settings = Settings()
