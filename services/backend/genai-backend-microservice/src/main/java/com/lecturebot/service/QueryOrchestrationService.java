package com.lecturebot.service;

import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResponse;
import com.lecturebot.service.genai.GenAiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QueryOrchestrationService {

    private static final Logger logger = LoggerFactory.getLogger(QueryOrchestrationService.class);

    private final GenAiClient genAiClient;

    public QueryOrchestrationService(GenAiClient genAiClient) {
        this.genAiClient = genAiClient;
    }

    public Optional<QueryResponse> processQuery(QueryRequest queryRequest) {
        if (queryRequest == null || queryRequest.getQueryText() == null || queryRequest.getQueryText().isBlank()) {
            logger.warn("Received a query request with no text.");
            return Optional.empty();
        }

        logger.info("Processing query for course space: {}", queryRequest.getCourseSpaceId());

        return genAiClient.submitQuery(queryRequest);
    }
}