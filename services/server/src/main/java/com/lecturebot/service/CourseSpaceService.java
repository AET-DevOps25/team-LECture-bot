package com.lecturebot.service;

import org.springframework.stereotype.Service;
import com.lecturebot.mapper.CourseSpaceMapper;
import com.lecturebot.repository.CourseSpaceRepository;
import com.lecturebot.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import com.lecturebot.entity.CourseSpace;
import com.lecturebot.generated.model.CourseSpaceDto;
import com.lecturebot.entity.User;
import com.lecturebot.generated.model.CreateCourseSpaceRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class CourseSpaceService {

    private final CourseSpaceRepository courseSpaceRepository;
    private final UserService userService;

    public CourseSpaceService(CourseSpaceRepository courseSpaceRepository, UserService userService) {
        this.courseSpaceRepository = courseSpaceRepository;
        this.userService = userService;
    }

    /**
     * Creates a new CourseSpace for the given user.
     *
     * @param name The name of the CourseSpace.
     * @param user The user who owns the CourseSpace.
     * @return The created CourseSpace.
     */
    @Transactional
    public CourseSpace createCourseSpace(CreateCourseSpaceRequest createCourseSpaceRequest) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        // Check if a course space with this name already exists for this user.
        if (courseSpaceRepository
                .findByOwnerAndName(currentUser, createCourseSpaceRequest.getName())
                .isPresent()) {
            throw new IllegalArgumentException("A course space with this name already exists for you.");
        }

        CourseSpace courseSpace = new CourseSpace(createCourseSpaceRequest.getName(), currentUser);

        return courseSpaceRepository.save(courseSpace);
    }

    @Transactional(readOnly = true)
    public List<CourseSpace> getCourseSpacesForCurrentUser() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return courseSpaceRepository.findByOwner(currentUser);
    }

    @Transactional(readOnly = true)
    public Optional<CourseSpace> getCourseSpaceForCurrentUserByName(String name) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return courseSpaceRepository.findByOwnerAndName(currentUser, name);
    }

    @Transactional
    public boolean deleteCourseSpace(UUID courseSpaceId) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        Optional<CourseSpace> courseSpace = courseSpaceRepository.findById(courseSpaceId);

        if (courseSpace.isPresent()
                && courseSpace.get().getOwner().getId().equals(currentUser.getId())) {
            courseSpaceRepository.deleteById(courseSpaceId);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean courseSpaceExists(String name) {
        User currentUser = userService.getCurrentAuthenticatedUser();

        return courseSpaceRepository.findByOwnerAndName(currentUser, name).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<CourseSpace> getCourseSpaceById(UUID id) {
        return courseSpaceRepository.findById(id);
    }

}
