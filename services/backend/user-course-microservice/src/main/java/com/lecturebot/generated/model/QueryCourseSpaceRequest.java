package com.lecturebot.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * QueryCourseSpaceRequest
 */

@JsonTypeName("queryCourseSpace_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-20T14:29:55.417633+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
public class QueryCourseSpaceRequest {

  private String question;

  public QueryCourseSpaceRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public QueryCourseSpaceRequest(String question) {
    this.question = question;
  }

  public QueryCourseSpaceRequest question(String question) {
    this.question = question;
    return this;
  }

  /**
   * The question to ask about the course space.
   * @return question
   */
  @NotNull 
  @Schema(name = "question", description = "The question to ask about the course space.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("question")
  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryCourseSpaceRequest queryCourseSpaceRequest = (QueryCourseSpaceRequest) o;
    return Objects.equals(this.question, queryCourseSpaceRequest.question);
  }

  @Override
  public int hashCode() {
    return Objects.hash(question);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryCourseSpaceRequest {\n");
    sb.append("    question: ").append(toIndentedString(question)).append("\n");
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

