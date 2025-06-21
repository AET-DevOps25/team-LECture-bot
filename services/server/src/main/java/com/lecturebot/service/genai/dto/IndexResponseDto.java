package com.lecturebot.service.genai.dto;

// This DTO must match the JSON structure returned by the GenAI service's /index endpoint
public record IndexResponseDto(
    String message,
    String documentId
) {}