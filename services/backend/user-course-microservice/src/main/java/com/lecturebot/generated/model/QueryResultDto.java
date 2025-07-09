package com.lecturebot.generated.model;

import java.util.List;

/**
 * QueryResultDto represents the result of a Q&A query.
 */
public class QueryResultDto {
    private String answerText;
    private List<CitationDto> citations;
    private String error;

    public String getAnswerText() {
        return answerText;
    }
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    public List<CitationDto> getCitations() {
        return citations;
    }
    public void setCitations(List<CitationDto> citations) {
        this.citations = citations;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
