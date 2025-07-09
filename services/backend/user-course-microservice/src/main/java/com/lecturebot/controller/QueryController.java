package com.lecturebot.controller;

import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResultDto;
import com.lecturebot.service.QueryService;
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
    public ResponseEntity<QueryResultDto> queryCourseSpace(
            @PathVariable("courseSpaceId") String courseSpaceId,
            @Valid @RequestBody QueryRequest request
    ) {
        // Set courseId from path if not set
        // (Assumes QueryRequest.courseId is UUID)
        // Optionally, validate userId from security context
        // request.setUserId(...);
        // request.setCourseId(UUID.fromString(courseSpaceId));
        QueryResultDto result = queryService.processQuery(request);
        return ResponseEntity.ok(result);
    }
}
