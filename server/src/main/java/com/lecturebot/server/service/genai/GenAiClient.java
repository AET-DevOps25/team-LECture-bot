package com.lecturebot.server.service.genai;

import com.lecturebot.server.service.genai.dto.IndexRequestDto;
import com.lecturebot.server.service.genai.dto.IndexResponseDto;
import com.lecturebot.server.service.genai.dto.QueryRequestDto;
import com.lecturebot.server.service.genai.dto.QueryResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class GenAiClient {

    private static final Logger logger = LoggerFactory.getLogger(GenAiClient.class);

    private final RestTemplate restTemplate;
    private final String genAiServiceBaseUrl;

    public GenAiClient(RestTemplate restTemplate,
                       @Value("${genai.service.base-url}") String genAiServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.genAiServiceBaseUrl = genAiServiceBaseUrl;
    }

    public Optional<IndexResponseDto> indexDocument(String documentId, String courseSpaceId, String textContent) {
        String url = UriComponentsBuilder.fromHttpUrl(genAiServiceBaseUrl)
                .path("/api/v1/index/") // Corrected path
                .toUriString();

        IndexRequestDto requestDto = new IndexRequestDto(documentId, courseSpaceId, textContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<IndexRequestDto> entity = new HttpEntity<>(requestDto, headers);

        try {
            logger.info("Sending indexing request to GenAI service for document_id: {}", documentId);
            ResponseEntity<IndexResponseDto> response = restTemplate.postForEntity(url, entity, IndexResponseDto.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Indexing request successful for document_id: {}. Status: {}", documentId, response.getBody().status());
                return Optional.of(response.getBody());
            }
            logger.warn("Indexing request for document_id: {} returned status: {} with body: {}",
                    documentId, response.getStatusCode(), response.getBody());
            return Optional.empty();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling GenAI indexing service for document_id: {}. Status: {}, Body: {}",
                    documentId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Unexpected error calling GenAI indexing service for document_id: {}", documentId, e);
            return Optional.empty();
        }
    }

    public Optional<QueryResponseDto> submitQuery(String queryText, String courseSpaceId) {
        String url = UriComponentsBuilder.fromHttpUrl(genAiServiceBaseUrl)
                .path("/api/v1/query/query")
                .toUriString();

        QueryRequestDto requestDto = new QueryRequestDto(queryText, courseSpaceId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QueryRequestDto> entity = new HttpEntity<>(requestDto, headers);

        try {
            logger.info("Sending query to GenAI service for course_space_id: {}", courseSpaceId);
            ResponseEntity<QueryResponseDto> response = restTemplate.postForEntity(url, entity, QueryResponseDto.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Query request successful for course_space_id: {}. Answer received.", courseSpaceId);
                return Optional.of(response.getBody());
            }
            logger.warn("Query request for course_space_id: {} returned status: {} with body: {}",
                    courseSpaceId, response.getStatusCode(), response.getBody());
            return Optional.empty();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error calling GenAI query service for course_space_id: {}. Status: {}, Body: {}",
                    courseSpaceId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Unexpected error calling GenAI query service for course_space_id: {}", courseSpaceId, e);
            return Optional.empty();
        }
    }
}
