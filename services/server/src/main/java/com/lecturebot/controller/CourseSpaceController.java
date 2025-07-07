package com.lecturebot.controller;

import com.lecturebot.dto.CourseSpaceResponse;
import com.lecturebot.dto.CreateCourseSpaceRequest;
import com.lecturebot.dto.UpdateCourseSpaceRequest;
import com.lecturebot.service.CourseSpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course-spaces")
@RequiredArgsConstructor
public class CourseSpaceController {
    private final CourseSpaceService courseSpaceService;

    @GetMapping
    public List<CourseSpaceResponse> listCourseSpaces() {
        return courseSpaceService.getCourseSpacesForCurrentUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseSpaceResponse> getCourseSpace(@PathVariable UUID id) {
        return ResponseEntity.ok(courseSpaceService.getCourseSpaceById(id));
    }

    @PostMapping
    public ResponseEntity<CourseSpaceResponse> createCourseSpace(@Valid @RequestBody CreateCourseSpaceRequest request) {
        return ResponseEntity.ok(courseSpaceService.createCourseSpace(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseSpaceResponse> updateCourseSpace(@PathVariable UUID id, @Valid @RequestBody UpdateCourseSpaceRequest request) {
        return ResponseEntity.ok(courseSpaceService.updateCourseSpace(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseSpace(@PathVariable UUID id) {
        courseSpaceService.deleteCourseSpace(id);
        return ResponseEntity.noContent().build();
    }
}
