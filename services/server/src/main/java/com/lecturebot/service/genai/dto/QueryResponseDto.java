package com.lecturebot.service.genai.dto;

import java.util.List;

// This DTO must match the JSON structure returned by the GenAI service's /query endpoint
public record QueryResponseDto(
    String answer,
    List<String> citations
) {}