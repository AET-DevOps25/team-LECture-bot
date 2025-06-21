package com.lecturebot.server.service.genai.dto;

public record IndexRequestDto(
    String document_id,
    String course_space_id,
    String text_content
) {}
