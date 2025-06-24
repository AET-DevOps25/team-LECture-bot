from pydantic import BaseModel, Field
from typing import Optional, List

class IndexDocumentRequest(BaseModel):
    document_id: str = Field(..., description="Unique identifier for the document from the main system")
    text_content: str = Field(..., description="Text content of the document")
    course_space_id: Optional[str] = Field(None, description="Contextual course space ID from the main system")
    # Add any other metadata you need to associate with chunks, e.g., original_filename: Optional[str] = None

class IndexDocumentResponse(BaseModel):
    document_id: str
    status: str # e.g., "received", "indexing_started", "completed", "failed"
    message: Optional[str] = None
    # num_chunks_indexed: Optional[int] = None # Consider adding later

class QueryRequest(BaseModel):
    query_text: str = Field(..., description="User query")
    course_space_id: Optional[str] = Field(None, description="Contextual course space ID for filtering retrieval")
    # top_k: Optional[int] = Field(3, description="Number of relevant chunks to retrieve") # Example advanced param

class Citation(BaseModel):
    document_id: str # The original document ID
    chunk_id: Optional[str] = None # ID of the specific chunk in the vector DB
    source_details: Optional[str] = None # e.g., page number, section header, if available from chunk metadata
    retrieved_text_preview: Optional[str] = None # Small preview of the retrieved chunk

class QueryResponse(BaseModel):
    answer: str
    citations: List[Citation] = []
    # error_message: Optional[str] = None # For graceful error reporting to client


# FLASCHARD shamas

class Flashcard(BaseModel):
    question: str = Field(..., description="The question side of the flashcard")
    answer: str = Field(..., description="The answer side of the flashcard")


class FlashcardRequest(BaseModel):
    course_space_id: str  = Field(..., description="Contextual course space ID for filtering flashcard generation")
    document_id: Optional[str] = Field(None, description="Optional: Specific document ID. If omitted, generates from all documents in the course space.")
    #number_of_flashcards: Optional[int] = Field(None, description="Number of flashcards to generate from the document")

#class FlashcardResponse(BaseModel):
#    scope: str = Field(..., description="Scope of the flashcard generation (e.g., 'course_space' or 'document')")
#    flashcards: List[Flashcard] = Field(..., description="List of generated flashcards")
#    status: str = Field(..., description="Status of the flashcard generation process")
#    message: Optional[str] = None # e.g., "Flashcards generated successfully" or error message 

class FlashcardsForDocument(BaseModel):
    document_id: str
    flashcards: List[Flashcard]

# Update the main response model
class FlashcardResponse(BaseModel):
    course_space_id: str
    results: List[FlashcardsForDocument]
