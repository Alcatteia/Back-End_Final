package com.exemplo.bancoalcatteia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("service", "Alcatteia Backend");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "alcatteia-backend");
        return ResponseEntity.ok(response);
    }
} 