"""Service to interact with the configured Large Language Model."""

from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate

from genai.core.config import settings
from genai.core.llm_factory import get_llm


class LLMService:
    """
    A service that encapsulates the logic for interacting with the LLM.

    It uses the `get_llm` factory to obtain a configured LLM instance
    and sets up a RAG chain for generating answers.
    """

    def __init__(self):
        self.llm = get_llm()
        self.prompt_template = ChatPromptTemplate.from_template(settings.RAG_PROMPT_TEMPLATE)
        self.chain = self.prompt_template | self.llm | StrOutputParser()
        print(f"LLMService initialized with provider: {settings.LLM_PROVIDER}")

    def generate_answer(self, question: str, context: str) -> str:
        """Generates an answer using the LLM based on a question and context."""
        return self.chain.invoke({"context": context, "question": question})