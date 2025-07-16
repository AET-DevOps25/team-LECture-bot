package com.lecturebot.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lecturebot.generated.model.Citation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * QueryResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-16T17:05:02.617957300+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
public class QueryResponse {

  private @Nullable String answer;

  @Valid
  private List<@Valid Citation> citations = new ArrayList<>();

  public QueryResponse answer(String answer) {
    this.answer = answer;
    return this;
  }

  /**
   * Get answer
   * @return answer
   */
  
  @Schema(name = "answer", example = "This document is about...", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("answer")
  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public QueryResponse citations(List<@Valid Citation> citations) {
    this.citations = citations;
    return this;
  }

  public QueryResponse addCitationsItem(Citation citationsItem) {
    if (this.citations == null) {
      this.citations = new ArrayList<>();
    }
    this.citations.add(citationsItem);
    return this;
  }

  /**
   * Get citations
   * @return citations
   */
  @Valid 
  @Schema(name = "citations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("citations")
  public List<@Valid Citation> getCitations() {
    return citations;
  }

  public void setCitations(List<@Valid Citation> citations) {
    this.citations = citations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QueryResponse queryResponse = (QueryResponse) o;
    return Objects.equals(this.answer, queryResponse.answer) &&
        Objects.equals(this.citations, queryResponse.citations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(answer, citations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QueryResponse {\n");
    sb.append("    answer: ").append(toIndentedString(answer)).append("\n");
    sb.append("    citations: ").append(toIndentedString(citations)).append("\n");
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

