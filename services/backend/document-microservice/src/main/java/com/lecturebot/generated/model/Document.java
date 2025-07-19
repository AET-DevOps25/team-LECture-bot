package com.lecturebot.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Document
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-19T11:17:28.002982+03:00[Europe/Sofia]", comments = "Generator version: 7.13.0")
public class Document {

  private @Nullable String id;

  private @Nullable String filename;

  /**
   * Gets or Sets fileType
   */
  public enum FileTypeEnum {
    PDF("PDF");

    private final String value;

    FileTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static FileTypeEnum fromValue(String value) {
      for (FileTypeEnum b : FileTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable FileTypeEnum fileType;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime uploadDate;

  private @Nullable String courseId;

  /**
   * Gets or Sets processingStatus
   */
  public enum ProcessingStatusEnum {
    PENDING("PENDING"),
    
    PROCESSING_EXTRACTION("PROCESSING_EXTRACTION"),
    
    PROCESSING_INDEXING("PROCESSING_INDEXING"),
    
    COMPLETED("COMPLETED"),
    
    FAILED("FAILED");

    private final String value;

    ProcessingStatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ProcessingStatusEnum fromValue(String value) {
      for (ProcessingStatusEnum b : ProcessingStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable ProcessingStatusEnum processingStatus;

  public Document id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Document filename(String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * Get filename
   * @return filename
   */
  
  @Schema(name = "filename", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("filename")
  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public Document fileType(FileTypeEnum fileType) {
    this.fileType = fileType;
    return this;
  }

  /**
   * Get fileType
   * @return fileType
   */
  
  @Schema(name = "fileType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fileType")
  public FileTypeEnum getFileType() {
    return fileType;
  }

  public void setFileType(FileTypeEnum fileType) {
    this.fileType = fileType;
  }

  public Document uploadDate(OffsetDateTime uploadDate) {
    this.uploadDate = uploadDate;
    return this;
  }

  /**
   * Get uploadDate
   * @return uploadDate
   */
  @Valid 
  @Schema(name = "uploadDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("uploadDate")
  public OffsetDateTime getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(OffsetDateTime uploadDate) {
    this.uploadDate = uploadDate;
  }

  public Document courseId(String courseId) {
    this.courseId = courseId;
    return this;
  }

  /**
   * Get courseId
   * @return courseId
   */
  
  @Schema(name = "courseId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("courseId")
  public String getCourseId() {
    return courseId;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public Document processingStatus(ProcessingStatusEnum processingStatus) {
    this.processingStatus = processingStatus;
    return this;
  }

  /**
   * Get processingStatus
   * @return processingStatus
   */
  
  @Schema(name = "processingStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("processingStatus")
  public ProcessingStatusEnum getProcessingStatus() {
    return processingStatus;
  }

  public void setProcessingStatus(ProcessingStatusEnum processingStatus) {
    this.processingStatus = processingStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Document document = (Document) o;
    return Objects.equals(this.id, document.id) &&
        Objects.equals(this.filename, document.filename) &&
        Objects.equals(this.fileType, document.fileType) &&
        Objects.equals(this.uploadDate, document.uploadDate) &&
        Objects.equals(this.courseId, document.courseId) &&
        Objects.equals(this.processingStatus, document.processingStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, filename, fileType, uploadDate, courseId, processingStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Document {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    fileType: ").append(toIndentedString(fileType)).append("\n");
    sb.append("    uploadDate: ").append(toIndentedString(uploadDate)).append("\n");
    sb.append("    courseId: ").append(toIndentedString(courseId)).append("\n");
    sb.append("    processingStatus: ").append(toIndentedString(processingStatus)).append("\n");
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

