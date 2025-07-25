package com.lecturebot.repository;

import com.lecturebot.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    java.util.List<Document> findByCourseId(UUID courseId);
}
