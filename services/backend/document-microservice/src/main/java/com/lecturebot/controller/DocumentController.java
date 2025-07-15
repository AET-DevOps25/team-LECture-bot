package com.lecturebot.controller;

import com.lecturebot.entity.FileType;
import com.lecturebot.entity.ProcessingStatus;
import com.lecturebot.repository.DocumentRepository;
import com.lecturebot.generated.api.DocumentApi;
import com.lecturebot.entity.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestPart;
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
     * Handles the uploading of PDF documents to a specific course space.
     *
     * @param courseSpaceId the ID of the course space
     * @param files         the list of PDF files to upload
     * @return a ResponseEntity containing a list of uploaded document representations or an error status
     */
    // POST /documents/courseSpaceId
    @Override
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<com.lecturebot.generated.model.Document>> uploadDocuments(
            String courseSpaceId,
            List<MultipartFile> files
    ) {
        System.out.println("Upload request received - courseSpaceId: '" + courseSpaceId + "', files count: " + (files != null ? files.size() : 0));
        
        List<com.lecturebot.generated.model.Document> responseList = new ArrayList<>();
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
                
                // Handle courseSpaceId conversion with error handling
                try {
                    if (courseSpaceId != null && !courseSpaceId.trim().isEmpty()) {
                        doc.setCourseId(Long.valueOf(courseSpaceId.trim()));
                    } else {
                        System.err.println("courseSpaceId is null or empty: '" + courseSpaceId + "'");
                        doc.setCourseId(null);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid courseSpaceId format: '" + courseSpaceId + "' - " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Collections.emptyList());
                }
                // Set userId if needed

                Document saved = documentRepository.save(doc);

                // Map entity to generated model
                com.lecturebot.generated.model.Document apiDoc = new com.lecturebot.generated.model.Document();
                apiDoc.setId(String.valueOf(saved.getId()));
                apiDoc.setFilename(saved.getFilename());
                apiDoc.setFileType(com.lecturebot.generated.model.Document.FileTypeEnum.PDF);
                if (saved.getUploadDate() != null) {
                    apiDoc.setUploadDate(OffsetDateTime.ofInstant(saved.getUploadDate(), ZoneOffset.UTC));
                } else {
                    apiDoc.setUploadDate(null);
                }
                apiDoc.setProcessingStatus(com.lecturebot.generated.model.Document.ProcessingStatusEnum.PENDING); // Use PENDING here too
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
     * Retrieves a document by its ID within a specific course space.
     *
     * @param courseSpaceId the ID of the course space
     * @param id             the ID of the document to retrieve
     * @return ResponseEntity containing the document if found, or 404 Not Found if not found
     */
    // GET /documents/courseSpaceId/{id}
    @Override
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<com.lecturebot.generated.model.Document> getDocumentById(
            String courseSpaceId,
            String id
    ) {
        Document doc = documentRepository.findById(Long.valueOf(id)).orElse(null);
        if (doc == null || !doc.getCourseId().toString().equals(courseSpaceId)) {
            return ResponseEntity.notFound().build();
        }
        com.lecturebot.generated.model.Document apiDoc = new com.lecturebot.generated.model.Document();
        apiDoc.setId(String.valueOf(doc.getId()));
        apiDoc.setFilename(doc.getFilename());
        apiDoc.setFileType(com.lecturebot.generated.model.Document.FileTypeEnum.PDF);
        if (doc.getUploadDate() != null) {
            apiDoc.setUploadDate(OffsetDateTime.ofInstant(doc.getUploadDate(), ZoneOffset.UTC));
        } else {
            apiDoc.setUploadDate(null);
        }
        apiDoc.setProcessingStatus(com.lecturebot.generated.model.Document.ProcessingStatusEnum.valueOf(doc.getProcessingStatus().name()));
        apiDoc.setCourseId(doc.getCourseId() != null ? doc.getCourseId().toString() : null);
        apiDoc.setUserId(doc.getUserId() != null ? doc.getUserId().toString() : null);
        apiDoc.setExtractedText(doc.getExtractedText());
        return ResponseEntity.ok(apiDoc);
    }

    /**
     * Retrieves all documents for a specific course space.
     *
     * @param courseSpaceId the ID of the course space
     * @return ResponseEntity containing a list of documents for the course space
     */
    // GET /documents/courseSpaceId
    @Override
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<com.lecturebot.generated.model.Document>> listDocuments(
            String courseSpaceId
    ) {
        List<Document> docs = documentRepository.findByCourseId(Long.valueOf(courseSpaceId));
        List<com.lecturebot.generated.model.Document> responseList = new ArrayList<>();
        for (Document doc : docs) {
            com.lecturebot.generated.model.Document apiDoc = new com.lecturebot.generated.model.Document();
            apiDoc.setId(String.valueOf(doc.getId()));
            apiDoc.setFilename(doc.getFilename());
            apiDoc.setFileType(com.lecturebot.generated.model.Document.FileTypeEnum.PDF);
            if (doc.getUploadDate() != null) {
                apiDoc.setUploadDate(OffsetDateTime.ofInstant(doc.getUploadDate(), ZoneOffset.UTC));
            } else {
                apiDoc.setUploadDate(null);
            }
            apiDoc.setProcessingStatus(com.lecturebot.generated.model.Document.ProcessingStatusEnum.valueOf(doc.getProcessingStatus().name()));
            apiDoc.setCourseId(doc.getCourseId() != null ? doc.getCourseId().toString() : null);
            apiDoc.setUserId(doc.getUserId() != null ? doc.getUserId().toString() : null);
            apiDoc.setExtractedText(doc.getExtractedText());
            responseList.add(apiDoc);
        }
        return ResponseEntity.ok(responseList);
    }
}