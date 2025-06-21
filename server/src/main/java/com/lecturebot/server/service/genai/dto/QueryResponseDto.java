package com.lecturebot.server.service.genai.dto;

import java.util.List;
import java.util.Map;

public record QueryResponseDto(
    String answer,
    List<Map<String, Object>> citations
) {}
