package com.lecturebot.service.genai;

import com.lecturebot.service.genai.dto.IndexRequestDto;
import com.lecturebot.service.genai.dto.IndexResponseDto;
import com.lecturebot.service.genai.dto.QueryRequestDto;
import com.lecturebot.service.genai.dto.QueryResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class GenAiClient {

    private static final Logger logger = LoggerFactory.getLogger(GenAiClient.class);
    private final RestTemplate restTemplate;
    private final String genAiServiceBaseUrl;

    public GenAiClient(RestTemplate restTemplate, @Value("${GENAI_SERVICE_BASE_URL}") String genAiServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.genAiServiceBaseUrl = genAiServiceBaseUrl;
    }

    public Optional<IndexResponseDto> indexDocument(String documentId, String courseSpaceId, String textContent) {
        String url = genAiServiceBaseUrl + "/api/v1/index/index";
        try {
            IndexRequestDto requestDto = new IndexRequestDto(documentId, courseSpaceId, textContent);
            logger.info("Sending indexing request to GenAI service for documentId: {}", documentId);
            IndexResponseDto response = restTemplate.postForObject(url, requestDto, IndexResponseDto.class);
            logger.info("Successfully received indexing response from GenAI service.");
            return Optional.ofNullable(response);
        } catch (RestClientException e) {
            logger.error("Error calling GenAI indexing service at {}: {}", url, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<QueryResponseDto> submitQuery(String queryText, String courseSpaceId) {
        String url = genAiServiceBaseUrl + "/api/v1/query/query";
        try {
            QueryRequestDto requestDto = new QueryRequestDto(queryText, courseSpaceId);
            logger.info("Sending query to GenAI service for courseSpaceId: {}", courseSpaceId);
            QueryResponseDto response = restTemplate.postForObject(url, requestDto, QueryResponseDto.class);
            logger.info("Successfully received query response from GenAI service.");
            return Optional.ofNullable(response);
        } catch (RestClientException e) {
            logger.error("Error calling GenAI query service at {}: {}", url, e.getMessage());
            return Optional.empty();
        }
    }
}