package com.lecturebot.service;

import com.lecturebot.entity.Document;
import com.lecturebot.entity.FileType;
import com.lecturebot.entity.ProcessingStatus;
import com.lecturebot.repository.DocumentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessingService.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    @Qualifier("genaiWebClient")
    private WebClient webClient;

    /**
     * Process PDF file: save to DB, extract text, send to GenAI
     * Simple synchronous approach for reliability
     */
    public Document processAndIndexPdf(MultipartFile file, UUID courseId) throws Exception {
        logger.info("Processing PDF: {}, course: {}", file.getOriginalFilename(), courseId);
        
        File tempFile = null;
        Document saved = null;
        try {
            // 1. Save file temporarily
            tempFile = File.createTempFile("pdf-", ".pdf");
            file.transferTo(tempFile);

            // 2. Save document metadata to database with PENDING status initially
            Document doc = new Document();
            doc.setFilename(StringUtils.cleanPath(file.getOriginalFilename()));
            doc.setFilePath(tempFile.getAbsolutePath());
            doc.setFileType(FileType.PDF);
            doc.setUploadDate(Instant.now());
            doc.setCourseId(courseId);
            doc.setUploadStatus(ProcessingStatus.PENDING);

            // try {
                saved = documentRepository.save(doc);
                logger.info("Saved document to DB with PENDING status: {}", saved.getId());
            // } catch (Exception e) {
            //     logger.error("Failed to save document metadata: {}", e.getMessage(), e);
            //     throw new RuntimeException(e.getMessage(), e);
            // }


            // 3. Update status to PROCESSING_EXTRACTION
            saved.setUploadStatus(ProcessingStatus.PROCESSING_EXTRACTION);
            saved = documentRepository.save(saved);
            logger.info("Updated document status to PROCESSING_EXTRACTION: {}", saved.getId());

            // 4. Extract text from PDF
            String extractedText = extractTextFromPdf(tempFile);
            logger.info("Extracted {} characters from PDF", extractedText != null ? extractedText.length() : 0);

            // Validate we have actual text content
            if (extractedText == null || extractedText.trim().isEmpty()) {
                // logger.error(extractedText == null ? "Extracted text is null" : "Extracted text is empty");
                throw new RuntimeException("No text could be extracted from PDF");
            }

            // 5. Clean and format text for GenAI
            String cleanedText = cleanTextForGenAI(extractedText);
            logger.info("Cleaned text length: {} characters", cleanedText != null ? cleanedText.length() : 0);

            // Debug: Output cleaned text content
            if (cleanedText != null) {
                logger.info("Cleaned text content: '{}'", cleanedText);
            }

            // Validate we have actual text content
            if (cleanedText == null || cleanedText.trim().isEmpty()) {
                throw new RuntimeException("No text could be cleaned");
            }

            // 6. Update status to PROCESSING_INDEXING
            saved.setUploadStatus(ProcessingStatus.PROCESSING_INDEXING);
            saved = documentRepository.save(saved);
            logger.info("Updated document status to PROCESSING_INDEXING: {}", saved.getId());

            // 7. Send to GenAI service for indexing
            sendToGenAIService(saved, cleanedText);

            // 8. Update status to COMPLETED
            saved.setUploadStatus(ProcessingStatus.COMPLETED);
            saved = documentRepository.save(saved);
            logger.info("Updated document status to COMPLETED: {}", saved.getId());

            return saved;

        } catch (Exception e) {
            // If we have a saved document, update its status to FAILED
            if (saved != null) {
                try {
                    saved.setUploadStatus(ProcessingStatus.FAILED);
                    documentRepository.save(saved);
                    logger.error("Updated document status to FAILED for document: {}", saved.getId());
                } catch (Exception updateException) {
                    logger.error("Failed to update document status to FAILED", updateException);
                }
            }

            logger.error("Error processing PDF: {}", e.getMessage(), e);

            if(saved != null && saved.getUploadStatus() == ProcessingStatus.FAILED) {
                documentRepository.delete(saved);
                logger.info("Document {} deleted after unsuccessful processing", saved.getId());
            }

            throw e;

        } finally {
            // Clean up temp file
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                logger.debug("Temp file deleted: {}", deleted);
            }
        }
    }

    private String extractTextFromPdf(File pdfFile) throws IOException {
        logger.debug("Extracting text from: {}", pdfFile.getAbsolutePath());
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            logger.debug("Extracted text length: {}, pages: {}", 
                        text != null ? text.length() : 0, 
                        document.getNumberOfPages());
            
            return text;
        }
    }

    /**
     * Clean and format text for GenAI service:
     * - Convert all newlines to spaces
     * - Replace double quotes with single quotes
     * - Remove excessive whitespace
     * - Trim the result
     */
    private String cleanTextForGenAI(String rawText) {
        if (rawText == null) {
            return null;
        }
        
        logger.debug("Cleaning text for GenAI, original length: {}", rawText.length());
        
        String cleaned = rawText
            // Replace all types of newlines and line breaks with spaces
            .replaceAll("\\r\\n|\\r|\\n", " ")
            // Replace double quotes with single quotes to avoid JSON issues
            .replaceAll("\"", "'")
            // Replace multiple consecutive spaces with single space
            .replaceAll("\\s+", " ")
            // Remove any control characters
            .replaceAll("[\\p{Cntrl}]", " ")
            // Trim whitespace
            .trim();
        
        logger.debug("Cleaned text length: {}", cleaned.length());
        return cleaned;
    }

    private void sendToGenAIService(Document document, String cleanedText) {
        try {
            logger.info("Sending to GenAI: doc={}, textLength={}", 
                       document.getId(), cleanedText != null ? cleanedText.length() : 0);
            
            // Validate text before sending
            if (cleanedText == null || cleanedText.trim().isEmpty()) {
                logger.error("Cannot send empty text to GenAI service for document {}", document.getId());
                return;
            }
            
            Map<String, Object> request = new HashMap<>();
            request.put("document_id", document.getId().toString());
            request.put("course_space_id", document.getCourseId().toString());
            request.put("text_content", cleanedText.trim());
            
            logger.info("GenAI request: doc_id={}, course_id={}, text_chars={}", 
                       document.getId(), document.getCourseId(), cleanedText.trim().length());
            
            String response = webClient.post()
                    .uri("/api/v1/genai/index")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Synchronous for simplicity
                    
            logger.info("GenAI response for {}: {}", document.getId(), response);
            
        } catch (Exception e) {
            logger.error("Failed to send to GenAI service for {}: {}", document.getId(), e.getMessage(), e);
            throw new RuntimeException("GenAI indexing failed", e);
        }
    }
}
