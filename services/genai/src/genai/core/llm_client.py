"""LLM client factory and custom client implementations."""

from typing import Optional, Dict, Any
from langchain.chat_models import ChatOpenAI
from genai.core.config import settings

class TUMAETLLM(ChatOpenAI):
    """
    LangChain ChatOpenAI wrapper for the TUM AET LLM endpoint.

    This client configures the ChatOpenAI class to use the TUM AET API,
    which is assumed to be OpenAI-compatible.
    """

    def __init__(self, **kwargs: Any):
        """
        Initializes the TUMAETLLM client.

        It sets the base URL and API key from the application settings,
        and allows overriding other ChatOpenAI parameters via kwargs.
        """
        if not settings.TUM_AET_LLM_API_BASE or not settings.TUM_AET_LLM_API_KEY:
            raise ValueError(
                "TUM AET LLM API base URL and API key must be configured."
            )
        
        model_kwargs: Dict[str, Any] = {
            "model_name": settings.TUM_AET_LLM_MODEL_NAME,
            "openai_api_base": settings.TUM_AET_LLM_API_BASE + "/chat/completions", # LangChain still expects /chat/completions
            "openai_api_key": settings.TUM_AET_LLM_API_KEY,
            "max_tokens": 1024,  # Reasonable default, adjust as needed
            "temperature": 0.1,  # For more deterministic output
        }
        # Allow overriding of any of the above, or setting other valid ChatOpenAI params
        model_kwargs.update(kwargs)
        super().__init__(**model_kwargs)