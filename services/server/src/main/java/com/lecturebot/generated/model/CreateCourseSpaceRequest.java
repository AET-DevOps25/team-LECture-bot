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
 * CreateCourseSpaceRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-08T22:55:56.606033+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
public class CreateCourseSpaceRequest {

  private String title;

  public CreateCourseSpaceRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateCourseSpaceRequest(String title) {
    this.title = title;
  }

  public CreateCourseSpaceRequest title(String title) {
    this.title = title;
    return this;
  }

  /**
   * The title of the course space to create.
   * @return title
   */
  @NotNull 
  @Schema(name = "title", example = "Introduction to AI", description = "The title of the course space to create.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateCourseSpaceRequest createCourseSpaceRequest = (CreateCourseSpaceRequest) o;
    return Objects.equals(this.title, createCourseSpaceRequest.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateCourseSpaceRequest {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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

