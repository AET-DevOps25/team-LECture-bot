package com.lecturebot.controller;

import com.lecturebot.entity.Document;
import com.lecturebot.generated.model.Document.FileTypeEnum;
import com.lecturebot.generated.model.Document.ProcessingStatusEnum;
import com.lecturebot.repository.DocumentRepository;
import com.lecturebot.service.DocumentProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DocumentControllerTest {
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private DocumentProcessingService documentProcessingService;
    @Mock
    private WebClient webClient;
    @InjectMocks
    private DocumentController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new DocumentController();
        // Inject mocks manually
        try {
            java.lang.reflect.Field repoField = DocumentController.class.getDeclaredField("documentRepository");
            repoField.setAccessible(true);
            repoField.set(controller, documentRepository);
            java.lang.reflect.Field procField = DocumentController.class.getDeclaredField("documentProcessingService");
            procField.setAccessible(true);
            procField.set(controller, documentProcessingService);
            java.lang.reflect.Field wcField = DocumentController.class.getDeclaredField("webClient");
            wcField.setAccessible(true);
            wcField.set(controller, webClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // uploadDocuments
    @Test
    void uploadDocuments_success() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getOriginalFilename()).thenReturn("test.pdf");
        List<MultipartFile> files = List.of(file);
        Document doc = new Document();
        doc.setId(UUID.randomUUID());
        doc.setFilename("test.pdf");
        doc.setFileType(com.lecturebot.entity.FileType.PDF);
        doc.setUploadDate(Instant.now());
        doc.setUploadStatus(com.lecturebot.entity.ProcessingStatus.COMPLETED);
        doc.setCourseId(UUID.randomUUID());
        when(documentProcessingService.processAndIndexPdf(any(), any())).thenReturn(doc);
        ResponseEntity<List<com.lecturebot.generated.model.Document>> resp = controller.uploadDocuments(doc.getCourseId().toString(), files);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals(1, resp.getBody().size());
        assertEquals("test.pdf", resp.getBody().get(0).getFilename());
    }

    @Test
    void uploadDocuments_badRequest_emptyFiles() {
        ResponseEntity<List<com.lecturebot.generated.model.Document>> resp = controller.uploadDocuments(UUID.randomUUID().toString(), Collections.emptyList());
        assertEquals(400, resp.getStatusCodeValue());
    }

    @Test
    void uploadDocuments_badRequest_nonPdf() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        List<MultipartFile> files = List.of(file);
        ResponseEntity<List<com.lecturebot.generated.model.Document>> resp = controller.uploadDocuments(UUID.randomUUID().toString(), files);
        assertEquals(400, resp.getStatusCodeValue());
    }

    @Test
    void uploadDocuments_badRequest_invalidCourseId() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");
        List<MultipartFile> files = List.of(file);
        ResponseEntity<List<com.lecturebot.generated.model.Document>> resp = controller.uploadDocuments("not-a-uuid", files);
        assertEquals(400, resp.getStatusCodeValue());
    }

    @Test
    void uploadDocuments_unprocessableEntity_noText() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");
        List<MultipartFile> files = List.of(file);
        when(documentProcessingService.processAndIndexPdf(any(), any())).thenThrow(new RuntimeException("No text could be cleaned"));
        ResponseEntity<List<com.lecturebot.generated.model.Document>> resp = controller.uploadDocuments(UUID.randomUUID().toString(), files);
        assertEquals(422, resp.getStatusCodeValue());
    }

    @Test
    void uploadDocuments_conflict_duplicate() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");
        List<MultipartFile> files = List.of(file);
        when(documentProcessingService.processAndIndexPdf(any(), any())).thenThrow(new RuntimeException("duplicate key value violates unique constraint"));
        ResponseEntity<List<com.lecturebot.generated.model.Document>> resp = controller.uploadDocuments(UUID.randomUUID().toString(), files);
        assertEquals(409, resp.getStatusCodeValue());
    }

    @Test
    void uploadDocuments_internalServerError_other() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");
        List<MultipartFile> files = List.of(file);
        when(documentProcessingService.processAndIndexPdf(any(), any())).thenThrow(new RuntimeException("other error"));
        ResponseEntity<List<com.lecturebot.generated.model.Document>> resp = controller.uploadDocuments(UUID.randomUUID().toString(), files);
        assertEquals(500, resp.getStatusCodeValue());
    }

    // listDocuments
    @Test
    void listDocuments_success() {
        UUID courseId = UUID.randomUUID();
        Document doc = new Document();
        doc.setId(UUID.randomUUID());
        doc.setFilename("test.pdf");
        doc.setFileType(com.lecturebot.entity.FileType.PDF);
        doc.setUploadDate(Instant.now());
        doc.setUploadStatus(com.lecturebot.entity.ProcessingStatus.COMPLETED);
        doc.setCourseId(courseId);
        when(documentRepository.findByCourseId(courseId)).thenReturn(List.of(doc));
        ResponseEntity<List<com.lecturebot.generated.model.Document>> resp = controller.listDocuments(courseId.toString());
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals(1, resp.getBody().size());
        assertEquals("test.pdf", resp.getBody().get(0).getFilename());
    }

    @Test
    void listDocuments_badRequest_invalidCourseId() {
        ResponseEntity<List<com.lecturebot.generated.model.Document>> resp = controller.listDocuments("not-a-uuid");
        assertEquals(400, resp.getStatusCodeValue());
    }

    // deleteDocumentById
    @Test
    void deleteDocumentById_success() {
        UUID courseId = UUID.randomUUID();
        UUID docId = UUID.randomUUID();
        Document doc = spy(new Document());
        doc.setId(docId);
        doc.setCourseId(courseId);
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));
        doReturn(courseId).when(doc).getCourseId();
        @SuppressWarnings({"unchecked", "rawtypes"})
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        @SuppressWarnings({"unchecked", "rawtypes"})
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        @SuppressWarnings({"unchecked", "rawtypes"})
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.delete()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(), any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.noContent().build()));
        ResponseEntity<Void> resp = controller.deleteDocumentById(courseId.toString(), docId.toString());
        assertEquals(204, resp.getStatusCodeValue());
        verify(documentRepository).delete(doc);
    }

    @Test
    void deleteDocumentById_notFound() {
        UUID courseId = UUID.randomUUID();
        UUID docId = UUID.randomUUID();
        when(documentRepository.findById(docId)).thenReturn(Optional.empty());
        ResponseEntity<Void> resp = controller.deleteDocumentById(courseId.toString(), docId.toString());
        assertEquals(404, resp.getStatusCodeValue());
    }

    @Test
    void deleteDocumentById_courseIdMismatch() {
        UUID courseId = UUID.randomUUID();
        UUID docId = UUID.randomUUID();
        Document doc = new Document();
        doc.setId(docId);
        doc.setCourseId(UUID.randomUUID());
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));
        ResponseEntity<Void> resp = controller.deleteDocumentById(courseId.toString(), docId.toString());
        assertEquals(404, resp.getStatusCodeValue());
    }

    @Test
    void deleteDocumentById_badRequest_invalidUUID() {
        ResponseEntity<Void> resp = controller.deleteDocumentById("not-a-uuid", "not-a-uuid");
        assertEquals(400, resp.getStatusCodeValue());
    }

    @Test
    void deleteDocumentById_internalServerError_deindexFails() {
        UUID courseId = UUID.randomUUID();
        UUID docId = UUID.randomUUID();
        Document doc = spy(new Document());
        doc.setId(docId);
        doc.setCourseId(courseId);
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));
        doReturn(courseId).when(doc).getCourseId();
        @SuppressWarnings({"unchecked", "rawtypes"})
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        @SuppressWarnings({"unchecked", "rawtypes"})
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(webClient.delete()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(), any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenThrow(new RuntimeException("deindex error"));
        ResponseEntity<Void> resp = controller.deleteDocumentById(courseId.toString(), docId.toString());
        assertEquals(500, resp.getStatusCodeValue());
        verify(documentRepository, never()).delete(doc);
    }
}
