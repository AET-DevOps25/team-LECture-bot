from typing import Dict, Any, Optional
from genai.services.vector_store_service import VectorStoreService

class DeindexingPipeline:
    """
    Orchestrates the de-indexing (removal) of all chunks for a document from the vector database.
    """
    def __init__(self):
        self.vector_store = VectorStoreService()
        print("DeindexingPipeline initialized with VectorStoreService.")

    def deindex_document(self, document_id: str, course_space_id: Optional[str] = None) -> Dict[str, Any]:
        """
        Removes all chunks for a document (optionally within a course space) from Weaviate.
        Returns a dict with status and number of deleted objects.
        """
        print(f"Starting de-indexing for document_id: {document_id}, course_space_id: {course_space_id}")
        result = self.vector_store.delete_document(document_id, course_space_id)
        print(f"De-indexing result: {result}")
        return result
