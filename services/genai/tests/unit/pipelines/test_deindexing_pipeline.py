import pytest
from genai.pipelines.deindexing_pipeline import DeindexingPipeline

class DummyVectorStoreService:
    def delete_document(self, document_id, course_space_id=None):
        if document_id == "FAIL":
            raise Exception("Delete error")
        if document_id == "NOT_FOUND":
            return {"status": "not_found", "deleted": 0}
        return {"status": "success", "deleted": 3}

@pytest.fixture
def pipeline(monkeypatch):
    monkeypatch.setattr("genai.pipelines.deindexing_pipeline.VectorStoreService", lambda *a, **kw: DummyVectorStoreService())
    return DeindexingPipeline()

def test_deindex_document_success(pipeline):
    result = pipeline.deindex_document("doc1", "cs1")
    assert result["status"] == "success"
    assert result["deleted"] == 3

def test_deindex_document_not_found(pipeline):
    result = pipeline.deindex_document("NOT_FOUND", "cs2")
    assert result["status"] == "not_found"
    assert result["deleted"] == 0

def test_deindex_document_error(pipeline):
    with pytest.raises(Exception) as exc:
        pipeline.deindex_document("FAIL", "cs3")
    assert "Delete error" in str(exc.value)
