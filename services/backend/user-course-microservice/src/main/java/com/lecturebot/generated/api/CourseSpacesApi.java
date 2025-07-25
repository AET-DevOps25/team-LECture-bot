/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (7.13.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.lecturebot.generated.api;

import com.lecturebot.generated.model.CourseSpaceDto;
import com.lecturebot.generated.model.CreateCourseSpaceRequest;
import com.lecturebot.generated.model.QueryCourseSpace200Response;
import com.lecturebot.generated.model.QueryCourseSpaceRequest;
import java.util.UUID;
import com.lecturebot.generated.model.UpdateCourseSpaceRequest;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-20T14:29:55.417633+02:00[Europe/Berlin]", comments = "Generator version: 7.13.0")
@Validated
@Tag(name = "CourseSpaces", description = "the CourseSpaces API")
// @RequestMapping("/api/v1")
public interface CourseSpacesApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /coursespaces : Create a new course space
     * Creates a new course space for the authenticated user.
     *
     * @param createCourseSpaceRequest The details of the course space to create. (required)
     * @return Course space created successfully. (status code 201)
     *         or Bad Request (e.g., validation error). (status code 400)
     *         or Unauthorized. (status code 401)
     */
    @Operation(
        operationId = "createCourseSpace",
        summary = "Create a new course space",
        description = "Creates a new course space for the authenticated user.",
        tags = { "CourseSpaces" },
        responses = {
            @ApiResponse(responseCode = "201", description = "Course space created successfully.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CourseSpaceDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request (e.g., validation error)."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/coursespaces",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    
    default ResponseEntity<CourseSpaceDto> createCourseSpace(
        @Parameter(name = "CreateCourseSpaceRequest", description = "The details of the course space to create.", required = true) @Valid @RequestBody CreateCourseSpaceRequest createCourseSpaceRequest
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"owner\" : { \"name\" : \"Ada Lovelace\", \"id\" : 0, \"email\" : \"ada.lovelace@example.com\" }, \"updated_at\" : \"2023-10-01T12:00:00Z\", \"name\" : \"Introduction to AI\", \"description\" : \"This is a course about AI.\", \"created_at\" : \"2023-10-01T12:00:00Z\", \"id\" : \"123e4567-e89b-12d3-a456-426614174000\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /coursespaces/{courseSpaceId} : Delete a course space
     * Permanently deletes a course space by its ID.
     *
     * @param courseSpaceId The ID of the course space to delete. (required)
     * @return Course space deleted successfully. (status code 204)
     *         or Bad Request (e.g., invalid ID). (status code 400)
     *         or Unauthorized. (status code 401)
     *         or Course space not found. (status code 404)
     */
    @Operation(
        operationId = "deleteCourseSpace",
        summary = "Delete a course space",
        description = "Permanently deletes a course space by its ID.",
        tags = { "CourseSpaces" },
        responses = {
            @ApiResponse(responseCode = "204", description = "Course space deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Bad Request (e.g., invalid ID)."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "404", description = "Course space not found.")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/coursespaces/{courseSpaceId}"
    )
    
    default ResponseEntity<Void> deleteCourseSpace(
        @Parameter(name = "courseSpaceId", description = "The ID of the course space to delete.", required = true, in = ParameterIn.PATH) @PathVariable("courseSpaceId") UUID courseSpaceId
    ) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /coursespaces/{courseSpaceId} : Get a specific CourseSpace
     * Gets the information for a chose CourseSpace
     *
     * @param courseSpaceId The ID of the course space to get (required)
     * @return Course Space fetched successfully. (status code 200)
     *         or Bad Request (e.g., invalid ID). (status code 400)
     *         or Unauthorized. (status code 401)
     *         or Course space not found. (status code 404)
     */
    @Operation(
        operationId = "getCourseSpace",
        summary = "Get a specific CourseSpace",
        description = "Gets the information for a chose CourseSpace",
        tags = { "CourseSpaces" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Course Space fetched successfully.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CourseSpaceDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request (e.g., invalid ID)."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "404", description = "Course space not found.")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/coursespaces/{courseSpaceId}",
        produces = { "application/json" }
    )
    
    default ResponseEntity<CourseSpaceDto> getCourseSpace(
        @Parameter(name = "courseSpaceId", description = "The ID of the course space to get", required = true, in = ParameterIn.PATH) @PathVariable("courseSpaceId") UUID courseSpaceId
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"owner\" : { \"name\" : \"Ada Lovelace\", \"id\" : 0, \"email\" : \"ada.lovelace@example.com\" }, \"updated_at\" : \"2023-10-01T12:00:00Z\", \"name\" : \"Introduction to AI\", \"description\" : \"This is a course about AI.\", \"created_at\" : \"2023-10-01T12:00:00Z\", \"id\" : \"123e4567-e89b-12d3-a456-426614174000\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /coursespaces : Get all course spaces for the current user
     * Retrieves a list of all course spaces associated with the authenticated user.
     *
     * @return A list of the user&#39;s course spaces. (status code 200)
     *         or Unauthorized. (status code 401)
     */
    @Operation(
        operationId = "getCourseSpaces",
        summary = "Get all course spaces for the current user",
        description = "Retrieves a list of all course spaces associated with the authenticated user.",
        tags = { "CourseSpaces" },
        responses = {
            @ApiResponse(responseCode = "200", description = "A list of the user's course spaces.", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseSpaceDto.class)))
            }),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/coursespaces",
        produces = { "application/json" }
    )
    
    default ResponseEntity<List<CourseSpaceDto>> getCourseSpaces(
        
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"owner\" : { \"name\" : \"Ada Lovelace\", \"id\" : 0, \"email\" : \"ada.lovelace@example.com\" }, \"updated_at\" : \"2023-10-01T12:00:00Z\", \"name\" : \"Introduction to AI\", \"description\" : \"This is a course about AI.\", \"created_at\" : \"2023-10-01T12:00:00Z\", \"id\" : \"123e4567-e89b-12d3-a456-426614174000\" }, { \"owner\" : { \"name\" : \"Ada Lovelace\", \"id\" : 0, \"email\" : \"ada.lovelace@example.com\" }, \"updated_at\" : \"2023-10-01T12:00:00Z\", \"name\" : \"Introduction to AI\", \"description\" : \"This is a course about AI.\", \"created_at\" : \"2023-10-01T12:00:00Z\", \"id\" : \"123e4567-e89b-12d3-a456-426614174000\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * POST /coursespaces/{courseSpaceId}/query : Query a course space (Q&amp;A)
     * Submits a natural language question for a specific course space and returns the answer.
     *
     * @param courseSpaceId The ID of the course space to query. (required)
     * @param queryCourseSpaceRequest  (required)
     * @return Query processed successfully. (status code 200)
     *         or Bad request (missing or invalid question) (status code 400)
     *         or Course space not found (status code 404)
     *         or Internal server error (status code 500)
     */
    @Operation(
        operationId = "queryCourseSpace",
        summary = "Query a course space (Q&A)",
        description = "Submits a natural language question for a specific course space and returns the answer.",
        tags = { "CourseSpaces" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Query processed successfully.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = QueryCourseSpace200Response.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request (missing or invalid question)"),
            @ApiResponse(responseCode = "404", description = "Course space not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/coursespaces/{courseSpaceId}/query",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    
    default ResponseEntity<QueryCourseSpace200Response> queryCourseSpace(
        @Parameter(name = "courseSpaceId", description = "The ID of the course space to query.", required = true, in = ParameterIn.PATH) @PathVariable("courseSpaceId") String courseSpaceId,
        @Parameter(name = "QueryCourseSpaceRequest", description = "", required = true) @Valid @RequestBody QueryCourseSpaceRequest queryCourseSpaceRequest
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"citations\" : [ { \"document_name\" : \"document_name\", \"retrieved_text_preview\" : \"retrieved_text_preview\", \"document_id\" : \"document_id\", \"chunk_id\" : \"chunk_id\" }, { \"document_name\" : \"document_name\", \"retrieved_text_preview\" : \"retrieved_text_preview\", \"document_id\" : \"document_id\", \"chunk_id\" : \"chunk_id\" } ], \"answer\" : \"answer\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /coursespaces/{courseSpaceId} : Update a course space
     * Updates the name and/or description of a course space owned by the authenticated user.
     *
     * @param courseSpaceId The ID of the course space to update. (required)
     * @param updateCourseSpaceRequest The new details for the course space. (required)
     * @return Course space updated successfully. (status code 200)
     *         or Bad Request (e.g., validation error). (status code 400)
     *         or Unauthorized. (status code 401)
     *         or Course space not found. (status code 404)
     */
    @Operation(
        operationId = "updateCourseSpace",
        summary = "Update a course space",
        description = "Updates the name and/or description of a course space owned by the authenticated user.",
        tags = { "CourseSpaces" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Course space updated successfully.", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = CourseSpaceDto.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request (e.g., validation error)."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "404", description = "Course space not found.")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/coursespaces/{courseSpaceId}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    
    default ResponseEntity<CourseSpaceDto> updateCourseSpace(
        @Parameter(name = "courseSpaceId", description = "The ID of the course space to update.", required = true, in = ParameterIn.PATH) @PathVariable("courseSpaceId") UUID courseSpaceId,
        @Parameter(name = "UpdateCourseSpaceRequest", description = "The new details for the course space.", required = true) @Valid @RequestBody UpdateCourseSpaceRequest updateCourseSpaceRequest
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"owner\" : { \"name\" : \"Ada Lovelace\", \"id\" : 0, \"email\" : \"ada.lovelace@example.com\" }, \"updated_at\" : \"2023-10-01T12:00:00Z\", \"name\" : \"Introduction to AI\", \"description\" : \"This is a course about AI.\", \"created_at\" : \"2023-10-01T12:00:00Z\", \"id\" : \"123e4567-e89b-12d3-a456-426614174000\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
