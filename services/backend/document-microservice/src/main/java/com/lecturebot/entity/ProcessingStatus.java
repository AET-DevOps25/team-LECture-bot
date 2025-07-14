package com.lecturebot.entity;

public enum ProcessingStatus {
    PENDING,      // uploaded, waiting for processing
    PROCESSING,   // being processed
    COMPLETED,    // done
    FAILED        // error
}
