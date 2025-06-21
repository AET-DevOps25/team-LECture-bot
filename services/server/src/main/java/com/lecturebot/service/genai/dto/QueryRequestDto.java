package com.lecturebot.service.genai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// This DTO must match the JSON structure expected by the GenAI service's /query endpoint
public record QueryRequestDto(
    @JsonProperty("query_text") String queryText,
    @JsonProperty("course_space_id") String courseSpaceId
) {}