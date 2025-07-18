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
 * Citation
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-16T11:24:17.030254+03:00[Europe/Sofia]", comments = "Generator version: 7.13.0")
public class Citation {

  private String documentId;

  private String chunkId;

  private @Nullable String documentName = null;

  private String retrievedTextPreview;

  public Citation() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Citation(String documentId, String chunkId, String retrievedTextPreview) {
    this.documentId = documentId;
    this.chunkId = chunkId;
    this.retrievedTextPreview = retrievedTextPreview;
  }

  public Citation documentId(String documentId) {
    this.documentId = documentId;
    return this;
  }

  /**
   * The ID of the document from which the citation was retrieved.
   * @return documentId
   */
  @NotNull 
  @Schema(name = "document_id", example = "doc-123", description = "The ID of the document from which the citation was retrieved.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("document_id")
  public String getDocumentId() {
    return documentId;
  }

  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }

  public Citation chunkId(String chunkId) {
    this.chunkId = chunkId;
    return this;
  }

  /**
   * A unique identifier for the specific chunk within the document (e.g., chunk index, page number).
   * @return chunkId
   */
  @NotNull 
  @Schema(name = "chunk_id", example = "0", description = "A unique identifier for the specific chunk within the document (e.g., chunk index, page number).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("chunk_id")
  public String getChunkId() {
    return chunkId;
  }

  public void setChunkId(String chunkId) {
    this.chunkId = chunkId;
  }

  public Citation documentName(String documentName) {
    this.documentName = documentName;
    return this;
  }

  /**
   * The name or title of the source document.
   * @return documentName
   */
  
  @Schema(name = "document_name", example = "Lecture Slides Week 5", description = "The name or title of the source document.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("document_name")
  public String getDocumentName() {
    return documentName;
  }

  public void setDocumentName(String documentName) {
    this.documentName = documentName;
  }

  public Citation retrievedTextPreview(String retrievedTextPreview) {
    this.retrievedTextPreview = retrievedTextPreview;
    return this;
  }

  /**
   * A short snippet of the text that was retrieved and used as context.
   * @return retrievedTextPreview
   */
  @NotNull 
  @Schema(name = "retrieved_text_preview", example = "LangChain is a framework for developing applications powered by language models.", description = "A short snippet of the text that was retrieved and used as context.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("retrieved_text_preview")
  public String getRetrievedTextPreview() {
    return retrievedTextPreview;
  }

  public void setRetrievedTextPreview(String retrievedTextPreview) {
    this.retrievedTextPreview = retrievedTextPreview;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Citation citation = (Citation) o;
    return Objects.equals(this.documentId, citation.documentId) &&
        Objects.equals(this.chunkId, citation.chunkId) &&
        Objects.equals(this.documentName, citation.documentName) &&
        Objects.equals(this.retrievedTextPreview, citation.retrievedTextPreview);
  }

  @Override
  public int hashCode() {
    return Objects.hash(documentId, chunkId, documentName, retrievedTextPreview);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Citation {\n");
    sb.append("    documentId: ").append(toIndentedString(documentId)).append("\n");
    sb.append("    chunkId: ").append(toIndentedString(chunkId)).append("\n");
    sb.append("    documentName: ").append(toIndentedString(documentName)).append("\n");
    sb.append("    retrievedTextPreview: ").append(toIndentedString(retrievedTextPreview)).append("\n");
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

