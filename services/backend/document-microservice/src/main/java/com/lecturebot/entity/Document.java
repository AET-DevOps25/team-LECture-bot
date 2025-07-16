package com.lecturebot.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Entity
@Data
@Table(name = "document", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"filename", "course_id"}))
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_type")
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column(name = "upload_date")
    private Instant uploadDate;

    @Column(name = "extracted_text")
    @Lob
    private String extractedText;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "upload_status")
    @Enumerated(EnumType.STRING)
    private ProcessingStatus uploadStatus;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // getters and setters
}
