package com.lecturebot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    @GetMapping
    public List<Map<String, Object>> getCourses() {
        // Return a mock list for now
        return List.of(
            Map.of("id", "course-1", "name", "Introduction to AI", "description", "Learn the basics of AI."),
            Map.of("id", "course-2", "name", "Advanced Machine Learning", "description", "Deep dive into ML algorithms.")
        );
    }
}
