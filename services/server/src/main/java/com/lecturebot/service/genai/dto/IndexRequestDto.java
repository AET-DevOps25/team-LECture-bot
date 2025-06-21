package com.lecturebot.service.genai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// This DTO must match the JSON structure expected by the GenAI service's /index endpoint
public record IndexRequestDto(
    @JsonProperty("document_id") String documentId,
    @JsonProperty("course_space_id") String courseSpaceId,
    @JsonProperty("text_content") String textContent
) {}