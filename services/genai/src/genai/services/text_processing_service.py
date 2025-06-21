from typing import List, Dict, Any, Optional # Added Optional
from langchain.text_splitter import RecursiveCharacterTextSplitter, TextSplitter
from genai.core.config import settings # Import the global settings

class TextProcessingService:
    """
    Service for processing text, primarily focusing on chunking.
    It uses configuration from genai.core.config.settings for default chunker parameters.
    """
    def __init__(self, 
                 chunk_size: Optional[int] = None, 
                 chunk_overlap: Optional[int] = None,
                 separators: Optional[List[str]] = None,
                 text_splitter_type: str = "recursive_character"):
        """
        Initializes the TextProcessingService.

        Args:
            chunk_size: Target size for each text chunk. Defaults to settings.CHUNK_SIZE.
            chunk_overlap: Amount of overlap. Defaults to settings.CHUNK_OVERLAP.
            separators: List of separators for RecursiveCharacterTextSplitter. 
                        Defaults to settings.TEXT_SPLITTER_SEPARATORS.
            text_splitter_type: Type of LangChain text splitter. Currently "recursive_character".
        """
        self.chunk_size = chunk_size if chunk_size is not None else settings.CHUNK_SIZE
        self.chunk_overlap = chunk_overlap if chunk_overlap is not None else settings.CHUNK_OVERLAP
        resolved_separators = separators if separators is not None else settings.TEXT_SPLITTER_SEPARATORS
        
        if text_splitter_type == "recursive_character":
            self.splitter: TextSplitter = RecursiveCharacterTextSplitter(
                chunk_size=self.chunk_size,
                chunk_overlap=self.chunk_overlap,
                length_function=len,
                is_separator_regex=False,
                separators=resolved_separators
            )
        # elif text_splitter_type == "tiktoken":
        #     from langchain_text_splitters import TokenTextSplitter # Corrected import path if using newer langchain
        #     # Ensure tiktoken is installed: poetry add tiktoken
        #     self.splitter = TokenTextSplitter(
        #         encoding_name="cl100k_base", 
        #         chunk_size=self.chunk_size, # Note: This would be token count
        #         chunk_overlap=self.chunk_overlap # Note: This would be token count
        #     )
        else:
            raise ValueError(f"Unsupported text_splitter_type: {text_splitter_type}")

        print(f"TextProcessingService initialized with: type='{text_splitter_type}', "
              f"chunk_size={self.chunk_size}, chunk_overlap={self.chunk_overlap}, "
              f"separators={resolved_separators}")

    def chunk_text(self, text: str, metadata: Optional[Dict[str, Any]] = None) -> List[Dict[str, Any]]:
        """
        Splits a given text into smaller chunks.

        Args:
            text: The input text to be chunked.
            metadata: Optional metadata to be associated with each chunk. 
                      This metadata will be enriched with chunk-specific info.

        Returns:
            A list of dictionaries, where each dictionary represents a chunk
            and contains 'page_content' (the chunk text) and 'metadata'.
        """
        if not text:
            return []
        
        initial_documents_metadata = [metadata.copy() if metadata else {}]
        
        langchain_documents = self.splitter.create_documents([text], metadatas=initial_documents_metadata)

        processed_chunks = []
        for i, doc in enumerate(langchain_documents):
            # doc.metadata should already contain a copy of the initial metadata
            # plus any metadata added by the splitter itself (if any).
            chunk_metadata = doc.metadata 
            chunk_metadata["chunk_index"] = i # Add chunk-specific sequence
            
            processed_chunks.append({
                "page_content": doc.page_content,
                "metadata": chunk_metadata
            })
            
        print(f"Chunked text into {len(processed_chunks)} chunks.")
        return processed_chunks

# Example Usage (can be removed or moved to a test file later):
if __name__ == "__main__":
    sample_text = """This is the first paragraph. It contains several sentences.
    This is still the first paragraph.

    This is the second paragraph. It also has multiple sentences. We want to see how this splits.
    Let's add a bit more text to make the chunks meaningful.

    A third paragraph to ensure we have enough content. This service will be crucial for our RAG pipeline.
    Proper chunking is key to good retrieval.

    And a final short paragraph.
    """
    
    # Scenario 1: Initialize with defaults from settings (assuming settings are loaded)
    print("--- Scenario 1: Using defaults from settings ---")
    text_processor_default = TextProcessingService() 
    doc_metadata = {
        "document_id": "doc_123",
        "course_space_id": "cs_abc",
        "original_filename": "lecture1.pdf"
    }
    chunks_default = text_processor_default.chunk_text(sample_text, metadata=doc_metadata)
    if chunks_default:
        print(f"Total chunks: {len(chunks_default)}")
        # for i, chunk_data in enumerate(chunks_default):
        #     print(f"\nChunk {i+1} Content: '{chunk_data['page_content'][:70]}...' Metadata: {chunk_data['metadata']}")

    print("\n--- Scenario 2: Overriding defaults ---")
    # Scenario 2: Initialize with overridden parameters
    text_processor_override = TextProcessingService(chunk_size=150, chunk_overlap=30, separators=["\n\n", "\n"])
    chunks_override = text_processor_override.chunk_text(sample_text, metadata=doc_metadata)
    if chunks_override:
        print(f"Total chunks: {len(chunks_override)}")
        # for i, chunk_data in enumerate(chunks_override):
        #     print(f"\nChunk {i+1} Content: '{chunk_data['page_content'][:70]}...' Metadata: {chunk_data['metadata']}")

