package com.lecturebot.service;

import org.springframework.stereotype.Service;
import com.lecturebot.repository.CourseSpaceRepository;
import com.lecturebot.entity.CourseSpace;
import com.lecturebot.dto.CourseSpaceResponse;
import com.lecturebot.entity.User;
import java.util.List;
import java.util.UUID;

@Service
public class CourseSpaceService {
    // For OpenAPI controller: return entities for mapping
    public List<CourseSpace> getCourseSpacesForCurrentUserEntities() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return courseSpaceRepository.findByOwner(currentUser);
    }

    // For OpenAPI controller: create from OpenAPI model
    public CourseSpace createCourseSpaceFromOpenApi(com.lecturebot.generated.model.CreateCourseSpaceRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        // Check for duplicate title for this user using repository method
        if (courseSpaceRepository.findByOwnerAndTitle(currentUser, request.getTitle()).isPresent()) {
            throw new IllegalArgumentException("A course space with this title already exists for you.");
        }
        CourseSpace courseSpace = new CourseSpace(request.getTitle(), null, currentUser);
        return courseSpaceRepository.save(courseSpace);
    }

    private final CourseSpaceRepository courseSpaceRepository;
    private final UserService userService;

    public CourseSpaceService(CourseSpaceRepository courseSpaceRepository, UserService userService) {
        this.courseSpaceRepository = courseSpaceRepository;
        this.userService = userService;
    }

    // --- CRUD for CourseSpace with title/description, using DTOs and ownership checks ---

    public CourseSpaceResponse createCourseSpace(com.lecturebot.dto.CreateCourseSpaceRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        // Check for duplicate title for this user using repository method
        if (courseSpaceRepository.findByOwnerAndTitle(currentUser, request.getTitle()).isPresent()) {
            throw new IllegalArgumentException("A course space with this title already exists for you.");
        }
        CourseSpace courseSpace = new CourseSpace(request.getTitle(), request.getDescription(), currentUser);
        CourseSpace saved = courseSpaceRepository.save(courseSpace);
        return toResponse(saved);
    }

    public List<CourseSpaceResponse> getCourseSpacesForCurrentUser() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return courseSpaceRepository.findByOwner(currentUser)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CourseSpaceResponse getCourseSpaceById(UUID id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        CourseSpace cs = courseSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course space not found"));
        if (!cs.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("Not authorized");
        }
        return toResponse(cs);
    }

    public CourseSpaceResponse updateCourseSpace(UUID id, com.lecturebot.dto.UpdateCourseSpaceRequest request) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        CourseSpace cs = courseSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course space not found"));
        if (!cs.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("Not authorized");
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            cs.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            cs.setDescription(request.getDescription());
        }
        CourseSpace updated = courseSpaceRepository.save(cs);
        return toResponse(updated);
    }

    public void deleteCourseSpace(UUID id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        CourseSpace cs = courseSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course space not found"));
        if (!cs.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("Not authorized");
        }
        courseSpaceRepository.delete(cs);
    }

    private CourseSpaceResponse toResponse(CourseSpace cs) {
        return new CourseSpaceResponse(
                cs.getId(),
                cs.getTitle(),
                cs.getDescription(),
                cs.getOwner() != null ? cs.getOwner().getId() : null,
                cs.getCreatedAt(),
                cs.getUpdatedAt()
        );
    }

}
