package com.storeops.reports;

import com.storeops.reports.dto.ReportDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final ReportRepository repo;

    public ReportService(ReportRepository repo) { this.repo = repo; }

    public List<ReportDto> storeReports(String storeId) {
        return repo.findStoreReports(storeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReportDto> regionReports(String regionId) {
        return repo.findRegionReports(regionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ReportDto toDto(Report r) {
        ReportDto dto = new ReportDto();
        dto.reportId = r.getId();
        dto.reportType = r.getType();
        dto.reportStatus = r.getStatus();
        dto.targetId = r.getTargetId();
        dto.description = r.getDescription();
        return dto;
    }
}
