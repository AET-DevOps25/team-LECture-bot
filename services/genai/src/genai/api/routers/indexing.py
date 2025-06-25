from fastapi import APIRouter, HTTPException, status, Body
from genai.pipelines.indexing_pipeline import IndexingPipeline
from genai.api import schemas

# Add a print statement for debugging to confirm the module is loaded
print("✅ Indexing router module loaded.")

router = APIRouter()
pipeline = IndexingPipeline()

@router.post(
    "/",
    response_model=schemas.IndexDocumentResponse,
    summary="Index a document for RAG",
    description="Receives document text, chunks it, generates embeddings, and stores them in the vector DB.",
    status_code=status.HTTP_200_OK,
)
async def index_document_in_pipeline(
    payload: schemas.IndexDocumentRequest = Body(...)
):
    print(f"Indexing request received for document: {payload.document_id}")
    try:
        result = pipeline.process_document(payload)
        return schemas.IndexDocumentResponse(**result)
    except Exception as e:
        print(f"Error during indexing pipeline: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"An unexpected error occurred: {e}",
        )

print("✅ Indexing router loaded successfully.")
