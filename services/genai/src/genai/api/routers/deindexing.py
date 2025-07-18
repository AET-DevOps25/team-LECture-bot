from fastapi import APIRouter, HTTPException, status
from genai.pipelines.deindexing_pipeline import DeindexingPipeline

router = APIRouter()
pipeline = DeindexingPipeline()

@router.delete(
    "/{course_space_id}/{document_id}",
    summary="De-index a document (remove all its chunks from the vector DB)",
    description="Removes all chunks for a document (optionally within a course space) from the vector DB.",
    status_code=status.HTTP_200_OK,
)
async def deindex_document_in_pipeline(course_space_id: str, document_id: str):
    print(f"De-indexing request received for document: {document_id} in course_space: {course_space_id}")
    try:
        result = pipeline.deindex_document(document_id, course_space_id)
        return result
    except Exception as e:
        print(f"Error during de-indexing pipeline: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"An unexpected error occurred: {e}",
        )

print("✅ Deindexing router loaded successfully.")
