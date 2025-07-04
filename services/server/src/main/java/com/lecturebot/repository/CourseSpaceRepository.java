package com.lecturebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lecturebot.entity.CourseSpace;
import com.lecturebot.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseSpaceRepository extends JpaRepository<CourseSpace, UUID> {

    Optional<CourseSpace> findById(UUID id);

    Optional<CourseSpace> findByOwnerAndName(User user, String name);

    List<CourseSpace> findByOwner(User user);

}
