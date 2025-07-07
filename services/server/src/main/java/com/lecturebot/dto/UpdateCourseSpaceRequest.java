package com.lecturebot.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
public class UpdateCourseSpaceRequest {
    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String description;
}
