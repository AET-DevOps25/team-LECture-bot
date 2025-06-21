package com.lecturebot.server.service.genai.dto;

public record QueryRequestDto(
    String query_text,
    String course_space_id
) {}
