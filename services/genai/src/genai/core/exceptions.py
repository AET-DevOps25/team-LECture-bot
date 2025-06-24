"""Custom exceptions for the GenAI service."""

class GenAIError(Exception):
    """Base exception class for the GenAI service."""
    pass

class LLMConfigError(GenAIError):
    """Raised when there is an error in the LLM configuration."""
    pass