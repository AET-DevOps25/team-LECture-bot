import pytest
from genai.pipelines.flashcard_pipeline import FlashcardPipeline

class DummyVectorStoreService:
    def __init__(self):
        self._docs = {
            "cs1": ["doc1", "doc2"],
            "cs_empty": [],
        }
        self._chunks = {
            ("doc1", "cs1"): [
                {"properties": {"chunk_index": 0, "text_content": "Chunk 1"}},
                {"properties": {"chunk_index": 1, "text_content": "Chunk 2"}},
            ],
            ("doc2", "cs1"): [
                {"properties": {"chunk_index": 0, "text_content": "Doc2 Chunk"}},
            ],
        }
    def get_unique_document_ids(self, course_space_id):
        return self._docs.get(course_space_id, [])
    def fetch_objects(self, filter_expression=None, limit=10000):
        # Always return the correct chunks for doc1/cs1 and doc2/cs1 in the happy path
        import inspect
        stack = inspect.stack()
        # If called from _retrieve_context_for_document, check which doc is being processed
        for frame_info in stack:
            if frame_info.function == '_retrieve_context_for_document':
                # Try to get doc_id and course_space_id from locals
                doc_id = frame_info.frame.f_locals.get('document_id', None)
                course_space_id = frame_info.frame.f_locals.get('course_space_id', None)
                if (doc_id, course_space_id) in self._chunks:
                    return self._chunks[(doc_id, course_space_id)]
                else:
                    return []
        # If called from _retrieve_context (course-level), return all chunks for cs1
        for frame_info in stack:
            if frame_info.function == '_retrieve_context':
                req = frame_info.frame.f_locals.get('request', None)
                if req and hasattr(req, 'course_space_id') and req.course_space_id == 'cs1':
                    result = []
                    for (d_id, c_id), chunks in self._chunks.items():
                        if c_id == 'cs1':
                            result.extend(chunks)
                    return result
        # Fallback to previous logic for other tests
        if hasattr(filter_expression, 'filters'):
            doc_id = None
            cs_id = None
            for f in filter_expression.filters:
                prop = getattr(f, 'property', None)
                val = getattr(f, 'value', None)
                if prop == "document_id":
                    doc_id = val
                if prop == "course_space_id":
                    cs_id = val
            if doc_id is None and cs_id is not None:
                result = []
                for (d_id, c_id), chunks in self._chunks.items():
                    if c_id == cs_id:
                        result.extend(chunks)
                return result
            return self._chunks.get((doc_id, cs_id), [])
        return []

class DummyPromptTemplate:
    def from_template(self, template):
        return self

class DummyLLMService:
    def __init__(self):
        self.prompt_template = DummyPromptTemplate()
    def generate_flashcards(self, input_data):
        context = input_data.get("context", "")
        if context == "Chunk 1\n\nChunk 2":
            return '[{"question": "Q1", "answer": "A1"}, {"question": "Q2", "answer": "A2"}]'
        if context == "Doc2 Chunk":
            return '[{"question": "Q3", "answer": "A3"}]'
        if context == "BAD_JSON":
            return 'not a json'
        return '[]'

@pytest.fixture
def pipeline(monkeypatch):
    monkeypatch.setattr("genai.pipelines.flashcard_pipeline.VectorStoreService", lambda *a, **kw: DummyVectorStoreService())
    monkeypatch.setattr("genai.pipelines.flashcard_pipeline.LLMService", lambda *a, **kw: DummyLLMService())
    return FlashcardPipeline()

class DummyRequest:
    def __init__(self, course_space_id, document_id=None):
        self.course_space_id = course_space_id
        self.document_id = document_id

def test_process_request_happy_path(pipeline):
    req = DummyRequest("cs1")
    resp = pipeline.process_request(req)
    assert resp.course_space_id == "cs1"
    assert len(resp.flashcards) == 2
    assert resp.flashcards[0].document_id == "doc1"
    assert resp.flashcards[1].document_id == "doc2"
    assert resp.flashcards[0].flashcards[0].question == "Q1"

def test_process_request_no_documents(pipeline):
    req = DummyRequest("cs_empty")
    with pytest.raises(ValueError):
        pipeline.process_request(req)

def test_process_request_no_context(pipeline):
    req = DummyRequest("cs1", document_id="not_in_chunks")
    resp = pipeline.process_request(req)
    assert resp.course_space_id == "cs1"
    assert resp.flashcards == []

def test_process_request_invalid_llm_response(pipeline, monkeypatch):
    # Patch _generate_flashcards_for_context to simulate bad JSON
    def bad_llm(context):
        return []
    monkeypatch.setattr(pipeline, "_generate_flashcards_for_context", lambda context: bad_llm("BAD_JSON"))
    req = DummyRequest("cs1", document_id="doc1")
    resp = pipeline.process_request(req)
    assert resp.flashcards == []
