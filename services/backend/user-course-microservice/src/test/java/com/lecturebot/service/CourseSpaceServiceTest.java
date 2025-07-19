package com.lecturebot.service;

import com.lecturebot.entity.CourseSpace;
import com.lecturebot.entity.User;
import com.lecturebot.generated.model.CreateCourseSpaceRequest;
import com.lecturebot.generated.model.UpdateCourseSpaceRequest;
import com.lecturebot.repository.CourseSpaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseSpaceServiceTest {
    @Mock
    private CourseSpaceRepository courseSpaceRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private CourseSpaceService courseSpaceService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("Test User");
    }

    @Test
    void createCourseSpace_success() {
        CreateCourseSpaceRequest req = new CreateCourseSpaceRequest().name("Math").description("desc");
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(courseSpaceRepository.findByOwnerAndName(user, "Math")).thenReturn(Optional.empty());
        CourseSpace cs = new CourseSpace("Math", "desc", user);
        when(courseSpaceRepository.save(any(CourseSpace.class))).thenReturn(cs);
        CourseSpace result = courseSpaceService.createCourseSpace(req);
        assertEquals("Math", result.getName());
        assertEquals("desc", result.getDescription());
        assertEquals(user, result.getOwner());
    }

    @Test
    void createCourseSpace_duplicateName_throwsException() {
        CreateCourseSpaceRequest req = new CreateCourseSpaceRequest().name("Math").description("desc");
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(courseSpaceRepository.findByOwnerAndName(user, "Math")).thenReturn(Optional.of(new CourseSpace()));
        assertThrows(IllegalArgumentException.class, () -> courseSpaceService.createCourseSpace(req));
    }

    @Test
    void updateCourseSpace_success() {
        UUID id = UUID.randomUUID();
        UpdateCourseSpaceRequest req = new UpdateCourseSpaceRequest().name("Physics").description("desc2");
        CourseSpace cs = new CourseSpace("Math", "desc", user);
        cs.setId(id);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(courseSpaceRepository.findById(id)).thenReturn(Optional.of(cs));
        when(courseSpaceRepository.save(any(CourseSpace.class))).thenReturn(cs);
        Optional<CourseSpace> result = courseSpaceService.updateCourseSpace(id, req);
        assertTrue(result.isPresent());
        assertEquals("Physics", result.get().getName());
        assertEquals("desc2", result.get().getDescription());
    }

    @Test
    void updateCourseSpace_notFound_returnsEmpty() {
        UUID id = UUID.randomUUID();
        UpdateCourseSpaceRequest req = new UpdateCourseSpaceRequest().name("Physics").description("desc2");
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(courseSpaceRepository.findById(id)).thenReturn(Optional.empty());
        Optional<CourseSpace> result = courseSpaceService.updateCourseSpace(id, req);
        assertFalse(result.isPresent());
    }

    @Test
    void updateCourseSpace_notOwner_returnsEmpty() {
        UUID id = UUID.randomUUID();
        UpdateCourseSpaceRequest req = new UpdateCourseSpaceRequest().name("Physics").description("desc2");
        User otherUser = new User();
        otherUser.setId(2L);
        CourseSpace cs = new CourseSpace("Math", "desc", otherUser);
        cs.setId(id);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(courseSpaceRepository.findById(id)).thenReturn(Optional.of(cs));
        Optional<CourseSpace> result = courseSpaceService.updateCourseSpace(id, req);
        assertFalse(result.isPresent());
    }

    @Test
    void getCourseSpacesForCurrentUser_success() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        List<CourseSpace> list = Arrays.asList(new CourseSpace("Math", "desc", user));
        when(courseSpaceRepository.findByOwner(user)).thenReturn(list);
        List<CourseSpace> result = courseSpaceService.getCourseSpacesForCurrentUser();
        assertEquals(1, result.size());
    }

    @Test
    void getCourseSpaceForCurrentUserByName_success() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        CourseSpace cs = new CourseSpace("Math", "desc", user);
        when(courseSpaceRepository.findByOwnerAndName(user, "Math")).thenReturn(Optional.of(cs));
        Optional<CourseSpace> result = courseSpaceService.getCourseSpaceForCurrentUserByName("Math");
        assertTrue(result.isPresent());
    }

    @Test
    void deleteCourseSpace_success() {
        UUID id = UUID.randomUUID();
        CourseSpace cs = new CourseSpace("Math", "desc", user);
        cs.setId(id);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(courseSpaceRepository.findById(id)).thenReturn(Optional.of(cs));
        boolean result = courseSpaceService.deleteCourseSpace(id);
        assertTrue(result);
        verify(courseSpaceRepository).deleteById(id);
    }

    @Test
    void deleteCourseSpace_notOwner_returnsFalse() {
        UUID id = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setId(2L);
        CourseSpace cs = new CourseSpace("Math", "desc", otherUser);
        cs.setId(id);
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(courseSpaceRepository.findById(id)).thenReturn(Optional.of(cs));
        boolean result = courseSpaceService.deleteCourseSpace(id);
        assertFalse(result);
        verify(courseSpaceRepository, never()).deleteById(id);
    }

    @Test
    void courseSpaceExists_true() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(courseSpaceRepository.findByOwnerAndName(user, "Math")).thenReturn(Optional.of(new CourseSpace()));
        assertTrue(courseSpaceService.courseSpaceExists("Math"));
    }

    @Test
    void courseSpaceExists_false() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(courseSpaceRepository.findByOwnerAndName(user, "Math")).thenReturn(Optional.empty());
        assertFalse(courseSpaceService.courseSpaceExists("Math"));
    }

    @Test
    void getCourseSpaceById_success() {
        UUID id = UUID.randomUUID();
        CourseSpace cs = new CourseSpace("Math", "desc", user);
        cs.setId(id);
        when(courseSpaceRepository.findById(id)).thenReturn(Optional.of(cs));
        Optional<CourseSpace> result = courseSpaceService.getCourseSpaceById(id);
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void getCourseSpaceById_notFound() {
        UUID id = UUID.randomUUID();
        when(courseSpaceRepository.findById(id)).thenReturn(Optional.empty());
        Optional<CourseSpace> result = courseSpaceService.getCourseSpaceById(id);
        assertFalse(result.isPresent());
    }
}
