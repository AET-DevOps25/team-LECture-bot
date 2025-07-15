"""Contains all the data models used in inputs/outputs"""

from .citation import Citation
from .flashcard import Flashcard
from .flashcard_request import FlashcardRequest
from .flashcard_response import FlashcardResponse
from .flashcards_for_document import FlashcardsForDocument
from .index_request import IndexRequest
from .index_response import IndexResponse
from .query_request import QueryRequest
from .query_response import QueryResponse

__all__ = (
    "Citation",
    "Flashcard",
    "FlashcardRequest",
    "FlashcardResponse",
    "FlashcardsForDocument",
    "IndexRequest",
    "IndexResponse",
    "QueryRequest",
    "QueryResponse",
)
