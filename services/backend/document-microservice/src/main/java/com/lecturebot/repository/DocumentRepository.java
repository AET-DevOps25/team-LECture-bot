package com.lecturebot.repository;

import com.lecturebot.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    java.util.List<Document> findByCourseId(Long courseId);
}
