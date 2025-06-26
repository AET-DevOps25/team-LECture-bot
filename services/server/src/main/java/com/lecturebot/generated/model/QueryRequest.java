package com.lecturebot.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * QueryRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-06-27T00:14:59.690014+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
public class QueryRequest {

  private String queryText;

  private String courseSpaceId;

  public QueryRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public QueryRequest(String queryText, String courseSpaceId) {
    this.queryText = queryText;
    this.courseSpaceId = courseSpaceId;
  }

  public QueryRequest queryText(String queryText) {
    this.queryText = queryText;
    return this;
  }

  /**
   * Get queryText
   * @return queryText
   */
  @NotNull 
  @Schema(name = "queryText", example = "What is this document about?", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("queryText")
  public String getQueryText() {
    return queryText;
  }

  public void setQueryText(String queryText) {
    this.queryText = queryText;
  }

  public QueryRequest courseSpaceId(String courseSpaceId) {
    this.courseSpaceId = courseSpaceId;
    return this;
  }

  /**
   * Get courseSpaceId
   * @return courseSpaceId
   */
  @NotNull 
  @Schema(name = "courseSpaceId", example = "cs-456", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("courseSpaceId")
  public String getCourseSpaceId() {
    return courseSpaceId;
  }

  public void setCourseSpaceId(String courseSpaceId) {
    this.courseSpaceId = courseSpaceId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryRequest queryRequest = (QueryRequest) o;
    return Objects.equals(this.queryText, queryRequest.queryText) &&
        Objects.equals(this.courseSpaceId, queryRequest.courseSpaceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryText, courseSpaceId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryRequest {\n");
    sb.append("    queryText: ").append(toIndentedString(queryText)).append("\n");
    sb.append("    courseSpaceId: ").append(toIndentedString(courseSpaceId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

