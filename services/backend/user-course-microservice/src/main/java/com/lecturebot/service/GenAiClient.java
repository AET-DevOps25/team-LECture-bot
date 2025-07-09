package com.lecturebot.service;

import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResultDto;
import org.springframework.stereotype.Component;

@Component
public class GenAiClient {
    public QueryResultDto submitQuery(QueryRequest request) {
        // Dummy implementation for now
        QueryResultDto result = new QueryResultDto();
        result.setAnswerText("This is a sample answer about your course materials. See the citation below (from GenAiClient).\n");

        com.lecturebot.generated.model.CitationDto citation = new com.lecturebot.generated.model.CitationDto();
        citation.setDocumentId(java.util.UUID.randomUUID());
        citation.setDocumentName("SampleDocument.pdf");
        citation.setPageNumber(3);
        citation.setContextSnippet("This is a relevant snippet from the document used to answer your question (from GenAiClient).\n");

        java.util.List<com.lecturebot.generated.model.CitationDto> citations = new java.util.ArrayList<>();
        citations.add(citation);
        result.setCitations(citations);
        result.setError(null);
        return result;
    }
}
