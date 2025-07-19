package com.lecturebot.controller;

import com.lecturebot.generated.model.*;
import com.lecturebot.service.QueryOrchestrationService;
import com.lecturebot.service.genai.GenAiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenAiControllerTest {
    private GenAiClient genAiClient;
    private QueryOrchestrationService queryOrchestrationService;
    private GenAiController controller;

    @BeforeEach
    void setUp() {
        genAiClient = mock(GenAiClient.class);
        queryOrchestrationService = mock(QueryOrchestrationService.class);
        controller = new GenAiController(genAiClient, queryOrchestrationService);
    }

    @Test
    void indexDocument_success() {
        IndexRequest req = new IndexRequest();
        IndexResponse resp = new IndexResponse();
        when(genAiClient.indexDocument(req)).thenReturn(Optional.of(resp));
        ResponseEntity<IndexResponse> result = controller.indexDocument(req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody());
    }

    @Test
    void indexDocument_failure_returns500() {
        IndexRequest req = new IndexRequest();
        when(genAiClient.indexDocument(req)).thenReturn(Optional.empty());
        ResponseEntity<IndexResponse> result = controller.indexDocument(req);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void submitQuery_success() {
        QueryRequest req = new QueryRequest();
        QueryResponse resp = new QueryResponse();
        when(queryOrchestrationService.processQuery(req)).thenReturn(Optional.of(resp));
        ResponseEntity<QueryResponse> result = controller.submitQuery(req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody());
    }

    @Test
    void submitQuery_failure_returns500() {
        QueryRequest req = new QueryRequest();
        when(queryOrchestrationService.processQuery(req)).thenReturn(Optional.empty());
        ResponseEntity<QueryResponse> result = controller.submitQuery(req);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void generateFlashcards_success() {
        FlashcardRequest req = new FlashcardRequest();
        FlashcardResponse resp = new FlashcardResponse();
        when(genAiClient.generateFlashcards(req)).thenReturn(Optional.of(resp));
        ResponseEntity<FlashcardResponse> result = controller.generateFlashcards(req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody());
    }

    @Test
    void generateFlashcards_empty_returns500WithError() {
        FlashcardRequest req = new FlashcardRequest();
        when(genAiClient.generateFlashcards(req)).thenReturn(Optional.empty());
        ResponseEntity<FlashcardResponse> result = controller.generateFlashcards(req);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        assertNotNull(result.getBody().getError());
    }

    @Test
    void generateFlashcards_noDocumentsFound_returns204() {
        FlashcardRequest req = new FlashcardRequest();
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "No documents found".getBytes(), null);
        when(genAiClient.generateFlashcards(req)).thenThrow(ex);
        ResponseEntity<FlashcardResponse> result = controller.generateFlashcards(req);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void generateFlashcards_otherHttpError_returnsErrorBody() {
        FlashcardRequest req = new FlashcardRequest();
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "Some other error".getBytes(), null);
        when(genAiClient.generateFlashcards(req)).thenThrow(ex);
        ResponseEntity<FlashcardResponse> result = controller.generateFlashcards(req);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getError().contains("Some other error"));
    }

    @Test
    void deindexDocument_success_returns204() {
        when(genAiClient.deindexDocument("course", "doc")).thenReturn(true);
        ResponseEntity<Void> result = controller.deindexDocument("course", "doc");
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void deindexDocument_notFound_returns404() {
        when(genAiClient.deindexDocument("course", "doc")).thenReturn(false);
        ResponseEntity<Void> result = controller.deindexDocument("course", "doc");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void deindexDocument_exception_returns500() {
        when(genAiClient.deindexDocument("course", "doc")).thenThrow(new RuntimeException("fail"));
        ResponseEntity<Void> result = controller.deindexDocument("course", "doc");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
}
