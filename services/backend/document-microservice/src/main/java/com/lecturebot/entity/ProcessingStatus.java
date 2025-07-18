package com.lecturebot.entity;

public enum ProcessingStatus {
    PENDING,                // just uploaded
    PROCESSING_EXTRACTION,  // extracting text from PDF
    PROCESSING_INDEXING,    // sending to Weaviate for indexing
    COMPLETED,              // successfully stored in vector database and fully processed and available for search
    FAILED                  // error at any step
}
