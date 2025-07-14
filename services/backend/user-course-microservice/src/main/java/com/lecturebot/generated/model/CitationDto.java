package com.lecturebot.generated.model;

import java.util.UUID;

/**
 * CitationDto represents a citation for a Q&A answer.
 */
public class CitationDto {
    private UUID documentId;
    private String documentName;
    private Integer pageNumber;
    private String contextSnippet;

    public UUID getDocumentId() {
        return documentId;
    }
    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }
    public String getDocumentName() {
        return documentName;
    }
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    public Integer getPageNumber() {
        return pageNumber;
    }
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
    public String getContextSnippet() {
        return contextSnippet;
    }
    public void setContextSnippet(String contextSnippet) {
        this.contextSnippet = contextSnippet;
    }
}
