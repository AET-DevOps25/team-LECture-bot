package com.lecturebot;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.lecturebot.generated.model.FlashcardRequest;
import com.lecturebot.generated.model.FlashcardResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort; // Corrected import
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Add this annotation to disable the datasource for this test
public class GenAiIntegrationTest {
    // Define a mock DataSource to prevent the real one from being created.
    @LocalServerPort // Corrected annotation
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static WireMockServer wireMockServer;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // This is intentionally left empty. Properties are set in setup().
        registry.add("GENAI_SERVICE_BASE_URL", wireMockServer::baseUrl);

    }

    @BeforeEach
    static void setup() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        System.setProperty("GENAI_SERVICE_BASE_URL", wireMockServer.baseUrl());
    }

    @AfterEach
    static void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void flashcardsEndpoint_shouldReturnSuccess_whenGenAiServiceResponds() {
        // Arrange: Stub the mock genai-service
        stubFor(post(urlEqualTo("/api/v1/flashcard/generate-flashcards"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"course_space_id\":\"cs-101\",\"results\":[]}")));

        // Act: Call our actual server application's endpoint
        String url = "http://localhost:" + port + "/api/v1/genai/flashcards";
        FlashcardRequest requestBody = new FlashcardRequest();
        requestBody.setCorseSpaceId("cs-101"); // Corrected method call
        HttpEntity<FlashcardRequest> entity = new HttpEntity<>(requestBody, new HttpHeaders());

        ResponseEntity<FlashcardResponse> response = this.restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                FlashcardResponse.class);

        // Assert: Verify that our server correctly handled the mock response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCourseSpaceId()).isEqualTo("cs-101");

        // Optionally, verify that the mock service was called as expected
        verify(postRequestedFor(urlEqualTo("/api/v1/flashcard/generate-flashcards")));
    }
}
