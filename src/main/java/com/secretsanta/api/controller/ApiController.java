package com.secretsanta.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller for Angular application
 * Add your API endpoints here that the Angular app will consume
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * Example API endpoint
     * Access at: http://localhost:8080/api/status
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Secret Santa API is running");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    /**
     * Example health check endpoint
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }
}
