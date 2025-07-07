package com.lecturebot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSpaceResponse {
    private UUID id;
    private String title;
    private String description;
    private Long ownerId;
    private Instant createdAt;
    private Instant updatedAt;
}
