import os
from dotenv import load_dotenv

load_dotenv()

os.environ["WEAVIATE_URL"] = "http://localhost:8081"

import pytest
from fastapi.testclient import TestClient
from genai.main import app

client = TestClient(app)

def test_query_endpoint_happy_path(monkeypatch):
    # Patch QAPipeline to return a dummy response
    class DummyResponse:
        answer = "Test answer"
        citations = [
            {"document_id": "doc1", "chunk_id": "1", "source_details": "Doc1 details", "retrieved_text_preview": "Preview..."}
        ]
    def dummy_process_query(self, req):
        return DummyResponse()
    monkeypatch.setattr("genai.pipelines.qa_pipeline.QAPipeline.process_query", dummy_process_query)

    payload = {"query_text": "What is X?", "course_space_id": "course123"}
    response = client.post("/api/v1/query", json=payload)
    assert response.status_code == 200
    data = response.json()
    assert data["answer"] == "Test answer"
    assert len(data["citations"]) == 1
    assert data["citations"][0]["document_id"] == "doc1"

def test_query_endpoint_invalid(monkeypatch):
    payload = {"course_space_id": "course123"}  # Missing query_text
    response = client.post("/api/v1/query", json=payload)
    assert response.status_code == 422

def test_query_endpoint_internal_error(monkeypatch):
    def raise_error(self, req):
        raise Exception("LLM error")
    monkeypatch.setattr("genai.pipelines.qa_pipeline.QAPipeline.process_query", raise_error)
    payload = {"query_text": "fail", "course_space_id": "course123"}
    response = client.post("/api/v1/query", json=payload)
    assert response.status_code == 500
    assert "error" in response.text or "detail" in response.text
