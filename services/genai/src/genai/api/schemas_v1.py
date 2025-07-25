# generated by datamodel-codegen:
#   filename:  genai-backend-openapi.yaml
#   timestamp: 2025-07-19T08:17:24+00:00

from __future__ import annotations

from typing import List, Optional

from pydantic import BaseModel, Field


class IndexRequest(BaseModel):
    document_id: str = Field(..., examples=['doc-123'])
    course_space_id: str = Field(..., examples=['cs-456'])
    text_content: str = Field(..., examples=['This is the content of the document.'])


class IndexResponse(BaseModel):
    message: Optional[str] = Field(None, examples=['Document indexed successfully'])
    document_id: Optional[str] = Field(None, examples=['doc-123'])
    status: Optional[str] = Field(
        None, description='Status of the indexing operation.', examples=['completed']
    )
    chunks_processed: Optional[int] = Field(
        None, description='Number of text chunks processed.', examples=[5]
    )
    chunks_stored_in_weaviate: Optional[int] = Field(
        None,
        description='Number of chunks successfully stored in Weaviate.',
        examples=[5],
    )


class QueryRequest(BaseModel):
    query_text: str = Field(..., examples=['What is this document about?'])
    course_space_id: str = Field(..., examples=['cs-456'])


class FlashcardRequest(BaseModel):
    course_space_id: str = Field(..., examples=['cs-456'])
    document_id: Optional[str] = Field(None, examples=['doc-123'])


class Flashcard(BaseModel):
    question: Optional[str] = Field(None, examples=['What is the capital of France?'])
    answer: Optional[str] = Field(None, examples=['Paris'])


class FlashcardsForDocument(BaseModel):
    document_id: Optional[str] = None
    flashcards: Optional[List[Flashcard]] = None


class FlashcardResponse(BaseModel):
    course_space_id: Optional[str] = None
    flashcards: Optional[List[FlashcardsForDocument]] = None
    error: Optional[str] = Field(
        None,
        description='An error message if the flashcard generation failed.',
        examples=['Failed to retrieve documents from the vector store.'],
    )


class Citation(BaseModel):
    document_id: str = Field(
        ...,
        description='The ID of the document from which the citation was retrieved.',
        examples=['doc-123'],
    )
    chunk_id: str = Field(
        ...,
        description='A unique identifier for the specific chunk within the document (e.g., chunk index, page number).',
        examples=['0'],
    )
    document_name: Optional[str] = Field(
        None,
        description='The name or title of the source document.',
        examples=['Lecture Slides Week 5'],
    )
    retrieved_text_preview: str = Field(
        ...,
        description='A short snippet of the text that was retrieved and used as context.',
        examples=[
            'LangChain is a framework for developing applications powered by language models.'
        ],
    )


class QueryResponse(BaseModel):
    answer: Optional[str] = Field(None, examples=['This document is about...'])
    citations: Optional[List[Citation]] = None
