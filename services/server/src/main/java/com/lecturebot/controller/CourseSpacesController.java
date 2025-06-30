package com.lecturebot.controller;

import com.lecturebot.generated.api.CourseSpacesApi;
import com.lecturebot.generated.model.CourseSpace;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CourseSpacesController implements CourseSpacesApi {

    // In a real implementation, you would inject a CourseSpaceService here.
    // For now, we return mock data to get the frontend working.

    @Override
    public ResponseEntity<List<CourseSpace>> getCourseSpaces() {
        // Mock data for demonstration purposes
        CourseSpace cs1 = new CourseSpace().id(UUID.randomUUID()).name("Introduction to AI");
        CourseSpace cs2 = new CourseSpace().id(UUID.randomUUID()).name("Software Engineering");

        return ResponseEntity.ok(List.of(cs1, cs2));
    }
}