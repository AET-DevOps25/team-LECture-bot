package com.lecturebot.controller;

import com.lecturebot.generated.model.FlashcardRequest;
import com.lecturebot.generated.model.FlashcardResponse;
import com.lecturebot.generated.model.IndexRequest;
import com.lecturebot.generated.model.IndexResponse;
import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResponse;
import com.lecturebot.service.genai.GenAiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenAiTestControllerTest {

    @Mock
    private GenAiClient genAiClient;

    private GenAiTestController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new GenAiTestController(genAiClient);
    }

    @Test
    void testIndexDocument_success() {
        IndexRequest request = mock(IndexRequest.class);
        IndexResponse responseObj = mock(IndexResponse.class);
        when(genAiClient.indexDocument(request)).thenReturn(Optional.of(responseObj));

        ResponseEntity<?> response = controller.testIndexDocument(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseObj, response.getBody());
    }

    @Test
    void testIndexDocument_failure() {
        IndexRequest request = mock(IndexRequest.class);
        when(genAiClient.indexDocument(request)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.testIndexDocument(request);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Failed to index document via GenAI service", response.getBody());
    }

    @Test
    void testSubmitQuery_success() {
        QueryRequest request = mock(QueryRequest.class);
        QueryResponse responseObj = mock(QueryResponse.class);
        when(genAiClient.submitQuery(request)).thenReturn(Optional.of(responseObj));

        ResponseEntity<?> response = controller.testSubmitQuery(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseObj, response.getBody());
    }

    @Test
    void testSubmitQuery_failure() {
        QueryRequest request = mock(QueryRequest.class);
        when(genAiClient.submitQuery(request)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.testSubmitQuery(request);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Failed to get query response from GenAI service", response.getBody());
    }

    @Test
    void testGenerateFlashcards_success() {
        FlashcardRequest request = mock(FlashcardRequest.class);
        FlashcardResponse responseObj = mock(FlashcardResponse.class);
        when(genAiClient.generateFlashcards(request)).thenReturn(Optional.of(responseObj));

        ResponseEntity<?> response = controller.testGenerateFlashcards(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseObj, response.getBody());
    }

    @Test
    void testGenerateFlashcards_failure() {
        FlashcardRequest request = mock(FlashcardRequest.class);
        when(genAiClient.generateFlashcards(request)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.testGenerateFlashcards(request);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Failed to generate flashcards via GenAI service", response.getBody());
    }
}
