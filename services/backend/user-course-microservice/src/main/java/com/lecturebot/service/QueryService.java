package com.lecturebot.service;

import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResultDto;
import org.springframework.stereotype.Service;

@Service
public class QueryService {
    public QueryResultDto processQuery(QueryRequest request) {
        // Dummy implementation for now
        QueryResultDto result = new QueryResultDto();
        result.setAnswerText("This is a sample answer.");
        result.setCitations(null); // Or provide a sample list
        result.setError(null);
        return result;
    }
}
