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

    @Override
    public ResponseEntity<List<com.lecturebot.generated.model.Document>> uploadDocuments(
            String courseSpaceId,
            List<MultipartFile> files
    ) {
        System.out.println("PDF upload - courseSpaceId: " + courseSpaceId + ", files: " + (files != null ? files.size() : 0));
        
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<com.lecturebot.generated.model.Document> responseList = new ArrayList<>();
        
        for (MultipartFile file : files) {
            // Validate PDF file
            if (file == null || !"application/pdf".equalsIgnoreCase(file.getContentType())) {
                System.err.println("Invalid file type: " + (file != null ? file.getContentType() : "null"));
                return ResponseEntity.badRequest().build();
            }

            try {
                // Validate course space ID
                UUID courseUuid = UUID.fromString(courseSpaceId);
                System.out.println("BEFORE calling processAndIndexPdf for file: " + file.getOriginalFilename());
                
                // Process PDF immediately (synchronous for reliability)
                Document processedDoc = documentProcessingService.processAndIndexPdf(file, courseUuid);
                System.out.println("AFTER calling processAndIndexPdf, got doc: " + processedDoc.getId());
                
                // Convert to API response
                com.lecturebot.generated.model.Document apiDoc = new com.lecturebot.generated.model.Document();
                apiDoc.setId(processedDoc.getId().toString());
                apiDoc.setFilename(processedDoc.getFilename());
                apiDoc.setFileType(com.lecturebot.generated.model.Document.FileTypeEnum.PDF);
                apiDoc.setUploadDate(OffsetDateTime.ofInstant(processedDoc.getUploadDate(), ZoneOffset.UTC));
                apiDoc.setProcessingStatus(com.lecturebot.generated.model.Document.ProcessingStatusEnum.valueOf(processedDoc.getUploadStatus().name()));
                apiDoc.setCourseId(processedDoc.getCourseId().toString());
                apiDoc.setExtractedText(processedDoc.getExtractedText());
                
                responseList.add(apiDoc);
                
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid courseSpaceId: " + courseSpaceId);
                return ResponseEntity.badRequest().build();
            } catch (Exception e) {
                System.err.println("PDF processing failed: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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