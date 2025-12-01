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
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false, defaultValue = "0.2") Double radiusDegrees
    ) {
        Map<String, Object> result = reportService.generateHeatmap(latitude, longitude, radiusDegrees);
        return ResponseEntity.ok(result);
    }
}
