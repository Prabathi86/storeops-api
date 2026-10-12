package com.storeops.reports;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storeops.reports.dto.ReportDto;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService service;

    public ReportController(ReportService service) { this.service = service; }

    @GetMapping("/stores/{storeId}")
    public ResponseEntity<List<ReportDto>> storeReports(@PathVariable String storeId) {
        return ResponseEntity.ok(service.storeReports(storeId));
    }

    @GetMapping("/regions/{regionId}")
    public ResponseEntity<List<ReportDto>> regionReports(@PathVariable String regionId) {
        return ResponseEntity.ok(service.regionReports(regionId));
    }
}
