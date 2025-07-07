package com.lecturebot.controller;

import com.lecturebot.generated.api.CourseSpacesApi;
import com.lecturebot.generated.model.CourseSpaceDto;
import com.lecturebot.generated.model.CreateCourseSpaceRequest;
import com.lecturebot.generated.model.CourseSpaceDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.lecturebot.service.CourseSpaceService;

import com.lecturebot.mapper.CourseSpaceMapper;
import com.lecturebot.entity.CourseSpace;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@RestController
public class CourseSpacesController implements CourseSpacesApi {

    private final CourseSpaceService courseSpaceService;
    private final CourseSpaceMapper courseSpaceMapper;
    private static final Logger logger = LoggerFactory.getLogger(CourseSpacesController.class);

    public CourseSpacesController(CourseSpaceService courseSpaceService, CourseSpaceMapper courseSpaceMapper) {
        this.courseSpaceService = courseSpaceService;
        this.courseSpaceMapper = courseSpaceMapper;
    }

    @Override
    public ResponseEntity<List<CourseSpaceDto>> getCourseSpaces() {
        // Get entities, map to OpenAPI DTOs
        List<CourseSpace> courseSpaces = courseSpaceService.getCourseSpacesForCurrentUserEntities();
        return ResponseEntity.ok(courseSpaceMapper.toDtoList(courseSpaces));

    }

    @Override
    public ResponseEntity<CourseSpaceDto> createCourseSpace(CreateCourseSpaceRequest createCourseSpaceRequest) {
        // Use name from OpenAPI model
        try {
            CourseSpace cs = courseSpaceService.createCourseSpaceFromOpenApi(createCourseSpaceRequest);
            CourseSpaceDto csDto = courseSpaceMapper.toDto(cs);
            return ResponseEntity.ok(csDto);
        } catch (Exception e) {
            logger.error("Error", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }

    @Override
    public ResponseEntity<Void> deleteCourseSpace(UUID courseSpaceId) {
        try {
            courseSpaceService.deleteCourseSpace(courseSpaceId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }
}
