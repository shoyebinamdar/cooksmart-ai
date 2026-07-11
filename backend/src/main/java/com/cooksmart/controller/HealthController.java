package com.cooksmart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Service health checks")
public class HealthController {

    @GetMapping
    @Operation(summary = "Liveness/readiness probe")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "cooksmart-ai",
                "timestamp", Instant.now().toString()));
    }
}
