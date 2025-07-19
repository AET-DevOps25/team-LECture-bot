package com.lecturebot.service;

import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResponse;
import com.lecturebot.service.genai.GenAiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueryOrchestrationServiceTest {

    @Mock
    private GenAiClient genAiClient;

    private QueryOrchestrationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new QueryOrchestrationService(genAiClient);
    }

    @Test
    void processQuery_validRequest_returnsResponse() {
        QueryRequest request = mock(QueryRequest.class);
        QueryResponse responseObj = mock(QueryResponse.class);
        when(request.getQueryText()).thenReturn("What is AI?");
        when(genAiClient.submitQuery(request)).thenReturn(Optional.of(responseObj));

        Optional<QueryResponse> result = service.processQuery(request);
        assertTrue(result.isPresent());
        assertEquals(responseObj, result.get());
    }

    @Test
    void processQuery_nullRequest_returnsEmpty() {
        Optional<QueryResponse> result = service.processQuery(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void processQuery_nullQueryText_returnsEmpty() {
        QueryRequest request = mock(QueryRequest.class);
        when(request.getQueryText()).thenReturn(null);
        Optional<QueryResponse> result = service.processQuery(request);
        assertTrue(result.isEmpty());
    }

    @Test
    void processQuery_blankQueryText_returnsEmpty() {
        QueryRequest request = mock(QueryRequest.class);
        when(request.getQueryText()).thenReturn("");
        Optional<QueryResponse> result = service.processQuery(request);
        assertTrue(result.isEmpty());
    }

    @Test
    void processQuery_validRequest_clientReturnsEmpty() {
        QueryRequest request = mock(QueryRequest.class);
        when(request.getQueryText()).thenReturn("What is AI?");
        when(genAiClient.submitQuery(request)).thenReturn(Optional.empty());
        Optional<QueryResponse> result = service.processQuery(request);
        assertTrue(result.isEmpty());
    }
}
