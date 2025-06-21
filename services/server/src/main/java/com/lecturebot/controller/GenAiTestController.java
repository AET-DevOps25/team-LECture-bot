package com.lecturebot.controller;

import com.lecturebot.service.genai.GenAiClient;
import com.lecturebot.service.genai.dto.IndexResponseDto;
import com.lecturebot.service.genai.dto.QueryResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/test/genai")
public class GenAiTestController {

    private final GenAiClient genAiClient;

    public GenAiTestController(GenAiClient genAiClient) {
        this.genAiClient = genAiClient;
    }

    // Test DTO for indexing from the server's perspective
    public record ServerIndexTestRequest(String documentId, String courseSpaceId, String textContent) {}
    // Test DTO for querying from the server's perspective
    public record ServerQueryTestRequest(String queryText, String courseSpaceId) {}


    @PostMapping("/index")
    public ResponseEntity<?> testIndexDocument(@RequestBody ServerIndexTestRequest request) {
        Optional<IndexResponseDto> response = genAiClient.indexDocument(
                request.documentId(),
                request.courseSpaceId(),
                request.textContent()
        );
        return response.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(500).body("Failed to index document via GenAI service"));
    }

    @PostMapping("/query")
    public ResponseEntity<?> testSubmitQuery(@RequestBody ServerQueryTestRequest request) {
        Optional<QueryResponseDto> response = genAiClient.submitQuery(
                request.queryText(),
                request.courseSpaceId()
        );
        return response.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(500).body("Failed to get query response from GenAI service"));
    }
}
