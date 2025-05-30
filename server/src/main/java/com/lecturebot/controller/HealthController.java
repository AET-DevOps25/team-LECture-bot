package com.lecturebot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, String>> checkHealth() {
        // You can expand this to check database connections, etc.
        return ResponseEntity.ok(Map.of("status", "UP", "message", "LECture-bot server is running!"));
    }
}
