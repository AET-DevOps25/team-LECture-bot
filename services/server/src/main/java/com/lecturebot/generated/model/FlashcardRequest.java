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
 * FlashcardRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-06-25T17:57:04.683390+03:00[Europe/Sofia]", comments = "Generator version: 7.13.0")
public class FlashcardRequest {

  private @Nullable String corseSpaceId;

  private @Nullable String documentId = null;

  public FlashcardRequest corseSpaceId(String corseSpaceId) {
    this.corseSpaceId = corseSpaceId;
    return this;
  }

  /**
   * Get corseSpaceId
   * @return corseSpaceId
   */
  
  @Schema(name = "corse_space_id", example = "cs-456", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("corse_space_id")
  public String getCorseSpaceId() {
    return corseSpaceId;
  }

  public void setCorseSpaceId(String corseSpaceId) {
    this.corseSpaceId = corseSpaceId;
  }

  public FlashcardRequest documentId(String documentId) {
    this.documentId = documentId;
    return this;
  }

  /**
   * Get documentId
   * @return documentId
   */
  
  @Schema(name = "document_id", example = "doc-123", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("document_id")
  public String getDocumentId() {
    return documentId;
  }

  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FlashcardRequest flashcardRequest = (FlashcardRequest) o;
    return Objects.equals(this.corseSpaceId, flashcardRequest.corseSpaceId) &&
        Objects.equals(this.documentId, flashcardRequest.documentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(corseSpaceId, documentId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FlashcardRequest {\n");
    sb.append("    corseSpaceId: ").append(toIndentedString(corseSpaceId)).append("\n");
    sb.append("    documentId: ").append(toIndentedString(documentId)).append("\n");
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

