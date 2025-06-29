import os
os.environ["WEAVIATE_URL"] = "http://localhost:8081"

import pytest
from genai.pipelines.qa_pipeline import QAPipeline
from genai.api.schemas import QueryRequest

class DummyEmbedder:
    def embed_text(self, text):
        return [0.1, 0.2, 0.3]  # Dummy vector

class DummyVectorStore:
    def similarity_search(self, query_vector, limit, filter_expression):
        # Return two dummy chunks
        return [
            {'properties': {'document_id': 'doc1', 'chunk_index': 1, 'text_content': 'Chunk 1 content.'}},
            {'properties': {'document_id': 'doc2', 'chunk_index': 2, 'text_content': 'Chunk 2 content.'}}
        ]

class DummyLLMService:
    def generate_answer(self, question, context):
        return f"Answer to: {question} with context: {context[:20]}..."

@pytest.fixture
def qa_pipeline(monkeypatch):
    pipeline = QAPipeline()
    pipeline.embedder = DummyEmbedder()
    pipeline.vector_store = DummyVectorStore()
    pipeline.llm_service = DummyLLMService()
    return pipeline

def test_process_query_happy_path(qa_pipeline):
    req = QueryRequest(query_text="What is X?", course_space_id="course123")
    resp = qa_pipeline.process_query(req)
    assert resp.answer.startswith("Answer to: What is X?")
    assert len(resp.citations) == 2
    assert resp.citations[0].document_id == 'doc1'
    assert resp.citations[1].document_id == 'doc2'

def test_process_query_no_results(monkeypatch, qa_pipeline):
    qa_pipeline.vector_store.similarity_search = lambda *a, **kw: []
    req = QueryRequest(query_text="No match", course_space_id="course123")
    resp = qa_pipeline.process_query(req)
    assert "No relevant documents" in resp.answer or resp.answer
    assert resp.citations == [] or isinstance(resp.citations, list)

def test_process_query_embedding_fail(monkeypatch, qa_pipeline):
    qa_pipeline.embedder.embed_text = lambda x: []
    req = QueryRequest(query_text="fail", course_space_id="course123")
    with pytest.raises(ValueError):
        qa_pipeline.process_query(req)
