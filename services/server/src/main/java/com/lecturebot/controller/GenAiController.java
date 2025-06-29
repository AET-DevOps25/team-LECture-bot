package com.lecturebot.controller;

import com.lecturebot.generated.api.GenAiApi;
import com.lecturebot.generated.model.IndexRequest;
import com.lecturebot.generated.model.IndexResponse;
import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResponse;
import com.lecturebot.service.QueryOrchestrationService;
import com.lecturebot.service.genai.GenAiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
}