package com.lecturebot.documentservice.repository;

import com.lecturebot.documentservice.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
