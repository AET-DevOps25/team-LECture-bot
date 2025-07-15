package com.lecturebot.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lecturebot.generated.model.UserProfile;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateUserProfileResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-14T23:17:00.952256+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
public class UpdateUserProfileResponse {

  private @Nullable UserProfile userProfile;

  private @Nullable Boolean requireReauth;

  private @Nullable String message;

  public UpdateUserProfileResponse userProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
    return this;
  }

  /**
   * Get userProfile
   * @return userProfile
   */
  @Valid 
  @Schema(name = "userProfile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userProfile")
  public UserProfile getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(UserProfile userProfile) {
    this.userProfile = userProfile;
  }

  public UpdateUserProfileResponse requireReauth(Boolean requireReauth) {
    this.requireReauth = requireReauth;
    return this;
  }

  /**
   * Get requireReauth
   * @return requireReauth
   */
  
  @Schema(name = "require_reauth", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("require_reauth")
  public Boolean getRequireReauth() {
    return requireReauth;
  }

  public void setRequireReauth(Boolean requireReauth) {
    this.requireReauth = requireReauth;
  }

  public UpdateUserProfileResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  
  @Schema(name = "message", example = "Profile updated successfully.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateUserProfileResponse updateUserProfileResponse = (UpdateUserProfileResponse) o;
    return Objects.equals(this.userProfile, updateUserProfileResponse.userProfile) &&
        Objects.equals(this.requireReauth, updateUserProfileResponse.requireReauth) &&
        Objects.equals(this.message, updateUserProfileResponse.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userProfile, requireReauth, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateUserProfileResponse {\n");
    sb.append("    userProfile: ").append(toIndentedString(userProfile)).append("\n");
    sb.append("    requireReauth: ").append(toIndentedString(requireReauth)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

