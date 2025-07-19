package com.lecturebot.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lecturebot.generated.model.FlashcardsForDocument;
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
 * FlashcardResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-20T00:42:32.342649+03:00[Europe/Sofia]", comments = "Generator version: 7.13.0")
public class FlashcardResponse {

  private @Nullable String courseSpaceId;

  @Valid
  private List<@Valid FlashcardsForDocument> flashcards = new ArrayList<>();

  private @Nullable String error = null;

  public FlashcardResponse courseSpaceId(String courseSpaceId) {
    this.courseSpaceId = courseSpaceId;
    return this;
  }

  /**
   * Get courseSpaceId
   * @return courseSpaceId
   */
  
  @Schema(name = "course_space_id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("course_space_id")
  public String getCourseSpaceId() {
    return courseSpaceId;
  }

  public void setCourseSpaceId(String courseSpaceId) {
    this.courseSpaceId = courseSpaceId;
  }

  public FlashcardResponse flashcards(List<@Valid FlashcardsForDocument> flashcards) {
    this.flashcards = flashcards;
    return this;
  }

  public FlashcardResponse addFlashcardsItem(FlashcardsForDocument flashcardsItem) {
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
  public List<@Valid FlashcardsForDocument> getFlashcards() {
    return flashcards;
  }

  public void setFlashcards(List<@Valid FlashcardsForDocument> flashcards) {
    this.flashcards = flashcards;
  }

  public FlashcardResponse error(String error) {
    this.error = error;
    return this;
  }

  /**
   * An error message if the flashcard generation failed.
   * @return error
   */
  
  @Schema(name = "error", example = "Failed to retrieve documents from the vector store.", description = "An error message if the flashcard generation failed.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("error")
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FlashcardResponse flashcardResponse = (FlashcardResponse) o;
    return Objects.equals(this.courseSpaceId, flashcardResponse.courseSpaceId) &&
        Objects.equals(this.flashcards, flashcardResponse.flashcards) &&
        Objects.equals(this.error, flashcardResponse.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(courseSpaceId, flashcards, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FlashcardResponse {\n");
    sb.append("    courseSpaceId: ").append(toIndentedString(courseSpaceId)).append("\n");
    sb.append("    flashcards: ").append(toIndentedString(flashcards)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
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

