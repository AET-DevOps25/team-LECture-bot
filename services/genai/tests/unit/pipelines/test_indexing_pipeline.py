import pytest
from genai.pipelines.indexing_pipeline import IndexingPipeline

class DummyTextProcessingService:
    def chunk_text(self, text, metadata):
        if text == "NO_CHUNKS":
            return []
        if text == "EMBED_FAIL":
            return [{"page_content": "EMBED_FAIL", "metadata": {"chunk_index": 0, **metadata}}]
        return [
            {"page_content": "chunk1", "metadata": {"chunk_index": 0, **metadata}},
            {"page_content": "chunk2", "metadata": {"chunk_index": 1, **metadata}},
        ]

class DummyEmbeddingService:
    def embed_texts(self, texts):
        if "EMBED_FAIL" in texts:
            raise Exception("Embedding error")
        if texts == ["chunk1", "chunk2"]:
            return [[0.1, 0.2], [0.3, 0.4]]
        return [[] for _ in texts]  # Simulate no embeddings

class DummyVectorStoreService:
    def add_documents(self, docs):
        if docs and docs[0] == "FAIL":
            raise Exception("Storage error")
        return {"added": len(docs), "errors": 0}

@pytest.fixture
def pipeline(monkeypatch):
    # Patch the class before instantiation!
    monkeypatch.setattr("genai.pipelines.indexing_pipeline.VectorStoreService", lambda *a, **kw: DummyVectorStoreService())
    pipeline = IndexingPipeline()
    pipeline.text_processor = DummyTextProcessingService()
    pipeline.embedder = DummyEmbeddingService()
    return pipeline

class DummyPayload:
    def __init__(self, document_id, text_content, course_space_id):
        self.document_id = document_id
        self.text_content = text_content
        self.course_space_id = course_space_id

def test_process_document_happy_path(pipeline):
    payload = DummyPayload("doc1", "Some text", "cs1")
    result = pipeline.process_document(payload)
    assert result["status"] == "completed"
    assert result["chunks_processed"] == 2

def test_process_document_no_text(pipeline):
    payload = DummyPayload("doc2", "   ", "cs2")
    result = pipeline.process_document(payload)
    assert result["status"] == "skipped"
    assert result["chunks_processed"] == 0

def test_process_document_no_chunks(pipeline):
    payload = DummyPayload("doc3", "NO_CHUNKS", "cs3")
    result = pipeline.process_document(payload)
    assert result["status"] == "completed_no_chunks"
    assert result["chunks_processed"] == 0

def test_process_document_embedding_fail(pipeline):
    payload = DummyPayload("doc4", "EMBED_FAIL", "cs4")
    result = pipeline.process_document(payload)
    assert result["status"] == "failed"
    assert "embedding" in result["message"]

def test_process_document_storage_fail(monkeypatch, pipeline):
    class FailingVectorStore:
        def add_documents(self, docs):
            raise Exception("Storage error")
    pipeline.vector_store = FailingVectorStore()
    payload = DummyPayload("doc5", "Some text", "cs5")
    result = pipeline.process_document(payload)
    assert result["status"] == "failed"
    assert "Weaviate storage" in result["message"]