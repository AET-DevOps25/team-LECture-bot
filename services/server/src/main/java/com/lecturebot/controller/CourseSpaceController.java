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
@RequestMapping("/coursespaces")
@RequiredArgsConstructor
public class CourseSpaceController {
    private final CourseSpaceService courseSpaceService;

    @GetMapping
    public List<CourseSpaceResponse> listCourseSpaces() {
        return courseSpaceService.getCourseSpacesForCurrentUser();
    }

    @GetMapping("/{courseSpaceId}")
    public ResponseEntity<CourseSpaceResponse> getCourseSpace(@PathVariable UUID courseSpaceId) {
        return ResponseEntity.ok(courseSpaceService.getCourseSpaceById(courseSpaceId));
    }

    @PostMapping
    public ResponseEntity<CourseSpaceResponse> createCourseSpace(@Valid @RequestBody CreateCourseSpaceRequest request) {
        return ResponseEntity.ok(courseSpaceService.createCourseSpace(request));
    }

    @PutMapping("/{courseSpaceId}")
    public ResponseEntity<CourseSpaceResponse> updateCourseSpace(@PathVariable UUID courseSpaceId, @Valid @RequestBody UpdateCourseSpaceRequest request) {
        return ResponseEntity.ok(courseSpaceService.updateCourseSpace(courseSpaceId, request));
    }

    @DeleteMapping("/{courseSpaceId}")
    public ResponseEntity<Void> deleteCourseSpace(@PathVariable UUID courseSpaceId) {
        courseSpaceService.deleteCourseSpace(courseSpaceId);
        return ResponseEntity.noContent().build();
    }
}
