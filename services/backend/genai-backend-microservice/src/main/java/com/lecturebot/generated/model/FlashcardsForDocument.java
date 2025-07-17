package com.lecturebot.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lecturebot.generated.model.Flashcard;
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
 * FlashcardsForDocument
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-17T20:20:37.654505500+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
public class FlashcardsForDocument {

  private @Nullable String documentId;

  @Valid
  private List<@Valid Flashcard> flashcards = new ArrayList<>();

  public FlashcardsForDocument documentId(String documentId) {
    this.documentId = documentId;
    return this;
  }

  /**
   * Get documentId
   * @return documentId
   */
  
  @Schema(name = "document_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("document_id")
  public String getDocumentId() {
    return documentId;
  }

  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }

  public FlashcardsForDocument flashcards(List<@Valid Flashcard> flashcards) {
    this.flashcards = flashcards;
    return this;
  }

  public FlashcardsForDocument addFlashcardsItem(Flashcard flashcardsItem) {
    if (this.flashcards == null) {
      this.flashcards = new ArrayList<>();
    }
    this.flashcards.add(flashcardsItem);
    return this;
  }

  /**
   * Get flashcards
   * @return flashcards
   */
  @Valid 
  @Schema(name = "flashcards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("flashcards")
  public List<@Valid Flashcard> getFlashcards() {
    return flashcards;
  }

  public void setFlashcards(List<@Valid Flashcard> flashcards) {
    this.flashcards = flashcards;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FlashcardsForDocument flashcardsForDocument = (FlashcardsForDocument) o;
    return Objects.equals(this.documentId, flashcardsForDocument.documentId) &&
        Objects.equals(this.flashcards, flashcardsForDocument.flashcards);
  }

  @Override
  public int hashCode() {
    return Objects.hash(documentId, flashcards);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FlashcardsForDocument {\n");
    sb.append("    documentId: ").append(toIndentedString(documentId)).append("\n");
    sb.append("    flashcards: ").append(toIndentedString(flashcards)).append("\n");
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

