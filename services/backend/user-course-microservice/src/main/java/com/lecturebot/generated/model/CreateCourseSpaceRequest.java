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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-17T20:20:31.430293300+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
public class CreateCourseSpaceRequest {

  private String name;

  private @Nullable String description;

  public CreateCourseSpaceRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateCourseSpaceRequest(String name) {
    this.name = name;
  }

  public CreateCourseSpaceRequest name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the course space to create.
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "Introduction to AI", description = "The name of the course space to create.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CreateCourseSpaceRequest description(String description) {
    this.description = description;
    return this;
  }

  /**
   * The description of the course space.
   * @return description
   */
  
  @Schema(name = "description", example = "This is a course about AI.", description = "The description of the course space.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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
    return Objects.equals(this.name, createCourseSpaceRequest.name) &&
        Objects.equals(this.description, createCourseSpaceRequest.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateCourseSpaceRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

