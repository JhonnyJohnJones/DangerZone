package com.dangerzone.backend.controller;

import com.dangerzone.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/heatmap")
@RequiredArgsConstructor
public class HeatmapController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getHeatmap(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false, defaultValue = "0.2") Double radiusDegrees
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token missing or invalid");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        
        Map<String, Object> result = reportService.generateHeatmap(latitude, longitude, radiusDegrees);
        return ResponseEntity.ok(result);
    }
}
