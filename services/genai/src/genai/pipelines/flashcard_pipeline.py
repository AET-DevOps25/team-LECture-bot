from typing import Optional

from weaviate.classes import query
from genai.core.config import settings
from genai.services.vector_store_service import VectorStoreService
from genai.services.llm_service import LLMService

from weaviate.classes.query import Filter
from genai.api import schemas


import json


class FlashcardPipeline:
    def __init__(self):
        self.vector_store = VectorStoreService()
        self.llm_service = LLMService()
        self.llm_service.prompt_template = self.llm_service.prompt_template.from_template(
                settings.FLASHCARD_PROMPT_TEMPLATE
            )
        print("FlashcardPipeline initialized.")


    def _retrieve_context(self, request: schemas.FlashcardRequest) -> str:
        """
        Retrieves and concatenates all text chunks based on the request scope.
        If document_id is provided, it scopes to that document within the course.
        If not, it scopes to the entire course space
        """

        # 1. Initialize a list to hold filter conditions
        filters = [
                Filter.by_property("course_space_id").equal(request.course_space_id)
        ]

        if request.document_id:
            print(f"Retrieving context for document_id: {request.document_id}")
            filters.append(Filter.by_property("document_id").equal(request.document_id))
        else:
            print(f"Retrieving context for all documents in course_space_id: {request.course_space_id}")

        # 2. Combine all filters into a single filter expression
        combined_filter = Filter.all_of(filters)
        

        # 3. Perform a similarity search with a dummy vector to retrieve all chunks


        search_results = self.vector_store.fetch_objects(
            filter_expression=combined_filter,
            limit=10000  # Adjust limit as needed
        )


        if not search_results:
            return ""

        # 4. Sort the results by doc_id and chunk index to maintain order
        sorted_chunks = sorted(search_results, key=lambda x: (
            x.get('properties', {}).get('document_id', ''),
            x.get('properties', {}).get('chunk_index', 0)
        ))

        # 5. Extract text content from the sorted chunks in a single string
        return "\n\n".join([res['properties']['text_content'] for res in sorted_chunks])


    
    def _retrieve_context_for_document(self, document_id: str, course_space_id: str) -> str:
        """Retrieves all text chunks for a single document."""
        filters = Filter.all_of([
            Filter.by_property("course_space_id").equal(course_space_id),
            Filter.by_property("document_id").equal(document_id)
        ])
        
        search_results = self.vector_store.fetch_objects(filter_expression=filters, limit=10000)
        
        if not search_results: return ""
        
        sorted_results = sorted(search_results, key=lambda x: x.get('properties', {}).get('chunk_index', 0))
        return "\n\n".join([res['properties']['text_content'] for res in sorted_results])



    def _generate_flashcards_for_context(self, context: str) -> list[schemas.Flashcard]:
        """Generates a list of flashcard objects from a given text context."""
        if not context:
            return []

        llm_input_data = {"context": context}
        llm_response_str = self.llm_service.generate_flashcards(
            input_data=llm_input_data
        )
        
        try:
            flashcard_data = json.loads(llm_response_str)
            validated_flashcards = []
            if isinstance(flashcard_data, list):
                for item in flashcard_data:
                    if isinstance(item, dict) and "question" in item and "answer" in item:
                        validated_flashcards.append(schemas.Flashcard(**item))
            return validated_flashcards
        except (json.JSONDecodeError, TypeError):
            print(f"Warning: Failed to parse LLM response for a document. Response: {llm_response_str}")
            return []


    def process_request(self, request: schemas.FlashcardRequest) -> schemas.FlashcardResponse:
        """
        Orchestrates flashcard generation.
        - If a document_id is provided, it processes only that document.
        - If not, it finds all documents in the course_space and processes them individually.
        """
        document_ids_to_process = []
        if request.document_id:
            # Process a single, specified document
            document_ids_to_process.append(request.document_id)
        else:
            # Find all documents in the course space
            document_ids_to_process = self.vector_store.get_unique_document_ids(request.course_space_id)

        if not document_ids_to_process:
            raise ValueError(f"No documents found for course space: {request.course_space_id}")

        # This will hold the results for each document
        all_results: list[schemas.FlashcardsForDocument] = []

        # Loop through each document and generate flashcards
        for doc_id in document_ids_to_process:
            print(f"Processing document: {doc_id}")
            context = self._retrieve_context_for_document(doc_id, request.course_space_id)
            
            if not context:
                print(f"Warning: No content found for document {doc_id}. Skipping.")
                continue

            flashcards = self._generate_flashcards_for_context(context)
            
            if flashcards:
                all_results.append(
                    schemas.FlashcardsForDocument(document_id=doc_id, flashcards=flashcards)
                )
            else:
                print(f"No valid flashcards generated for document {doc_id}.")

        return schemas.FlashcardResponse(course_space_id=request.course_space_id, results=all_results)


    
    def generate_flashcards(self, request: schemas.FlashcardRequest) -> schemas.FlashcardResponse:
        """
        Generate flashcards based on the provided request.
        
        Args:
            request: FlashcardRequest containing document_id and optionally, course_space_id and number of flashcards.
        
        Returns:
            FlashcardResponse containing the generated flashcards.
        """
        print(f"Generating flashcards for document_id: {request.document_id}")


        # 1. Retrieve all chunks for the document
        full_context = self._retrieve_context(
            request=request
        )

        if not full_context:
            raise ValueError("Could not find any content for the specified scope.")


        llm_input_data = { 
            "context": full_context,
        }

        llm_response_str = self.llm_service.generate_response(
                prompt_template_str=settings.FLASHCARD_PROMPT_TEMPLATE,
                input_data= llm_input_data
        )


        print(f"LLM response received: {llm_response_str}")

        flaschcards = []
        try: 
            flashcards_data = json.loads(llm_response_str)

            if not isinstance(flashcards_data, list):
                print(f"LLM response did not return a JSON list. Response: {llm_response_str}")
                raise ValueError(f"LLM response failed to generate flashcards in the expected format. Response is not a valid JSON list: {llm_response_str}")

            for item in flashcards_data:
                if isinstance(item, dict) and 'question' in item and 'answer' in item:
                    flaschcards.append(schemas.Flashcard(**item))
                else:
                    print(f"Skipping invalid flashcard item: {item}")

            if not flaschcards:
                raise ValueError(f"LLM response did not return any valid flashcards. Response: {llm_response_str}")

            flashcards = [schemas.Flashcard(**fc) for fc in flashcards_data]
        except json.JSONDecodeError as e:
            print(f"Error {e} while parsing LLM response:", llm_response_str)
            raise ValueError(f"Failed to parse LLM response: {llm_response_str}") 
        except Exception as e:
            print(f"Unexpected error while processing LLM response: {llm_response_str}")
            raise 



        response_scope = request.document_id if request.document_id else request.course_space_id

        return schemas.FlashcardResponse(
            scope=response_scope,
            flashcards=flashcards,
            status="success",
            message="Flashcards generated successfully"
        )
