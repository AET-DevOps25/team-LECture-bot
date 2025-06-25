package com.lecturebot.controller;

import com.lecturebot.service.genai.GenAiClient;
import com.lecturebot.generated.model.IndexRequest;
import com.lecturebot.generated.model.IndexResponse;
import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/test/genai") // The /api/v1 prefix is handled globally by server.servlet.context-path
public class GenAiTestController {

    private final GenAiClient genAiClient;

    public GenAiTestController(GenAiClient genAiClient) {
        this.genAiClient = genAiClient;
    }

    @PostMapping("/index")
    public ResponseEntity<?> testIndexDocument(@RequestBody IndexRequest request) {
        Optional<IndexResponse> response = genAiClient.indexDocument(request);
        return response.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(500).body("Failed to index document via GenAI service"));
    }

    @PostMapping("/query")
    public ResponseEntity<?> testSubmitQuery(@RequestBody QueryRequest request) {
        Optional<QueryResponse> response = genAiClient.submitQuery(request);
        return response.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(500).body("Failed to get query response from GenAI service"));
    }
}
