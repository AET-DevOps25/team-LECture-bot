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
 * IndexResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-03T19:50:22.687940+03:00[Europe/Sofia]", comments = "Generator version: 7.13.0")
public class IndexResponse {

  private @Nullable String message;

  private @Nullable String documentId;

  private @Nullable String status;

  private @Nullable Integer chunksProcessed;

  private @Nullable Integer chunksStoredInWeaviate;

  public IndexResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  
  @Schema(name = "message", example = "Document indexed successfully", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public IndexResponse documentId(String documentId) {
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

  public IndexResponse status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Status of the indexing operation.
   * @return status
   */
  
  @Schema(name = "status", example = "completed", description = "Status of the indexing operation.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public IndexResponse chunksProcessed(Integer chunksProcessed) {
    this.chunksProcessed = chunksProcessed;
    return this;
  }

  /**
   * Number of text chunks processed.
   * @return chunksProcessed
   */
  
  @Schema(name = "chunks_processed", example = "5", description = "Number of text chunks processed.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("chunks_processed")
  public Integer getChunksProcessed() {
    return chunksProcessed;
  }

  public void setChunksProcessed(Integer chunksProcessed) {
    this.chunksProcessed = chunksProcessed;
  }

  public IndexResponse chunksStoredInWeaviate(Integer chunksStoredInWeaviate) {
    this.chunksStoredInWeaviate = chunksStoredInWeaviate;
    return this;
  }

  /**
   * Number of chunks successfully stored in Weaviate.
   * @return chunksStoredInWeaviate
   */
  
  @Schema(name = "chunks_stored_in_weaviate", example = "5", description = "Number of chunks successfully stored in Weaviate.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("chunks_stored_in_weaviate")
  public Integer getChunksStoredInWeaviate() {
    return chunksStoredInWeaviate;
  }

  public void setChunksStoredInWeaviate(Integer chunksStoredInWeaviate) {
    this.chunksStoredInWeaviate = chunksStoredInWeaviate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IndexResponse indexResponse = (IndexResponse) o;
    return Objects.equals(this.message, indexResponse.message) &&
        Objects.equals(this.documentId, indexResponse.documentId) &&
        Objects.equals(this.status, indexResponse.status) &&
        Objects.equals(this.chunksProcessed, indexResponse.chunksProcessed) &&
        Objects.equals(this.chunksStoredInWeaviate, indexResponse.chunksStoredInWeaviate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, documentId, status, chunksProcessed, chunksStoredInWeaviate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndexResponse {\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    documentId: ").append(toIndentedString(documentId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    chunksProcessed: ").append(toIndentedString(chunksProcessed)).append("\n");
    sb.append("    chunksStoredInWeaviate: ").append(toIndentedString(chunksStoredInWeaviate)).append("\n");
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

