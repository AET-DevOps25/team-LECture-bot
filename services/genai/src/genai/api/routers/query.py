from fastapi import APIRouter, HTTPException, status, Body
from genai.api import schemas
from genai.pipelines.qa_pipeline import QAPipeline


router = APIRouter()

qa_pipeline = QAPipeline()


@router.post(
    "", # Changed from "/query" to "" to match /api/v1/query
    response_model=schemas.QueryResponse,
    summary="Submit a query for Retrieval-Augmented Generation (RAG)",
    description="Receives a user query and context (like course_space_id), retrieves relevant document chunks from the vector database, and generates an answer using an LLM."
)

def submit_query_for_rag(
    payload: schemas.QueryRequest = Body(...)
):
    print(f"RAG query received: '{payload.query_text}' for course space: {payload.course_space_id}")
    try:
        # Use the pipeline to get the response
        response = qa_pipeline.process_query(payload)
        return response
    except ValueError as ve:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail=str(ve))
    except Exception as e:
      print(f"Error during RAG processing: {e}")
      import traceback
      traceback.print_exc()
      raise HTTPException(
          status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
          detail="An error occurred while processing your query."
      )

# You can add more query-related endpoints here later, e.g., for flashcard generation if it uses a similar RAG approach.
# For example:
# @router.post("/generate-flashcards", ...)
# async def generate_flashcards_endpoint(...):
#     # ...
#     pass