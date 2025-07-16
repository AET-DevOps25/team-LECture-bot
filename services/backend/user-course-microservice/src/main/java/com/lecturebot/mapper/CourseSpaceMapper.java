package com.lecturebot.mapper;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import com.lecturebot.entity.CourseSpace;
import com.lecturebot.entity.User;
import com.lecturebot.generated.model.CreateCourseSpaceRequest;
import com.lecturebot.generated.model.CourseSpaceDto;

@Component
public class CourseSpaceMapper {
    /**
     * Converts a CreateCourseSpaceRequest DTO and a User entity into a CourseSpace
     * JPA entity.
     * This is used before saving to the database.
     */
    public CourseSpace toEntity(CreateCourseSpaceRequest request, User owner) {
        if (request == null || owner == null) {
            throw new IllegalArgumentException("Request and owner must not be null");
        }
        CourseSpace courseSpace = new CourseSpace(request.getName(), request.getDescription(), owner);
        return courseSpace;
    }

    /**
     * Converts a CourseSpace JPA entity into the OpenAPI-generated CourseSpace DTO.
     * This is used to create the API response.
     */
    public CourseSpaceDto toDto(CourseSpace entity) {
        if (entity == null) {
            return null;
        }

        return new CourseSpaceDto()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt().atOffset(ZoneOffset.UTC))
                .updatedAt(entity.getUpdatedAt().atOffset(ZoneOffset.UTC));
    }

    public List<CourseSpaceDto> toDtoList(List<CourseSpace> courseSpaces) {
        if (courseSpaces == null) {
            return null;
        }
        return courseSpaces.stream().map(this::toDto).collect(Collectors.toList());
    }
}
