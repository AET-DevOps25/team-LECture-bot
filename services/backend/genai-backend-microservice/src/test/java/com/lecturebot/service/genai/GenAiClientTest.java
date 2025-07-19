package com.lecturebot.service.genai;

import com.lecturebot.generated.model.FlashcardRequest;
import com.lecturebot.generated.model.FlashcardResponse;
import com.lecturebot.generated.model.IndexRequest;
import com.lecturebot.generated.model.IndexResponse;
import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenAiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private GenAiClient client;

    private final String baseUrl = "http://genai-service";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        client = new GenAiClient(restTemplate, baseUrl);
    }

    @Test
    void indexDocument_success() {
        IndexRequest request = mock(IndexRequest.class);
        IndexResponse responseObj = mock(IndexResponse.class);
        when(request.getDocumentId()).thenReturn("doc1");
        when(restTemplate.postForObject(eq(baseUrl + "/api/v1/index"), eq(request), eq(IndexResponse.class))).thenReturn(responseObj);
        Optional<IndexResponse> result = client.indexDocument(request);
        assertTrue(result.isPresent());
        assertEquals(responseObj, result.get());
    }

    @Test
    void indexDocument_failure() {
        IndexRequest request = mock(IndexRequest.class);
        when(request.getDocumentId()).thenReturn("doc1");
        when(restTemplate.postForObject(anyString(), any(), eq(IndexResponse.class))).thenThrow(new RestClientException("fail"));
        Optional<IndexResponse> result = client.indexDocument(request);
        assertTrue(result.isEmpty());
    }

    @Test
    void submitQuery_success() {
        QueryRequest request = mock(QueryRequest.class);
        QueryResponse responseObj = mock(QueryResponse.class);
        when(request.getCourseSpaceId()).thenReturn("cs1");
        when(restTemplate.postForObject(eq(baseUrl + "/api/v1/query"), eq(request), eq(QueryResponse.class))).thenReturn(responseObj);
        Optional<QueryResponse> result = client.submitQuery(request);
        assertTrue(result.isPresent());
        assertEquals(responseObj, result.get());
    }

    @Test
    void submitQuery_failure() {
        QueryRequest request = mock(QueryRequest.class);
        when(request.getCourseSpaceId()).thenReturn("cs1");
        when(restTemplate.postForObject(anyString(), any(), eq(QueryResponse.class))).thenThrow(new RestClientException("fail"));
        Optional<QueryResponse> result = client.submitQuery(request);
        assertTrue(result.isEmpty());
    }

    @Test
    void generateFlashcards_success() {
        FlashcardRequest request = mock(FlashcardRequest.class);
        FlashcardResponse responseObj = mock(FlashcardResponse.class);
        when(request.getCourseSpaceId()).thenReturn("cs1");
        when(restTemplate.postForObject(eq(baseUrl + "/api/v1/flashcards/generate"), eq(request), eq(FlashcardResponse.class))).thenReturn(responseObj);
        Optional<FlashcardResponse> result = client.generateFlashcards(request);
        assertTrue(result.isPresent());
        assertEquals(responseObj, result.get());
    }

    @Test
    void generateFlashcards_failure() {
        FlashcardRequest request = mock(FlashcardRequest.class);
        when(request.getCourseSpaceId()).thenReturn("cs1");
        when(restTemplate.postForObject(anyString(), any(), eq(FlashcardResponse.class))).thenThrow(new RestClientException("fail"));
        Optional<FlashcardResponse> result = client.generateFlashcards(request);
        assertTrue(result.isEmpty());
    }

    @Test
    void deindexDocument_success() {
        doNothing().when(restTemplate).delete(baseUrl + "/api/v1/index/cs1/doc1");
        boolean result = client.deindexDocument("cs1", "doc1");
        assertTrue(result);
    }

    @Test
    void deindexDocument_failure() {
        doThrow(new RestClientException("fail")).when(restTemplate).delete(anyString());
        boolean result = client.deindexDocument("cs1", "doc1");
        assertFalse(result);
    }
}
