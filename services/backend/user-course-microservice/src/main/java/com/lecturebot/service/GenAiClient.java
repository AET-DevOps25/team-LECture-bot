package com.lecturebot.service;

import com.lecturebot.generated.model.CitationDto;
import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

@Component
public class GenAiClient {
    @Value("${GENAI_SERVICE_BASE_URL:http://localhost:8011/api/v1/query}")
    private String genaiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public QueryResultDto submitQuery(QueryRequest request) {
        // Build request body for GenAI service
        // Map Java DTO to Python API schema

        Map<String, String> body = new HashMap<>();
        body.put("query_text", request.getQueryText());
        body.put("course_space_id", request.getCourseId() != null ? request.getCourseId().toString() : null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                genaiServiceUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        QueryResultDto result = new QueryResultDto();
        if (responseBody != null) {
            result.setAnswerText((String) responseBody.getOrDefault("answer", ""));
            List<Map<String, Object>> citationsRaw = (List<Map<String, Object>>) responseBody.get("citations");
            List<CitationDto> citations = new ArrayList<>();
            if (citationsRaw != null) {
                for (Map<String, Object> c : citationsRaw) {
                    CitationDto citation = new CitationDto();
                    // Try to parse UUID, fallback to null
                    try {
                        citation.setDocumentId(UUID.fromString((String) c.get("document_id")));
                    } catch (Exception e) {
                        citation.setDocumentId(null);
                    }
                    citation.setDocumentName(null); // Not provided by GenAI, can be extended
                    citation.setPageNumber(null); // Not provided by GenAI, can be extended
                    citation.setContextSnippet((String) c.getOrDefault("retrieved_text_preview", ""));
                    citations.add(citation);
                }
            }
            result.setCitations(citations);
            result.setError(null); // Extend if GenAI returns error_message
        }
        return result;
    }
}
