package com.lecturebot.mapper;

import com.lecturebot.entity.User;
import com.lecturebot.generated.model.UserProfile;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper mapper = new UserMapper();

    @Test
    void toDto_validUser_success() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setEmail("test@example.com");
        user.setName("Test User");

        UserProfile dto = mapper.toDto(user);
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("Test User", dto.getName());
    }

    @Test
    void toDto_nullUser_returnsNull() {
        assertNull(mapper.toDto(null));
    }
}
