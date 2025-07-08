package com.lecturebot.documentservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Data;

@Entity
@Data
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    private Instant uploadDate;

    @Lob
    private String extractedText; // or use a String path if storing externally

    private Long courseId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus;

    // getters and setters
}
