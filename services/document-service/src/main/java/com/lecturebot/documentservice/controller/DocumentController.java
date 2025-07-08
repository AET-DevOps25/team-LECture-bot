package com.lecturebot.documentservice.controller;

import com.lecturebot.documentservice.entity.FileType;
import com.lecturebot.documentservice.entity.ProcessingStatus;
import com.lecturebot.documentservice.repository.DocumentRepository;
import com.lecturebot.documentservice.generated.api.DocumentApi;
import com.lecturebot.documentservice.entity.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class DocumentController implements DocumentApi {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    @Qualifier("genaiWebClient")
    private WebClient webClient;

    /**
     * Handles the uploading of PDF documents.
     *
     * @param files the list of PDF files to upload
     * @return a ResponseEntity containing a list of uploaded document representations or an error status
     */
    // POST /documents
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<com.lecturebot.documentservice.generated.model.Document>> documentsPost(List<MultipartFile> files) {
        List<com.lecturebot.documentservice.generated.model.Document> responseList = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseList);
        }

        for (MultipartFile file : files) {
            if (file == null || !"application/pdf".equalsIgnoreCase(file.getContentType())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.emptyList());
            }

            try {
                // Save file to temp location
                String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
                File tempFile = File.createTempFile("upload-", ".pdf");
                file.transferTo(tempFile);

                // Create and save Document entity
                Document doc = new Document();
                doc.setFilename(originalFilename);
                doc.setFileType(FileType.PDF);
                doc.setUploadDate(Instant.now());
                doc.setProcessingStatus(ProcessingStatus.PENDING); // Use PENDING instead of UPLOADED
                // Set courseId, userId if needed

                Document saved = documentRepository.save(doc);

                // Map entity to generated model
                com.lecturebot.documentservice.generated.model.Document apiDoc = new com.lecturebot.documentservice.generated.model.Document();
                apiDoc.setId(String.valueOf(saved.getId()));
                apiDoc.setFilename(saved.getFilename());
                apiDoc.setFileType(com.lecturebot.documentservice.generated.model.Document.FileTypeEnum.PDF);
                if (saved.getUploadDate() != null) {
                    apiDoc.setUploadDate(OffsetDateTime.ofInstant(saved.getUploadDate(), ZoneOffset.UTC));
                } else {
                    apiDoc.setUploadDate(null);
                }
                apiDoc.setProcessingStatus(com.lecturebot.documentservice.generated.model.Document.ProcessingStatusEnum.PENDING); // Use PENDING here too
                apiDoc.setCourseId(saved.getCourseId() != null ? saved.getCourseId().toString() : null);
                apiDoc.setUserId(saved.getUserId() != null ? saved.getUserId().toString() : null);
                apiDoc.setExtractedText(saved.getExtractedText());

                responseList.add(apiDoc);

                // Send document ID and extracted text to GenAI service for indexing
                webClient.post()
                        .uri("/genai/index")
                        .bodyValue(Map.of(
                                "document_id", saved.getId().toString(),
                                "course_space_id", saved.getCourseId() != null ? saved.getCourseId().toString() : null,
                                "text_content", saved.getExtractedText() != null ? saved.getExtractedText() : null
                        ))
                        .retrieve()
                        .bodyToMono(Void.class)
                        .doOnError(error -> {
                            System.err.println("Error sending document to GenAI service: " + error.getMessage());
                        })
                        .subscribe(); // Fire-and-forget, but errors are now logged

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
            }
        }
        return ResponseEntity.ok(responseList);
    }

    /**
     * Retrieves a document by its ID.
     *
     * @param id the ID of the document to retrieve
     * @return ResponseEntity containing the document if found, or 404 Not Found if not found
     */
    // GET /documents/{id}
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<com.lecturebot.documentservice.generated.model.Document> documentsIdGet(String id) {
        Document doc = documentRepository.findById(Long.valueOf(id)).orElse(null);
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }
        com.lecturebot.documentservice.generated.model.Document apiDoc = new com.lecturebot.documentservice.generated.model.Document();
        apiDoc.setId(String.valueOf(doc.getId()));
        apiDoc.setFilename(doc.getFilename());
        apiDoc.setFileType(com.lecturebot.documentservice.generated.model.Document.FileTypeEnum.PDF);
        if (doc.getUploadDate() != null) {
            apiDoc.setUploadDate(OffsetDateTime.ofInstant(doc.getUploadDate(), ZoneOffset.UTC));
        } else {
            apiDoc.setUploadDate(null);
        }
        apiDoc.setProcessingStatus(com.lecturebot.documentservice.generated.model.Document.ProcessingStatusEnum.valueOf(doc.getProcessingStatus().name()));
        apiDoc.setCourseId(doc.getCourseId() != null ? doc.getCourseId().toString() : null);
        apiDoc.setUserId(doc.getUserId() != null ? doc.getUserId().toString() : null);
        apiDoc.setExtractedText(doc.getExtractedText());
        return ResponseEntity.ok(apiDoc);
    }
}