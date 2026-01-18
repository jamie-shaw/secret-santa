package com.secretsanta.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller for Angular application
 */
@RestController
@RequestMapping("/api")
@Tag(name = "System", description = "System status and health check endpoints")
public class ApiController {

    @Operation(
        summary = "Get API status",
        description = "Returns the current status of the Secret Santa API including timestamp"
    )
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Secret Santa API is running");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    @Operation(
        summary = "Health check",
        description = "Simple health check endpoint to verify the API is responsive"
    )
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }
}
