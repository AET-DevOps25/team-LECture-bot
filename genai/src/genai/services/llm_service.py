from langchain_openai import ChatOpenAI
from langchain.prompts import ChatPromptTemplate
from genai.core.config import settings

class LLMService:
    """Service for interacting with Large Language Models."""
    def __init__(self):
        if settings.LLM_PROVIDER == "openai":
            self.llm = ChatOpenAI(
                model_name=settings.OPENAI_MODEL_NAME,
                api_key=settings.OPENAI_API_KEY,
                temperature=settings.RAG_LLM_TEMPERATURE
            )
        else:
            raise NotImplementedError(f"LLM provider '{settings.LLM_PROVIDER}' is not yet supported.")
            
        self.prompt_template = ChatPromptTemplate.from_template(settings.RAG_PROMPT_TEMPLATE)
        print(f"LLMService initialized with provider: {settings.LLM_PROVIDER} and model: {settings.OPENAI_MODEL_NAME}")

    def generate_answer(self, query: str, context: str) -> str:
        """
        Generates an answer using the LLM based on a query and context.
        """
        prompt = self.prompt_template.format(context=context, question=query)
        response = self.llm.invoke(prompt)
        return response.content