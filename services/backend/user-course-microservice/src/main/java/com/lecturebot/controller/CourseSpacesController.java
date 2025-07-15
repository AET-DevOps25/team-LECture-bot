
package com.lecturebot.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lecturebot.generated.model.UpdateCourseSpaceRequest;
import com.lecturebot.generated.api.CourseSpacesApi;
import com.lecturebot.generated.model.CourseSpaceDto;
import com.lecturebot.generated.model.CreateCourseSpaceRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.lecturebot.service.CourseSpaceService;

import com.lecturebot.mapper.CourseSpaceMapper;
import com.lecturebot.entity.CourseSpace;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@RestController
public class CourseSpacesController implements CourseSpacesApi {
    @Override
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/coursespaces/{courseSpaceId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public ResponseEntity<CourseSpaceDto> updateCourseSpace(
            @PathVariable("courseSpaceId") UUID courseSpaceId,
            @RequestBody UpdateCourseSpaceRequest updateCourseSpaceRequest) {
        var updated = courseSpaceService.updateCourseSpace(courseSpaceId, updateCourseSpaceRequest);
        if (updated.isPresent()) {
            return ResponseEntity.ok(courseSpaceMapper.toDto(updated.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private final CourseSpaceService courseSpaceService;
    private final CourseSpaceMapper courseSpaceMapper;
    private static final Logger logger = LoggerFactory.getLogger(CourseSpacesController.class);

    public CourseSpacesController(CourseSpaceService courseSpaceService, CourseSpaceMapper courseSpaceMapper) {
        this.courseSpaceService = courseSpaceService;
        this.courseSpaceMapper = courseSpaceMapper;
    }

    @Override
    public ResponseEntity<List<CourseSpaceDto>> getCourseSpaces() {
        List<CourseSpace> courseSpaces = courseSpaceService.getCourseSpacesForCurrentUser();
        return ResponseEntity.ok(courseSpaceMapper.toDtoList(courseSpaces));

    }

    @Override
    public ResponseEntity<CourseSpaceDto> createCourseSpace(CreateCourseSpaceRequest createCourseSpaceRequest) {
        if (courseSpaceService.courseSpaceExists(createCourseSpaceRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            CourseSpace cs = courseSpaceService.createCourseSpace(createCourseSpaceRequest);
            CourseSpaceDto csDto = courseSpaceMapper.toDto(cs);
            return ResponseEntity.ok(csDto);
        } catch (Exception e) {
            logger.error("Error", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }

    @Override
    public ResponseEntity<Void> deleteCourseSpace(UUID courseSpaceId) {
        if (courseSpaceService.deleteCourseSpace(courseSpaceId)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.internalServerError().build();

    }

    @Override
    public ResponseEntity<CourseSpaceDto> getCourseSpace(UUID courseSpaceId) {
        Optional<CourseSpace> courseSpace = courseSpaceService.getCourseSpaceById(courseSpaceId);

        if (courseSpace.isPresent()) {
            CourseSpace cs = courseSpace.get();
            return ResponseEntity.ok(courseSpaceMapper.toDto(cs));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
