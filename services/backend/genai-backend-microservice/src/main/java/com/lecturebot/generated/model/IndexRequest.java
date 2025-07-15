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
 * IndexRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-14T23:17:08.584537+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
public class IndexRequest {

  private String documentId;

  private String courseSpaceId;

  private String textContent;

  public IndexRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public IndexRequest(String documentId, String courseSpaceId, String textContent) {
    this.documentId = documentId;
    this.courseSpaceId = courseSpaceId;
    this.textContent = textContent;
  }

  public IndexRequest documentId(String documentId) {
    this.documentId = documentId;
    return this;
  }

  /**
   * Get documentId
   * @return documentId
   */
  @NotNull 
  @Schema(name = "document_id", example = "doc-123", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("document_id")
  public String getDocumentId() {
    return documentId;
  }

  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }

  public IndexRequest courseSpaceId(String courseSpaceId) {
    this.courseSpaceId = courseSpaceId;
    return this;
  }

  /**
   * Get courseSpaceId
   * @return courseSpaceId
   */
  @NotNull 
  @Schema(name = "course_space_id", example = "cs-456", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("course_space_id")
  public String getCourseSpaceId() {
    return courseSpaceId;
  }

  public void setCourseSpaceId(String courseSpaceId) {
    this.courseSpaceId = courseSpaceId;
  }

  public IndexRequest textContent(String textContent) {
    this.textContent = textContent;
    return this;
  }

  /**
   * Get textContent
   * @return textContent
   */
  @NotNull 
  @Schema(name = "text_content", example = "This is the content of the document.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("text_content")
  public String getTextContent() {
    return textContent;
  }

  public void setTextContent(String textContent) {
    this.textContent = textContent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IndexRequest indexRequest = (IndexRequest) o;
    return Objects.equals(this.documentId, indexRequest.documentId) &&
        Objects.equals(this.courseSpaceId, indexRequest.courseSpaceId) &&
        Objects.equals(this.textContent, indexRequest.textContent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(documentId, courseSpaceId, textContent);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndexRequest {\n");
    sb.append("    documentId: ").append(toIndentedString(documentId)).append("\n");
    sb.append("    courseSpaceId: ").append(toIndentedString(courseSpaceId)).append("\n");
    sb.append("    textContent: ").append(toIndentedString(textContent)).append("\n");
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

