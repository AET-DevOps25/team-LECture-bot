package com.lecturebot.service.genai;

import com.lecturebot.generated.model.IndexRequest;
import com.lecturebot.generated.model.IndexResponse;
import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResponse;
import com.lecturebot.generated.model.FlashcardRequest;
import com.lecturebot.generated.model.FlashcardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Submits a document for indexing to the GenAI service.
     *
     * @param indexRequest The indexing request object.
     * @return An Optional containing the IndexResponse if successful, otherwise
     *         empty.
     */
    public Optional<IndexResponse> indexDocument(IndexRequest indexRequest) {
        String indexUrl = genAiServiceBaseUrl + "/index";
        try {
            logger.info("Sending indexing request to GenAI service for documentId: {}", indexRequest.getDocumentId());
            IndexResponse indexResponse = restTemplate.postForObject(indexUrl, indexRequest, IndexResponse.class);
            logger.info("Successfully received indexing response from GenAI service.");
            return Optional.ofNullable(indexResponse);
        } catch (RestClientException e) {
            logger.error("Error calling GenAI indexing service. URL: {}, Error: {}", indexUrl, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Submits a query to the GenAI service.
     *
     * @param queryRequest The query request object.
     * @return An Optional containing the QueryResponse if successful, otherwise
     *         empty.
     */
    public Optional<QueryResponse> submitQuery(QueryRequest queryRequest) {
        String queryUrl = genAiServiceBaseUrl + "/query";
        try {
            logger.info("Sending query to GenAI service for course space: {}", queryRequest.getCourseSpaceId());
            QueryResponse queryResponse = restTemplate.postForObject(queryUrl, queryRequest, QueryResponse.class);
            logger.info("Successfully received query response from GenAI service.");
            return Optional.ofNullable(queryResponse);
        } catch (RestClientException e) {
            logger.error("Error calling GenAI service for query. URL: {}, Error: {}", queryUrl, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<FlashcardResponse> generateFlashcards(FlashcardRequest request) {
        String url = genAiServiceBaseUrl + "/flashcards/generate";
        try {
            logger.info("Sending flashcard request to GenAI service for courseSpaceId: {}", request.getCourseSpaceId());
            FlashcardResponse response = restTemplate.postForObject(url, request, FlashcardResponse.class);
            logger.info("Successfully received flashcard response from GenAI service.");
            return Optional.ofNullable(response);
        } catch (RestClientException e) {
            logger.error("Error calling GenAI flashcard service at {}: {}", url, e.getMessage());
            return Optional.empty();
        }
    }
}
