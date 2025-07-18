import weaviate # Main client library for v4
import weaviate.classes as wvc # For v4 specific classes like Property, DataType, Filter
from typing import List, Dict, Any, Optional
from genai.core.config import settings
import uuid

# Define the structure of a document to be added to Weaviate
class WeaviateInputDocument(Dict[str, Any]):
    properties: Dict[str, Any]
    vector: List[float]
    # uuid: Optional[uuid.UUID] # Weaviate client v4 expects uuid.UUID type for UUIDs

class VectorStoreService:
    """
    Service for interacting with Weaviate vector database using Python Client v4.
    Handles connection, schema creation, data upsertion, and similarity search.
    """
    def __init__(self,
                 weaviate_url: str = settings.WEAVIATE_URL,
                 api_key: Optional[str] = settings.WEAVIATE_API_KEY,
                 class_name: str = settings.WEAVIATE_CLASS_NAME,
                 embedding_dimension: int = 384): 
        self.weaviate_url = weaviate_url
        self.api_key = api_key
        self.class_name = class_name
        self.embedding_dimension = embedding_dimension

        auth_creds = weaviate.auth.AuthApiKey(api_key=self.api_key) if self.api_key else None
        
        connect_kwargs = {}
        prepared_additional_headers = {} 
        if prepared_additional_headers:
            connect_kwargs['additional_headers'] = prepared_additional_headers

        print(f"Attempting to connect to Weaviate at {self.weaviate_url}...")
        try:
            http_protocol, http_address = self.weaviate_url.split("://")
            http_host_port = http_address.split(":")
            http_host = http_host_port[0]
            http_port_val = int(http_host_port[1]) if len(http_host_port) > 1 else (443 if http_protocol == "https" else 80)

            grpc_host_val = http_host 
            grpc_port_val = 50051 

            self.client = weaviate.connect_to_custom(
                http_host=http_host,
                http_port=http_port_val,
                http_secure=http_protocol == "https",
                grpc_host=grpc_host_val, 
                grpc_port=grpc_port_val, 
                grpc_secure=False, 
                auth_credentials=auth_creds,
                **connect_kwargs 
            )
            
            if not self.client.is_ready():
                self.client.close()
                raise ConnectionError("Weaviate client is not ready. Check connection and instance status.")
            print(f"Successfully connected to Weaviate. Server version: {self.client.get_meta()['version']}")
            self._ensure_schema_exists()
        except Exception as e:
            print(f"Error connecting to Weaviate or ensuring schema: {e}")
            if hasattr(self, 'client') and self.client.is_connected():
                self.client.close()
            raise RuntimeError(f"Failed to initialize VectorStoreService: {e}") from e

    def _ensure_schema_exists(self):
        """
        Checks if the defined collection exists in Weaviate, and creates it if not (v4 syntax).
        """
        try:
            if not self.client.collections.exists(self.class_name):
                print(f"Collection '{self.class_name}' not found. Creating now...")
                
                self.client.collections.create(
                    name=self.class_name,
                    description="Stores text chunks and their embeddings for LECture-bot.",
                    vectorizer_config=wvc.config.Configure.Vectorizer.none(), 
                    vector_index_config=wvc.config.Configure.VectorIndex.hnsw(
                        distance_metric=wvc.config.VectorDistances.COSINE 
                    ),
                    properties=[
                        wvc.config.Property(name="text_content", data_type=wvc.config.DataType.TEXT, description="The text content of the document chunk."),
                        wvc.config.Property(name="document_id", data_type=wvc.config.DataType.TEXT, description="The original document ID from the main system."),
                        wvc.config.Property(name="course_space_id", data_type=wvc.config.DataType.TEXT, description="The course space ID associated with the document."),
                        wvc.config.Property(name="chunk_index", data_type=wvc.config.DataType.INT, description="The sequential index of this chunk."),
                        wvc.config.Property(name="original_filename", data_type=wvc.config.DataType.TEXT, description="Original filename.", skip_vectorization=True), 
                    ]
                )
                print(f"Successfully created collection '{self.class_name}'.")
            else:
                print(f"Collection '{self.class_name}' already exists.")
        except Exception as e:
            print(f"An unexpected error occurred while ensuring collection schema: {e}")
            raise

    def add_documents(self, documents: List[WeaviateInputDocument]):
        """
        Adds a batch of documents (chunks with their embeddings and properties) to Weaviate using v4 client.
        """
        if not documents:
            print("No documents provided to add.")
            return {"status": "noop", "count": 0, "errors": 0}

        collection = self.client.collections.get(self.class_name)
        
        objects_to_insert = []
        for doc_data in documents:
            properties = doc_data.get("properties")
            vector = doc_data.get("vector")
            
            if not properties or not vector:
                print(f"Skipping document due to missing properties or vector: {str(doc_data)[:100]}")
                continue 

            # Weaviate client v4 expects uuid.UUID type for UUIDs
            doc_uuid_input = doc_data.get("uuid")
            if isinstance(doc_uuid_input, str):
                doc_uuid = uuid.UUID(doc_uuid_input)
            elif isinstance(doc_uuid_input, uuid.UUID):
                doc_uuid = doc_uuid_input
            else: # Generate a new one if not provided or not in correct type
                doc_uuid = uuid.uuid4()

            objects_to_insert.append(
                wvc.data.DataObject(
                    properties=properties,
                    vector=vector,
                    uuid=doc_uuid # Pass uuid.UUID object
                )
            )
        
        if not objects_to_insert:
            print("No valid documents to insert after filtering.")
            return {"status": "noop", "count": 0, "errors": (len(documents) - len(objects_to_insert))}

        print(f"Attempting to insert {len(objects_to_insert)} documents into '{self.class_name}'...")
        try:
            response = collection.data.insert_many(objects_to_insert)
            
            added_count = len(response.uuids)
            error_count = len(response.errors)
            
            print(f"Batch insertion completed. Successfully added: {added_count}, Errors: {error_count}")
            if response.has_errors:
                print("Errors during batch insertion:")
                for i, err_obj in response.errors.items(): 
                    print(f"  - Index {i}: {err_obj.message}")
            
            return {"status": "completed", "added": added_count, "errors": error_count, "error_messages": [err.message for err in response.errors.values()]}

        except Exception as e:
            print(f"An error occurred during batch document insertion: {e}")
            return {"status": "failed", "added": 0, "errors": len(objects_to_insert), "error_messages": [str(e)]}

    def delete_document(self, document_id: str, course_space_id: Optional[str] = None) -> Dict[str, Any]:
        """
        Deletes all objects in Weaviate with the given document_id (and optionally course_space_id).
        Returns a dict with status and number of deleted objects.
        """
        try:
            collection = self.client.collections.get(self.class_name)
            # Build filter: always filter by document_id, optionally by course_space_id
            if course_space_id:
                doc_filter = wvc.query.Filter.all_of([
                    wvc.query.Filter.by_property("document_id").equal(document_id),
                    wvc.query.Filter.by_property("course_space_id").equal(course_space_id)
                ])
            else:
                doc_filter = wvc.query.Filter.by_property("document_id").equal(document_id)

            # Fetch all objects matching the filter (get their UUIDs)
            response = collection.query.fetch_objects(filters=doc_filter, limit=1000)
            uuids_to_delete = [obj.uuid for obj in response.objects if obj.uuid]
            if not uuids_to_delete:
                print(f"No objects found to delete for document_id={document_id}, course_space_id={course_space_id}")
                return {"status": "not_found", "deleted": 0}

            # Delete all found objects
            deleted_count = 0
            errors = []
            for uuid_val in uuids_to_delete:
                try:
                    collection.data.delete_by_id(uuid_val)
                    deleted_count += 1
                except Exception as e:
                    print(f"Error deleting object {uuid_val}: {e}")
                    errors.append(str(e))
            print(f"Deleted {deleted_count} objects for document_id={document_id}, course_space_id={course_space_id}")
            return {"status": "completed", "deleted": deleted_count, "errors": errors}
        except Exception as e:
            print(f"Error during delete_document: {e}")
            return {"status": "failed", "deleted": 0, "error": str(e)}

    def similarity_search(self, 
                          query_vector: List[float], 
                          limit: int = 5,
                          filter_expression: Optional[wvc.query.Filter] = None 
                          ) -> List[Dict[str, Any]]:
        """
        Performs a similarity search in Weaviate using v4 client.
        """
        if not query_vector:
            print("Cannot perform search with an empty query vector.")
            return []

        collection = self.client.collections.get(self.class_name)
        
        try:
            response = collection.query.near_vector(
                near_vector=query_vector,
                limit=limit,
                filters=filter_expression, 
                # Corrected: MetadataQuery only takes specific fields like distance, certainty. UUID is on the object itself.
                return_metadata=wvc.query.MetadataQuery(distance=True), 
                return_properties=["text_content", "document_id", "course_space_id", "chunk_index", "original_filename"]
            )
            
            formatted_results = []
            for obj in response.objects: # obj is of type weaviate.collections.classes.types.WeaviateObject
                item = {
                    "properties": obj.properties,
                    "score": obj.metadata.distance if obj.metadata and obj.metadata.distance is not None else None, 
                    "weaviate_id": str(obj.uuid) if obj.uuid else None # Access obj.uuid directly
                }
                formatted_results.append(item)
            return formatted_results
        except Exception as e:
            print(f"Error during similarity search: {e}")
            return []

    def fetch_objects(self, filter_expression: any, limit: int = 1000) -> List[Dict[str, Any]]:
        """
        Fetches objects from Weaviate based on a filter expression.
        """
        
        try:
            collection = self.client.collections.get(self.class_name)
            response = collection.query.fetch_objects( limit=limit,
                    filters=filter_expression
            )

            results = []

            for item in response.objects:
                result = { "properties": item.properties }
                results.append(result)
            
            return results
        except Exception as e:
            print(f"Error fetching objects: {e}")
            return []

    def close(self):
        """Closes the Weaviate client connection."""
        if hasattr(self, 'client') and self.client.is_connected():
            print("Closing Weaviate client connection.")
            self.client.close()

    

    def get_unique_document_ids(self, course_space_id: str) -> List[str]:
        """
        Uses Weaviate aggregation to find all unique document_ids for a given course_space_id.
        """
        print(f"Aggregating to find unique document IDs for course_space_id: '{course_space_id}'")
        try:
            collection = self.client.collections.get(self.class_name)
            
            # Create a filter to scope the aggregation
            id_filter = wvc.query.Filter.by_property("course_space_id").equal(course_space_id)
            
            response = collection.aggregate.over_all(
                filters=id_filter,
                group_by="document_id" # The property we want to get unique values from
            )
            
            # The response contains a list of groups, each with the unique value
            unique_ids = [group.grouped_by.value for group in response.groups]
            print(f"Found {len(unique_ids)} unique documents.")
            return unique_ids
            
        except Exception as e:
            print(f"An error occurred during document ID aggregation: {e}")
            return []

# Example Usage (can be removed or moved to a test file later):
if __name__ == "__main__":
    print("Attempting to initialize VectorStoreService...")
    vector_store: Optional[VectorStoreService] = None 
    try:
        vector_store = VectorStoreService(embedding_dimension=384)

        print("\n--- Example: Adding Documents ---")
        from genai.services.embedding_service import EmbeddingService as TestEmbeddingService 
        
        sample_docs_data = [
            {"text_content": "Chunk 1: Introduction to AI and its applications.", "document_id": "doc_001", "course_space_id": "cs_ai_101", "chunk_index": 0, "original_filename": "intro_ai.pdf"},
            {"text_content": "Chunk 2: Deep Learning fundamentals and neural networks.", "document_id": "doc_001", "course_space_id": "cs_ai_101", "chunk_index": 1, "original_filename": "intro_ai.pdf"},
            {"text_content": "Chunk 1: History of Computer Science from Babbage to modern day.", "document_id": "doc_002", "course_space_id": "cs_history_202", "chunk_index": 0, "original_filename": "cs_history.pdf"}
        ]
        
        weaviate_input_docs: List[WeaviateInputDocument] = []
        try:
            test_embedder = TestEmbeddingService()
            for doc_props in sample_docs_data:
                actual_vector = test_embedder.embed_text(doc_props["text_content"])
                if actual_vector:
                    weaviate_input_docs.append(WeaviateInputDocument(
                        properties=doc_props, 
                        vector=actual_vector
                        ))
                else:
                    print(f"Warning: Could not generate embedding for '{doc_props['text_content']}', skipping.")
        except Exception as emb_e:
            print(f"Could not initialize TestEmbeddingService for example: {emb_e}. Cannot add documents.")

        if weaviate_input_docs:
            add_result = vector_store.add_documents(weaviate_input_docs)
            print(f"Addition result: {add_result}")
        else:
            print("No documents prepared for addition due to embedding issues or empty sample data.")

        print("\n--- Example: Similarity Search ---")
        query_text_example = "Tell me about AI applications"
        query_vector_example: List[float] = []
        try:
            test_embedder_for_query = TestEmbeddingService() 
            query_vector_example = test_embedder_for_query.embed_text(query_text_example)
        except Exception as emb_e:
            print(f"Could not initialize TestEmbeddingService for query example: {emb_e}.")

        if query_vector_example:
            print(f"Searching with query: '{query_text_example}'")
            
            search_results_all = vector_store.similarity_search(query_vector_example, limit=3)
            print("\nSearch results (all course spaces):")
            if search_results_all:
                for res in search_results_all:
                    score_val = res.get('score')
                    score_str = f"{score_val:.4f}" if isinstance(score_val, float) else "N/A"
                    print(f"  ID: {res.get('weaviate_id')}, Score (distance): {score_str}, Text: '{res.get('properties',{}).get('text_content')}', DocID: {res.get('properties',{}).get('document_id')}")
            else:
                print("  No results found.")

            ai_course_filter = wvc.query.Filter.by_property("course_space_id").equal("cs_ai_101")
            print(f"\nSearching with filter for course_space_id='cs_ai_101'")
            search_results_filtered = vector_store.similarity_search(query_vector_example, limit=2, filter_expression=ai_course_filter)
            print("\nSearch results (filtered for 'cs_ai_101'):")
            if search_results_filtered:
                for res in search_results_filtered:
                    score_val = res.get('score')
                    score_str = f"{score_val:.4f}" if isinstance(score_val, float) else "N/A"
                    print(f"  ID: {res.get('weaviate_id')}, Score (distance): {score_str}, Text: '{res.get('properties',{}).get('text_content')}', DocID: {res.get('properties',{}).get('document_id')}")
            else:
                print("  No results found with the filter.")
        else:
            print("Could not generate query vector for the example search.")

    except RuntimeError as e:
        print(f"RuntimeError during VectorStoreService operation: {e}")
    except ImportError as e:
        print(f"ImportError: {e}. Make sure 'weaviate-client' is installed.")
    except ConnectionRefusedError as e: 
        print(f"ConnectionRefusedError: {e}. Is Weaviate running at {settings.WEAVIATE_URL} and accessible?")
    except Exception as e:
        print(f"An unexpected error occurred in the example: {e}")
        import traceback
        traceback.print_exc()
    finally:
        if vector_store and vector_store.client.is_connected(): 
            vector_store.close()
