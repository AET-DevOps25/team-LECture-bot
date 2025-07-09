from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
# Make sure to import the query router from genai.api.routers
from genai.api.routers import indexing, query, flashcard # <-- Added 'query' here
from genai.core.config import settings

app = FastAPI(
    title=settings.APP_NAME,
    version="0.1.0",
    description="GenAI module for LECture-bot, responsible for document indexing and Retrieval-Augmented Generation (RAG) Q&A.",
    openapi_url=f"{settings.API_V1_STR}/openapi.json" # Ensures API docs are under the v1 path
)

# Include the API routers
app.include_router(indexing.router, prefix="/api/v1/index", tags=["indexing"])
app.include_router(query.router, prefix="/api/v1/query", tags=["query"])
app.include_router(flashcard.router, prefix="/api/v1/flashcards", tags=["flashcard"])

# Basic health check endpoint
@app.get(f"{settings.API_V1_STR}/health", summary="Health Check", tags=["Monitoring"])
async def health_check():
    return {"status": "healthy", "module_name": settings.APP_NAME, "version": app.version}

@app.get("/health", summary="Container Health Check", tags=["Monitoring"])
def container_health_check():
    return {"status": "healthy", "module_name": settings.APP_NAME, "version": app.version}

# Optional: Basic root endpoint
@app.get("/", tags=["Root"])
async def read_root():
    return {"message": "Welcome to the GenAI Service"}

print("✅ All routers included. Service is ready.")

# Optional: Basic exception handler (can be expanded)
@app.exception_handler(Exception)
async def generic_exception_handler(request: Request, exc: Exception):
    # In a real app, you'd log the exception here
    print(f"Unhandled exception: {exc}") # Basic logging
    return JSONResponse(
        status_code=500,
        content={"detail": "An unexpected error occurred on the server."},
    )

# The following block is useful for running Uvicorn directly for local development
# when not using Docker. For Docker deployment, the CMD in Dockerfile will handle this.
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "genai.main:app", # Points to this file (main.py) and the app instance
        host="0.0.0.0",   # Listen on all available network interfaces
        port=settings.GENAI_PORT,
        log_level="info",
        reload=True       # Enable auto-reload for development
    )
