package com.lecturebot.server.service.genai.dto;

import java.util.List;

public record IndexResponseDto(
    String status,
    String message,
    String document_id,
    Integer total_chunks_generated,
    Integer chunks_stored_successfully,
    List<String> failed_chunk_indexes
) {}
