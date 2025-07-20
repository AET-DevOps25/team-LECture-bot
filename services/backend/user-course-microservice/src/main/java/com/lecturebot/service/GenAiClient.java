package com.lecturebot.service;

import com.lecturebot.generated.model.Citation;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GenAiClient {
    /**
     * Stub method to simulate GenAI backend call.
     * Replace with actual HTTP call to GenAI backend.
     */
    public GenAiResponse queryCourseSpace(String question, String courseSpaceContext) {
        // TODO: Replace with real GenAI backend integration
        GenAiResponse response = new GenAiResponse();
        response.setAnswer("This is a stub answer for: " + question);
        response.setCitations(List.of());
        return response;
    }

    public static class GenAiResponse {
        private String answer;
        private List<Citation> citations;

        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public List<Citation> getCitations() { return citations; }
        public void setCitations(List<Citation> citations) { this.citations = citations; }
    }
}
