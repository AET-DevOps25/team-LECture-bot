from langchain_openai import ChatOpenAI
from langchain_community.chat_models import ChatOllama
from langchain.prompts import ChatPromptTemplate
from genai.core.config import settings

class LLMService:
    """Service for interacting with Large Language Models."""
    def __init__(self):
        # Dynamically select the LLM based on the provider in settings
        if settings.LLM_PROVIDER == "openai":
            if not settings.OPENAI_API_KEY:
                raise ValueError("OPENAI_API_KEY must be set in .env file when LLM_PROVIDER is 'openai'.")
            self.llm = ChatOpenAI(
                model_name=settings.OPENAI_MODEL_NAME,
                api_key=settings.OPENAI_API_KEY,
                temperature=settings.RAG_LLM_TEMPERATURE
            )
            print(f"LLMService initialized with provider: OpenAI (model: {settings.OPENAI_MODEL_NAME})")
        elif settings.LLM_PROVIDER == "ollama":
            self.llm = ChatOllama(
                model=settings.OLLAMA_MODEL_NAME,
                base_url=settings.OLLAMA_BASE_URL,
                temperature=settings.RAG_LLM_TEMPERATURE
            )
            print(f"LLMService initialized with provider: Ollama (model: {settings.OLLAMA_MODEL_NAME})")
        else:
            raise NotImplementedError(f"LLM provider '{settings.LLM_PROVIDER}' is not yet supported.")
            
        # The prompt template remains the same regardless of the provider
        self.prompt_template = ChatPromptTemplate.from_template(settings.RAG_PROMPT_TEMPLATE)

    def generate_answer(self, query: str, context: str) -> str:
        """
        Generates an answer using the LLM based on a query and context.
        """
        prompt = self.prompt_template.format(context=context, question=query)
        response = self.llm.invoke(prompt)
        return response.content

    def generate_response(self, prompt_template_str: str, input_data: dict) -> str:
        """
        Generates a response using the LLM based on a custom prompt template and input data.
        """
        prompt_template = ChatPromptTemplate.from_template(prompt_template_str)
        print(f"Generating response with prompt template: {prompt_template_str}")
        prompt = prompt_template.format(**input_data)
        print(f"Formatted prompt: {prompt}")
        response = self.llm.invoke(prompt)
        print(f"LLM response received: {response.content}")
        return response.content
