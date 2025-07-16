package com.lecturebot.controller;

import com.lecturebot.entity.FileType;
import com.lecturebot.entity.ProcessingStatus;
import com.lecturebot.repository.DocumentRepository;
import com.lecturebot.service.DocumentProcessingService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class DocumentController implements DocumentApi {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentProcessingService documentProcessingService;

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
                doc.setFilePath(tempFile.getAbsolutePath()); // Store the file path
                doc.setFileType(FileType.PDF);
                doc.setUploadDate(Instant.now());
                doc.setUploadStatus(ProcessingStatus.PENDING);
                
                // Handle courseSpaceId - now properly store as UUID
                if (courseSpaceId != null && !courseSpaceId.trim().isEmpty()) {
                    try {
                        // Parse and store the UUID directly
                        UUID courseUuid = java.util.UUID.fromString(courseSpaceId.trim());
                        doc.setCourseId(courseUuid);
                        System.out.println("Stored courseSpaceId UUID: " + courseSpaceId);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid courseSpaceId format - not a valid UUID: '" + courseSpaceId + "'");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Collections.emptyList());
                    }
                } else {
                    System.err.println("courseSpaceId is null or empty: '" + courseSpaceId + "'");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Collections.emptyList());
                }
                // Set userId if needed

                Document saved = documentRepository.save(doc);

                // Trigger PDF processing asynchronously
                try {
                    documentProcessingService.processUpload(saved.getId(), tempFile);
                } catch (Exception e) {
                    System.err.println("Error during PDF processing: " + e.getMessage());
                    // Continue with response even if processing fails
                }

                // Map entity to generated model
                com.lecturebot.generated.model.Document apiDoc = new com.lecturebot.generated.model.Document();
                apiDoc.setId(saved.getId().toString()); // UUID to String
                apiDoc.setFilename(saved.getFilename());
                apiDoc.setFileType(com.lecturebot.generated.model.Document.FileTypeEnum.PDF);
                if (saved.getUploadDate() != null) {
                    apiDoc.setUploadDate(OffsetDateTime.ofInstant(saved.getUploadDate(), ZoneOffset.UTC));
                } else {
                    apiDoc.setUploadDate(null);
                }
                apiDoc.setProcessingStatus(com.lecturebot.generated.model.Document.ProcessingStatusEnum.PENDING);
                apiDoc.setCourseId(saved.getCourseId().toString()); // UUID to String
                apiDoc.setUserId(saved.getUserId() != null ? saved.getUserId().toString() : null);
                apiDoc.setExtractedText(saved.getExtractedText());

                responseList.add(apiDoc);

                // Send document ID and extracted text to GenAI service for indexing
                Map<String, Object> indexRequest = new HashMap<>();
                indexRequest.put("document_id", saved.getId().toString());
                indexRequest.put("course_space_id", saved.getCourseId().toString());
                indexRequest.put("text_content", saved.getExtractedText() != null ? saved.getExtractedText() : "");
                
                webClient.post()
                        .uri("/genai/index")
                        .bodyValue(indexRequest)
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
        try {
            UUID documentId = UUID.fromString(id);
            UUID courseId = UUID.fromString(courseSpaceId);
            
            Document doc = documentRepository.findById(documentId).orElse(null);
            if (doc == null || !doc.getCourseId().equals(courseId)) {
                return ResponseEntity.notFound().build();
            }
            
            com.lecturebot.generated.model.Document apiDoc = new com.lecturebot.generated.model.Document();
            apiDoc.setId(doc.getId().toString());
            apiDoc.setFilename(doc.getFilename());
            apiDoc.setFileType(com.lecturebot.generated.model.Document.FileTypeEnum.PDF);
            if (doc.getUploadDate() != null) {
                apiDoc.setUploadDate(OffsetDateTime.ofInstant(doc.getUploadDate(), ZoneOffset.UTC));
            } else {
                apiDoc.setUploadDate(null);
            }
            apiDoc.setProcessingStatus(com.lecturebot.generated.model.Document.ProcessingStatusEnum.valueOf(doc.getUploadStatus().name()));
            apiDoc.setCourseId(doc.getCourseId().toString());
            apiDoc.setUserId(doc.getUserId() != null ? doc.getUserId().toString() : null);
            apiDoc.setExtractedText(doc.getExtractedText());
            return ResponseEntity.ok(apiDoc);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
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
        try {
            UUID courseId = UUID.fromString(courseSpaceId);
            List<Document> docs = documentRepository.findByCourseId(courseId);
            List<com.lecturebot.generated.model.Document> responseList = new ArrayList<>();
            
            for (Document doc : docs) {
                com.lecturebot.generated.model.Document apiDoc = new com.lecturebot.generated.model.Document();
                apiDoc.setId(doc.getId().toString());
                apiDoc.setFilename(doc.getFilename());
                apiDoc.setFileType(com.lecturebot.generated.model.Document.FileTypeEnum.PDF);
                if (doc.getUploadDate() != null) {
                    apiDoc.setUploadDate(OffsetDateTime.ofInstant(doc.getUploadDate(), ZoneOffset.UTC));
                } else {
                    apiDoc.setUploadDate(null);
                }
                apiDoc.setProcessingStatus(com.lecturebot.generated.model.Document.ProcessingStatusEnum.valueOf(doc.getUploadStatus().name()));
                apiDoc.setCourseId(doc.getCourseId().toString());
                apiDoc.setUserId(doc.getUserId() != null ? doc.getUserId().toString() : null);
                apiDoc.setExtractedText(doc.getExtractedText());
                responseList.add(apiDoc);
            }
            return ResponseEntity.ok(responseList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}