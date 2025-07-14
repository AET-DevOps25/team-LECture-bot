package com.lecturebot.generated.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * QueryRequest DTO for Q&A queries.
 */


import jakarta.validation.constraints.NotBlank;

public class QueryRequest {
    private Long userId;
    private UUID courseId;
    @JsonProperty("queryText")
    @NotBlank(message = "queryText must not be blank")
    private String queryText;
    private QueryType type;

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public UUID getCourseId() {
        return courseId;
    }
    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }
    public String getQueryText() {
        return queryText;
    }
    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
    public QueryType getType() {
        return type;
    }
    public void setType(QueryType type) {
        this.type = type;
    }
}
