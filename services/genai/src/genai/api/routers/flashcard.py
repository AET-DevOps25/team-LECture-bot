from fastapi import APIRouter, HTTPException, status, Body
from genai.api import schemas
from genai.pipelines.flashcard_pipeline import FlashcardPipeline


router = APIRouter()
flashcard_pipeline = FlashcardPipeline()

@router.post(
    "/generate",
    response_model=schemas.FlashcardResponse,
    summary="Generate flashcards from a document",
    description="Generates flashcards based on the provided document ID and course space ID. It retrieves all chunks of the document, processes them, and generates flashcards using an LLM.",
)
async def generate_flashcards_endpoint(
    payload: schemas.FlashcardRequest = Body(...)
):
    print(f"Flashcard generation request received for document: {payload.document_id}")
    try:
        response = flashcard_pipeline.process_request(payload)
        return response
    except ValueError as ve:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=str(ve))
    except Exception as e:
        print(f"Error during flashcard generation: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="An error occurred while generating flashcards."
        )

