
from typing import List
# Updated import for HuggingFaceEmbeddings
from langchain_huggingface import HuggingFaceEmbeddings 
from genai.core.config import settings
import time

class EmbeddingService:
    """
    Service for generating text embeddings using a sentence-transformer model.
    """
    def __init__(self, model_name: str = settings.EMBEDDING_MODEL_NAME):
        """
        Initializes the EmbeddingService and loads the specified sentence-transformer model.

        Args:
            model_name: The name of the sentence-transformer model to use
                        (e.g., 'sentence-transformers/all-MiniLM-L6-v2').
                        Defaults to the model specified in settings.EMBEDDING_MODEL_NAME.
        """
        self.model_name = model_name
        
        model_kwargs = {'device': 'cpu'} 
        encode_kwargs = {'normalize_embeddings': False} # Set to True if you want to normalize for cosine similarity

        print(f"Initializing EmbeddingService with model: {self.model_name}")
        print(f"Model kwargs: {model_kwargs}, Encode kwargs: {encode_kwargs}")

        try:
            start_time = time.time()
            # The HuggingFaceEmbeddings class itself is now imported from langchain_huggingface
            self.embedding_model = HuggingFaceEmbeddings(
                model_name=self.model_name,
                model_kwargs=model_kwargs,
                encode_kwargs=encode_kwargs
            )
            load_time = time.time() - start_time
            print(f"Successfully loaded embedding model '{self.model_name}' in {load_time:.2f} seconds.")
        except Exception as e:
            print(f"Error loading embedding model '{self.model_name}': {e}")
            raise RuntimeError(f"Failed to load embedding model: {e}") from e

    def embed_text(self, text: str) -> List[float]:
        """
        Generates an embedding for a single piece of text.

        Args:
            text: The input text.

        Returns:
            A list of floats representing the embedding, or an empty list on error/empty input.
        """
        if not self.embedding_model:
            print("Error: Embedding model is not loaded.")
            return [] 
        if not text or not text.strip():
            print("Warning: embed_text called with empty or whitespace-only string. Returning empty list.")
            return []
            
        try:
            embedding = self.embedding_model.embed_query(text)
            return embedding
        except Exception as e:
            print(f"Error generating embedding for text: '{text[:100]}...': {e}")
            return [] 

    def embed_texts(self, texts: List[str]) -> List[List[float]]:
        """
        Generates embeddings for a batch of texts.
        Returns a list of embeddings corresponding to the input texts, 
        with empty lists for texts that were empty or caused an error.

        Args:
            texts: A list of input texts.

        Returns:
            A list of embeddings (List[List[float]]).
        """
        if not self.embedding_model:
            print("Error: Embedding model is not loaded.")
            return [[] for _ in texts]
        if not texts:
            return []
        
        final_embeddings: List[List[float]] = [[] for _ in texts]
        texts_to_process_with_indices: List[tuple[int, str]] = []

        for i, text_original in enumerate(texts):
            if text_original and text_original.strip():
                texts_to_process_with_indices.append((i, text_original))
            else:
                print(f"Skipping empty or whitespace-only string at index {i}.")

        if not texts_to_process_with_indices:
            print("Warning: embed_texts called with a list of only empty or whitespace-only strings.")
            return final_embeddings

        actual_texts_to_embed = [text for _, text in texts_to_process_with_indices]

        try:
            generated_embeddings = self.embedding_model.embed_documents(actual_texts_to_embed)
            
            for i, (original_idx, _) in enumerate(texts_to_process_with_indices):
                if i < len(generated_embeddings):
                    final_embeddings[original_idx] = generated_embeddings[i]
                else:
                    print(f"Warning: Missing embedding for text at original index {original_idx}.")
            
            return final_embeddings

        except Exception as e:
            print(f"Error generating embeddings for batch of {len(actual_texts_to_embed)} texts: {e}")
            return final_embeddings


# Example Usage (can be removed or moved to a test file later):
if __name__ == "__main__":
    print("Attempting to initialize EmbeddingService...")
    try:
        embedding_service = EmbeddingService() 
        
        sample_texts = [
            "This is the first document.",
            "Another document for testing embeddings.",
            "LECure-bot is an intelligent course material assistant.",
            "", 
            "    ", 
            "Final piece of text."
        ]

        print(f"\nEmbedding single text: '{sample_texts[0]}'")
        single_embedding = embedding_service.embed_text(sample_texts[0])
        if single_embedding:
            print(f"  Embedding (first 5 dims): {single_embedding[:5]}")
            print(f"  Embedding dimension: {len(single_embedding)}")
        else:
            print("  Failed to generate single embedding or input was empty.")

        print(f"\nEmbedding batch of {len(sample_texts)} texts...")
        batch_embeddings = embedding_service.embed_texts(sample_texts)
        
        if batch_embeddings: 
            for i, emb in enumerate(batch_embeddings):
                original_text_preview = sample_texts[i][:30] if sample_texts[i] else "[EMPTY]"
                if emb:
                    print(f"  Text {i+1} ('{original_text_preview}...') Embedding (first 5 dims): {emb[:5]}, Dim: {len(emb)}")
                else:
                    print(f"  Text {i+1} ('{original_text_preview}...') Failed, empty text, or error; no embedding.")
        else: 
            print("  Failed to generate batch embeddings or input list was empty.")

    except RuntimeError as e:
        print(f"RuntimeError during EmbeddingService initialization or usage: {e}")
    except ImportError as e:
        print(f"ImportError: {e}. Make sure langchain, langchain-community, langchain-huggingface, and sentence-transformers are installed.")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
