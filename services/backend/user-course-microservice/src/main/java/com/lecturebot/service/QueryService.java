package com.lecturebot.service;

import com.lecturebot.generated.model.QueryRequest;
import com.lecturebot.generated.model.QueryResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryService {
    private final GenAiClient genAiClient;

    @Autowired
    public QueryService(GenAiClient genAiClient) {
        this.genAiClient = genAiClient;
    }

    public QueryResultDto processQuery(QueryRequest request) {
        // Forward to GenAiClient (dummy for now)
        return genAiClient.submitQuery(request);
    }
}
