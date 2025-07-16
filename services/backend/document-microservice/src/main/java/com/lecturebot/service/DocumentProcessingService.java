package com.lecturebot.service;

import com.lecturebot.entity.Document;
import com.lecturebot.entity.ProcessingStatus;
import com.lecturebot.repository.DocumentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class DocumentProcessingService {

    @Autowired
    private DocumentRepository documentRepository;

    public void processUpload(UUID documentId, File pdfFile) {
        Document doc = documentRepository.findById(documentId).orElse(null);
        if (doc == null) return;

        try {
            doc.setUploadStatus(ProcessingStatus.PROCESSING);
            documentRepository.save(doc);

            // Extract text from PDF
            String extractedText = extractTextFromPdf(pdfFile);

            doc.setExtractedText(extractedText);
            // Set status to COMPLETED after extraction
            doc.setUploadStatus(ProcessingStatus.COMPLETED);
            documentRepository.save(doc);

        } catch (Exception e) {
            doc.setUploadStatus(ProcessingStatus.FAILED);
            documentRepository.save(doc);
            // Optionally log error
        }
    }

    private String extractTextFromPdf(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
