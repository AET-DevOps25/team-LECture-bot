package com.lecturebot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lecturebot.generated.model.FlashcardRequest;
import com.lecturebot.generated.model.FlashcardResponse;
import com.lecturebot.service.genai.GenAiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// We specify the controller to test.
// We also mock the dependencies that are not part of the web layer, like services.
@WebMvcTest(FlashcardController.class)
public class FlashcardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GenAiClient genAiClient;

    // We also need to mock the JwtService because Spring Security is active

    @Test
    @WithMockUser // This annotation provides a mock authenticated user
    public void generateFlashcards_shouldReturnFlashcards_whenRequestIsValid() throws Exception {
        // Arrange
        FlashcardRequest request = new FlashcardRequest();
        request.setCorseSpaceId("cs-101"); // Corrected method call

        FlashcardResponse mockResponse = new FlashcardResponse();
        mockResponse.courseSpaceId("cs-101");

        when(genAiClient.getFlashcards(any(FlashcardRequest.class))).thenReturn(Optional.of(mockResponse));

        // Act & Assert
        mockMvc.perform(post("/api/v1/genai/generate-flashcards")
                .with(csrf()) // Add CSRF token for POST requests in tests
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course_space_id").value("cs-101"));
    }

    @Test
    @WithMockUser
    public void generateFlashcards_shouldReturn500_whenGenAiClientFails() throws Exception {
        // Arrange
        FlashcardRequest request = new FlashcardRequest();
        request.setCorseSpaceId("cs-101"); // Corrected method call

        when(genAiClient.getFlashcards(any(FlashcardRequest.class))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/v1/genai/generate-flashcards")
                .with(csrf()) // Add CSRF token
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
