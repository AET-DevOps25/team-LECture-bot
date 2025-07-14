package com.lecturebot.mapper;

import org.springframework.stereotype.Component;
import com.lecturebot.entity.User;
import com.lecturebot.generated.model.UserProfile;

@Component
public class UserMapper {

    public UserProfile toDto(User entity) {
        if (entity == null) {
            return null;
        }

        return new UserProfile()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName());
    }
}
