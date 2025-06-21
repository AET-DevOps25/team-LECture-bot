from typing import List, Dict, Any, Optional
from weaviate.classes.query import Filter

from genai.services.embedding_service import EmbeddingService
from genai.services.vector_store_service import VectorStoreService
from genai.services.llm_service import LLMService  # We will create this next
from genai.api import schemas
from genai.core.config import settings

class QAPipeline:
    """
    Orchestrates the Retrieval-Augmented Generation (RAG) process for Q&A.
    """
    def __init__(self):
        self.embedder = EmbeddingService()
        self.vector_store = VectorStoreService()
        self.llm_service = LLMService()
        print("QAPipeline initialized.")

    def _create_context_filter(self, course_space_id: Optional[str]) -> Optional[Filter]:
        """Creates a filter to scope the search to a specific course space."""
        if not course_space_id:
            return None
        
        return Filter.by_property("course_space_id").equal(course_space_id)

    def _format_context(self, search_results: List[Dict[str, Any]]) -> str:
        """Formats retrieved document chunks into a string context."""
        if not search_results:
            return "No relevant documents found."
        
        context_parts = []
        for i, result in enumerate(search_results):
            properties = result.get('properties', {})
            text_content = properties.get('text_content', '')
            doc_id = properties.get('document_id', 'N/A')
            chunk_idx = properties.get('chunk_index', 'N/A')
            context_parts.append(f"Citation {i+1} (Document: {doc_id}, Chunk: {chunk_idx}):\n{text_content}")
            
        return "\n\n---\n\n".join(context_parts)

    def _get_unique_and_valid_search_results(self, search_results: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Filters search results to include only unique chunks that have valid text content.
        Uniqueness is based on (document_id, chunk_index).
        The order of the first appearance of unique valid chunks is preserved.
        """
        unique_results_map: Dict[tuple[str, str], Dict[str, Any]] = {}
        
        for result in search_results:
            properties = result.get('properties', {})
            doc_id = properties.get('document_id')
            # chunk_index could be int or str from Weaviate, ensure it's str for key
            chunk_idx_val = properties.get('chunk_index') 

            if doc_id is None or chunk_idx_val is None:
                # Skip results without proper identifiers to form a unique key
                continue

            chunk_idx_str = str(chunk_idx_val) # Convert to string for consistent keying
            unique_key = (doc_id, chunk_idx_str)

            if unique_key not in unique_results_map:
                text_content = properties.get('text_content')
                if text_content and text_content.strip(): # Ensure there's actual text
                    # Store the first valid instance of this chunk
                    unique_results_map[unique_key] = result
                
        return list(unique_results_map.values())

    def process_query(self, request: schemas.QueryRequest) -> schemas.QueryResponse:
        """
        Processes a single Q&A query using the RAG pipeline.
        This is a regular synchronous method.
        """
        # 1. Generate an embedding for the user's query_text
        query_vector = self.embedder.embed_text(request.query_text)
        if not query_vector:
            raise ValueError("Failed to generate query embedding.")

        # 2. Retrieve relevant document chunks from Weaviate
        context_filter = self._create_context_filter(request.course_space_id)
        search_results = self.vector_store.similarity_search(
            query_vector=query_vector,
            limit=settings.RAG_TOP_K,
            filter_expression=context_filter
        )

        # Filter for unique and valid results to avoid redundant or empty context/citations
        valid_search_results = self._get_unique_and_valid_search_results(search_results)

        # 3. Format the retrieved context from valid results for the LLM prompt
        formatted_context = self._format_context(valid_search_results)

        # 4. Generate the final answer using the LLM
        answer = self.llm_service.generate_answer(request.query_text, formatted_context)
        
        # 5. Format citations from the valid (unique and with text) search results
        citations = [
            schemas.Citation(
                document_id=result.get('properties', {}).get('document_id'),
                # Ensure chunk_id is string, matching the key used in _get_unique_and_valid_search_results
                chunk_id=str(result.get('properties', {}).get('chunk_index')),
                source_details=f"Document ID: {result.get('properties', {}).get('document_id')}",
                # text_content should be present due to the filter in _get_unique_and_valid_search_results
                retrieved_text_preview=(result.get('properties', {}).get('text_content') or "")[:150] + "..."
            ) for result in valid_search_results
        ]

        return schemas.QueryResponse(answer=answer, citations=citations)