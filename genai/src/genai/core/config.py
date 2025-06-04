from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Optional

class Settings(BaseSettings):
    APP_NAME: str = "GenAI Module for LECture-bot"
    API_V1_STR: str = "/api/v1"

    # Weaviate Configuration
    WEAVIATE_URL: str = "http://localhost:8080" # Default, adjust if your Weaviate runs elsewhere
    WEAVIATE_API_KEY: Optional[str] = None # If your Weaviate instance needs an API key

    # LLM Configuration (Example for OpenAI)
    OPENAI_API_KEY: Optional[str] = None
    EMBEDDING_MODEL_NAME: str = "sentence-transformers/all-MiniLM-L6-v2" # Default embedding model

    # Service Port for this GenAI module
    GENAI_PORT: int = 8001 # Port the GenAI FastAPI app will listen on

    model_config = SettingsConfigDict(env_file=".env", extra="ignore", env_file_encoding='utf-8')

settings = Settings()
