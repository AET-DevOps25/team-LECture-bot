from typing import List, Dict, Any, Optional
import uuid

from genai.services.text_processing_service import TextProcessingService
from genai.services.embedding_service import EmbeddingService
from genai.services.vector_store_service import VectorStoreService, WeaviateInputDocument
from genai.api.schemas import IndexDocumentRequest # For type hinting the input payload

class IndexingPipeline:
    """
    Orchestrates the document indexing process:
    1. Chunks text content.
    2. Generates embeddings for chunks.
    3. Stores chunks and their embeddings in the vector database.
    """
    def __init__(self):
        # Instantiate the required services
        # In a more complex setup, these could be injected (e.g., via FastAPI dependencies)
        self.text_processor = TextProcessingService()
        self.embedder = EmbeddingService()
        self.vector_store = VectorStoreService()
        print("IndexingPipeline initialized with TextProcessing, Embedding, and VectorStore services.")

    def process_document(self, payload: IndexDocumentRequest) -> Dict[str, Any]:
        """
        Processes a single document for indexing.

        Args:
            payload: The request payload containing document_id, text_content, and course_space_id.

        Returns:
            A dictionary with the status and details of the indexing process.
        """
        print(f"Starting indexing process for document_id: {payload.document_id}")

        if not payload.text_content or not payload.text_content.strip():
            print(f"No text content provided for document_id: {payload.document_id}. Skipping.")
            return {
                "document_id": payload.document_id,
                "status": "skipped",
                "message": "No text content provided.",
                "chunks_processed": 0
            }

        # 1. Chunk the text
        # Prepare initial metadata for chunks
        # We can get original_filename if it's part of IndexDocumentRequest schema or passed differently
        initial_chunk_metadata = {
            "document_id": payload.document_id,
            "course_space_id": payload.course_space_id,
            # "original_filename": payload.original_filename # If available in payload
        }
        # Remove None values from metadata to keep it clean
        initial_chunk_metadata = {k: v for k, v in initial_chunk_metadata.items() if v is not None}

        print(f"Step 1: Chunking text for document_id: {payload.document_id}")
        # `chunk_text` returns List[Dict[str, Any]] where each dict has 'page_content' and 'metadata'
        chunks_with_metadata: List[Dict[str, Any]] = self.text_processor.chunk_text(
            text=payload.text_content,
            metadata=initial_chunk_metadata
        )

        if not chunks_with_metadata:
            print(f"No chunks generated for document_id: {payload.document_id}. This might be due to very short text.")
            return {
                "document_id": payload.document_id,
                "status": "completed_no_chunks",
                "message": "No chunks were generated from the text content.",
                "chunks_processed": 0
            }
        
        print(f"Generated {len(chunks_with_metadata)} chunks for document_id: {payload.document_id}")

        # 2. Generate embeddings for all chunks in a batch
        chunk_texts_to_embed: List[str] = [chunk['page_content'] for chunk in chunks_with_metadata]
        
        print(f"Step 2: Generating embeddings for {len(chunk_texts_to_embed)} chunks...")
        try:
            chunk_embeddings: List[List[float]] = self.embedder.embed_texts(chunk_texts_to_embed)
        except Exception as e:
            print(f"Error during embedding generation for document_id {payload.document_id}: {e}")
            return {
                "document_id": payload.document_id,
                "status": "failed",
                "message": f"Error during embedding generation: {str(e)}",
                "chunks_processed": 0
            }

        if len(chunk_embeddings) != len(chunks_with_metadata):
            print(f"Mismatch in number of chunks and embeddings for document_id {payload.document_id}. "
                  f"Chunks: {len(chunks_with_metadata)}, Embeddings: {len(chunk_embeddings)}")
            # This might happen if some texts in `embed_texts` were empty and returned empty embeddings list
            # We need to ensure we only try to store chunks that have valid embeddings.
            # A robust way is to filter out chunks for which embedding failed (empty list from embed_texts).
            
            # Re-align chunks with their embeddings
            valid_chunks_for_storage = []
            for i, chunk_data in enumerate(chunks_with_metadata):
                if i < len(chunk_embeddings) and chunk_embeddings[i]: # Check if embedding is not empty
                    valid_chunks_for_storage.append({
                        "properties": chunk_data['metadata'], # Metadata from TextProcessingService already includes chunk_index etc.
                        "vector": chunk_embeddings[i],
                        # Optionally generate a deterministic UUID for each chunk here if desired
                        # "uuid": uuid.uuid5(uuid.NAMESPACE_DNS, f"{payload.document_id}-{chunk_data['metadata'].get('chunk_index', i)}")
                    })
            print(f"After filtering, {len(valid_chunks_for_storage)} chunks have valid embeddings.")
        else: # Happy path, all chunks got embeddings
            valid_chunks_for_storage = [
                WeaviateInputDocument(
                    properties=chunk_data['metadata'], # Metadata from TextProcessingService
                    vector=chunk_embeddings[i]
                    # uuid=uuid.uuid5(uuid.NAMESPACE_DNS, f"{payload.document_id}-{chunk_data['metadata'].get('chunk_index', i)}") # Optional deterministic UUID
                ) for i, chunk_data in enumerate(chunks_with_metadata) if chunk_embeddings[i] # Ensure embedding exists
            ]
        
        if not valid_chunks_for_storage:
            print(f"No valid chunks with embeddings to store for document_id: {payload.document_id}.")
            return {
                "document_id": payload.document_id,
                "status": "failed",
                "message": "No valid embeddings were generated for the text chunks.",
                "chunks_processed": 0
            }

        # 3. Store chunks and embeddings in Weaviate
        print(f"Step 3: Storing {len(valid_chunks_for_storage)} chunks with embeddings in Weaviate...")
        try:
            # The 'metadata' from chunk_text is already the properties dict for Weaviate
            # `add_documents` expects a list of dicts with 'properties' and 'vector' keys
            storage_result = self.vector_store.add_documents(valid_chunks_for_storage)
            print(f"Storage result for document_id {payload.document_id}: {storage_result}")

            if storage_result.get("errors", 0) > 0:
                # Partial success or failure
                return {
                    "document_id": payload.document_id,
                    "status": "partial_failure" if storage_result.get("added", 0) > 0 else "failed",
                    "message": f"Weaviate storage completed with {storage_result.get('errors',0)} errors. "
                               f"Added: {storage_result.get('added',0)} chunks.",
                    "chunks_processed": len(valid_chunks_for_storage),
                    "weaviate_errors": storage_result.get("error_messages", [])
                }
            
            print(f"Successfully indexed {storage_result.get('added', 0)} chunks for document_id: {payload.document_id}")
            return {
                "document_id": payload.document_id,
                "status": "completed",
                "message": f"Document indexed successfully. {storage_result.get('added',0)} chunks stored.",
                "chunks_processed": len(valid_chunks_for_storage),
                "chunks_stored_in_weaviate": storage_result.get('added', 0)
            }

        except Exception as e:
            print(f"Error during Weaviate storage for document_id {payload.document_id}: {e}")
            # Log the full traceback here in a real application
            return {
                "document_id": payload.document_id,
                "status": "failed",
                "message": f"Error during Weaviate storage: {str(e)}",
                "chunks_processed": len(valid_chunks_for_storage) # Chunks were processed up to this point
            }
        finally:
            # Ensure Weaviate client connection is closed if this pipeline instance owns it
            # However, if VectorStoreService is a shared instance, it shouldn't be closed here.
            # For now, assuming it's managed by VectorStoreService itself or a higher context.
            # self.vector_store.close() # Only if appropriate here
            pass

# Example (for standalone testing if needed, but typically tested via API endpoint)
# async def main_test():
#     pipeline = IndexingPipeline()
#     sample_payload = IndexDocumentRequest(
#         document_id="test_doc_001",
#         text_content="This is a sample document. It has multiple sentences for testing. Weaviate is a vector database. Langchain helps build LLM apps.",
#         course_space_id="cs_test_101"
#     )
#     result = await pipeline.process_document(sample_payload)
#     print("\n--- Indexing Pipeline Test Result ---")
#     import json
#     print(json.dumps(result, indent=2))

# if __name__ == "__main__":
#     import asyncio
#     asyncio.run(main_test())