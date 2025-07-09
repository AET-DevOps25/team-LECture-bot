package com.lecturebot.controller;

import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResultDto;
import com.lecturebot.service.QueryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/coursespaces/{courseSpaceId}/query")
public class QueryController {

    private final QueryService queryService;

    @Autowired
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QueryResultDto> queryCourseSpace(
            @PathVariable("courseSpaceId") String courseSpaceId,
            @Valid @RequestBody QueryRequest request
    ) {
        // Set courseId from path if not set
        try {
            if (request.getCourseId() == null && courseSpaceId != null) {
                request.setCourseId(java.util.UUID.fromString(courseSpaceId));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }

        // Set userId from security context if available
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            // If your userId is the username or can be mapped, set it here. Otherwise, leave as is.
            // request.setUserId(...);
        }

        QueryResultDto result = queryService.processQuery(request);
        return ResponseEntity.ok(result);
    }
}
