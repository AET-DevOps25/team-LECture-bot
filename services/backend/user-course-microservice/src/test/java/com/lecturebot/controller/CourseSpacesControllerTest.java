package com.lecturebot.controller;

import com.lecturebot.entity.CourseSpace;
import com.lecturebot.generated.model.CourseSpaceDto;
import com.lecturebot.generated.model.CreateCourseSpaceRequest;
import com.lecturebot.generated.model.UpdateCourseSpaceRequest;
import com.lecturebot.mapper.CourseSpaceMapper;
import com.lecturebot.service.CourseSpaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseSpacesControllerTest {

    @Mock
    private CourseSpaceService courseSpaceService;
    @Mock
    private CourseSpaceMapper courseSpaceMapper;
    @InjectMocks
    private CourseSpacesController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateCourseSpace_success() {
        UUID id = UUID.randomUUID();
        UpdateCourseSpaceRequest req = new UpdateCourseSpaceRequest().name("Math");
        CourseSpace cs = new CourseSpace();
        CourseSpaceDto dto = new CourseSpaceDto();
        when(courseSpaceService.updateCourseSpace(id, req)).thenReturn(Optional.of(cs));
        when(courseSpaceMapper.toDto(cs)).thenReturn(dto);
        ResponseEntity<CourseSpaceDto> response = controller.updateCourseSpace(id, req);
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void updateCourseSpace_notFound() {
        UUID id = UUID.randomUUID();
        UpdateCourseSpaceRequest req = new UpdateCourseSpaceRequest().name("Math");
        when(courseSpaceService.updateCourseSpace(id, req)).thenReturn(Optional.empty());
        ResponseEntity<CourseSpaceDto> response = controller.updateCourseSpace(id, req);
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getCourseSpaces_success() {
        List<CourseSpace> csList = Arrays.asList(new CourseSpace(), new CourseSpace());
        List<CourseSpaceDto> dtoList = Arrays.asList(new CourseSpaceDto(), new CourseSpaceDto());
        when(courseSpaceService.getCourseSpacesForCurrentUser()).thenReturn(csList);
        when(courseSpaceMapper.toDtoList(csList)).thenReturn(dtoList);
        ResponseEntity<List<CourseSpaceDto>> response = controller.getCourseSpaces();
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
    }

    @Test
    void getCourseSpaces_failure() {
        when(courseSpaceService.getCourseSpacesForCurrentUser()).thenThrow(new RuntimeException("fail"));
        assertThrows(RuntimeException.class, () -> controller.getCourseSpaces());
    }

    @Test
    void createCourseSpace_success() {
        CreateCourseSpaceRequest req = new CreateCourseSpaceRequest().name("Math");
        CourseSpace cs = new CourseSpace();
        CourseSpaceDto dto = new CourseSpaceDto();
        when(courseSpaceService.courseSpaceExists("Math")).thenReturn(false);
        when(courseSpaceService.createCourseSpace(req)).thenReturn(cs);
        when(courseSpaceMapper.toDto(cs)).thenReturn(dto);
        ResponseEntity<CourseSpaceDto> response = controller.createCourseSpace(req);
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void createCourseSpace_alreadyExists() {
        CreateCourseSpaceRequest req = new CreateCourseSpaceRequest().name("Math");
        when(courseSpaceService.courseSpaceExists("Math")).thenReturn(true);
        ResponseEntity<CourseSpaceDto> response = controller.createCourseSpace(req);
        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createCourseSpace_serviceThrows_returnsInternalServerError() {
        CreateCourseSpaceRequest req = new CreateCourseSpaceRequest().name("Math");
        when(courseSpaceService.courseSpaceExists("Math")).thenReturn(false);
        when(courseSpaceService.createCourseSpace(req)).thenThrow(new RuntimeException("fail"));
        ResponseEntity<CourseSpaceDto> response = controller.createCourseSpace(req);
        assertEquals(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void deleteCourseSpace_success() {
        UUID id = UUID.randomUUID();
        when(courseSpaceService.deleteCourseSpace(id)).thenReturn(true);
        ResponseEntity<Void> response = controller.deleteCourseSpace(id);
        assertEquals(org.springframework.http.HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteCourseSpace_failure() {
        UUID id = UUID.randomUUID();
        when(courseSpaceService.deleteCourseSpace(id)).thenReturn(false);
        ResponseEntity<Void> response = controller.deleteCourseSpace(id);
        assertEquals(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getCourseSpace_success() {
        UUID id = UUID.randomUUID();
        CourseSpace cs = new CourseSpace();
        CourseSpaceDto dto = new CourseSpaceDto();
        when(courseSpaceService.getCourseSpaceById(id)).thenReturn(Optional.of(cs));
        when(courseSpaceMapper.toDto(cs)).thenReturn(dto);
        ResponseEntity<CourseSpaceDto> response = controller.getCourseSpace(id);
        assertEquals(org.springframework.http.HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getCourseSpace_notFound() {
        UUID id = UUID.randomUUID();
        when(courseSpaceService.getCourseSpaceById(id)).thenReturn(Optional.empty());
        ResponseEntity<CourseSpaceDto> response = controller.getCourseSpace(id);
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
