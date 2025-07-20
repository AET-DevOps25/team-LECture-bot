package com.lecturebot.mapper;

import com.lecturebot.entity.CourseSpace;
import com.lecturebot.entity.User;
import com.lecturebot.generated.model.CreateCourseSpaceRequest;
import com.lecturebot.generated.model.CourseSpaceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CourseSpaceMapperTest {
    private CourseSpaceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CourseSpaceMapper();
    }

    @Test
    void toEntity_validInput_success() {
        CreateCourseSpaceRequest req = new CreateCourseSpaceRequest().name("Math").description("desc");
        User user = new User();
        CourseSpace cs = mapper.toEntity(req, user);
        assertEquals("Math", cs.getName());
        assertEquals("desc", cs.getDescription());
        assertEquals(user, cs.getOwner());
    }

    @Test
    void toEntity_nullRequest_throwsException() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> mapper.toEntity(null, user));
    }

    @Test
    void toEntity_nullOwner_throwsException() {
        CreateCourseSpaceRequest req = new CreateCourseSpaceRequest().name("Math");
        assertThrows(IllegalArgumentException.class, () -> mapper.toEntity(req, null));
    }

    @Test
    void toDto_validEntity_success() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        CourseSpace cs = new CourseSpace("Math", "desc", new User());
        cs.setId(id);
        cs.setCreatedAt(now);
        cs.setUpdatedAt(now);
        CourseSpaceDto dto = mapper.toDto(cs);
        assertEquals(id, dto.getId());
        assertEquals("Math", dto.getName());
        assertEquals("desc", dto.getDescription());
        assertEquals(now.atOffset(ZoneOffset.UTC), dto.getCreatedAt().toInstant().atOffset(ZoneOffset.UTC));
        assertEquals(now.atOffset(ZoneOffset.UTC), dto.getUpdatedAt().toInstant().atOffset(ZoneOffset.UTC));
    }

    @Test
    void toDto_nullEntity_returnsNull() {
        assertNull(mapper.toDto(null));
    }

    @Test
    void toDtoList_validList_success() {
        Instant now = Instant.now();
        CourseSpace cs1 = new CourseSpace("Math", "desc", new User());
        cs1.setId(UUID.randomUUID());
        cs1.setCreatedAt(now);
        cs1.setUpdatedAt(now);
        CourseSpace cs2 = new CourseSpace("Physics", "desc2", new User());
        cs2.setId(UUID.randomUUID());
        cs2.setCreatedAt(now);
        cs2.setUpdatedAt(now);
        List<CourseSpace> list = Arrays.asList(cs1, cs2);
        List<CourseSpaceDto> dtos = mapper.toDtoList(list);
        assertEquals(2, dtos.size());
        assertEquals(cs1.getName(), dtos.get(0).getName());
        assertEquals(cs2.getName(), dtos.get(1).getName());
    }

    @Test
    void toDtoList_nullList_returnsNull() {
        assertNull(mapper.toDtoList(null));
    }

    @Test
    void toDtoList_emptyList_returnsEmptyList() {
        List<CourseSpaceDto> dtos = mapper.toDtoList(Arrays.asList());
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }
}
