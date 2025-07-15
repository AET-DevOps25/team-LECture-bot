package com.lecturebot.controller;

import com.lecturebot.generated.api.GenAiApi;
import com.lecturebot.generated.model.IndexRequest;
import com.lecturebot.generated.model.IndexResponse;
import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResponse;
import com.lecturebot.service.QueryOrchestrationService;
import com.lecturebot.service.genai.GenAiClient;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.lecturebot.generated.model.FlashcardRequest;
import com.lecturebot.generated.model.FlashcardResponse;

@RestController
public class GenAiController implements GenAiApi {

    private final GenAiClient genAiClient;
    private final QueryOrchestrationService queryOrchestrationService;

    public GenAiController(GenAiClient genAiClient, QueryOrchestrationService queryOrchestrationService) {
        this.genAiClient = genAiClient;
        this.queryOrchestrationService = queryOrchestrationService;
    }

    @Override
    public ResponseEntity<IndexResponse> indexDocument(IndexRequest indexRequest) {
        // This is a direct pass-through for testing, as per the original setup.
        // A real implementation would go through an orchestration service.
        return genAiClient.indexDocument(indexRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(500).body(null));
    }

    @Override
    public ResponseEntity<QueryResponse> submitQuery(QueryRequest queryRequest) {
        return queryOrchestrationService.processQuery(queryRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(500).body(null));
    }

    @Override
    public ResponseEntity<FlashcardResponse> generateFlashcards(@RequestBody FlashcardRequest flashcardRequest) {
        try {
            // Attempt to get the response from the client
            Optional<FlashcardResponse> response = genAiClient.generateFlashcards(flashcardRequest);

            return response.map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        // If the optional is empty, return an internal error
                        FlashcardResponse errorResponse = new FlashcardResponse();
                        errorResponse.setError("An unexpected internal error occurred.");
                        errorResponse.setCourseSpaceId(flashcardRequest.getCourseSpaceId());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                    });

        } catch (HttpClientErrorException e) {
            // Check for the specific "No documents found" case
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST
                    && e.getResponseBodyAsString().contains("No documents found")) {
                // Return 204 No Content for this specific case
                return ResponseEntity.noContent().build();
            }

            // For any other error, create a response body with the error message
            FlashcardResponse errorResponse = new FlashcardResponse();
            errorResponse.setCourseSpaceId(flashcardRequest.getCourseSpaceId());
            errorResponse.setError("Failed to generate flashcards: " + e.getResponseBodyAsString());

            // Return the original status code from the exception with the error body
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        }
    }
}
