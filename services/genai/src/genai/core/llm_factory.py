"""Factory to create LLM instances based on settings."""

from langchain_core.language_models.chat_models import BaseChatModel
from langchain_openai import ChatOpenAI

from genai.core.config import Settings, settings
from genai.core.exceptions import LLMConfigError
from genai.core.llm_client import TUMAETLLM


def get_llm() -> BaseChatModel:
    """
    Factory function to get the configured LLM client.
    """
    return _get_llm(settings)


def _get_llm(llm_settings: Settings) -> BaseChatModel:
    """Returns a configured Chat model instance based on LLM provider."""
    llm_provider = llm_settings.LLM_PROVIDER.lower()

    if llm_provider == "openai":
        if not llm_settings.OPENAI_API_KEY:
            raise LLMConfigError("OPENAI_API_KEY must be configured for the 'openai' provider.")
        return ChatOpenAI(api_key=llm_settings.OPENAI_API_KEY, model=llm_settings.OPENAI_MODEL_NAME)
    
    elif llm_provider == "ollama":
        return ChatOpenAI(model_name=llm_settings.OLLAMA_MODEL_NAME, base_url=llm_settings.OLLAMA_BASE_URL)
    
    elif llm_provider == "tum_aet":
        return TUMAETLLM()
    
    else:
        raise LLMConfigError(f"Unsupported LLM provider: '{llm_provider}'")