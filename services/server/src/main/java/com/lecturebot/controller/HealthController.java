package com.lecturebot.controller;

import com.lecturebot.generated.api.HealthApi;
import com.lecturebot.generated.model.HealthStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController implements HealthApi {

    @Override
    public ResponseEntity<HealthStatus> healthCheck() {
        HealthStatus status = new HealthStatus()
                .status("UP")
                .message("LECture-bot server is running!");
        return ResponseEntity.ok(status);
    }
}
