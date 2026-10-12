package com.storeops.reports;

import com.storeops.reports.dto.ReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {

    ReportRepository repo;
    ReportService service;

    @BeforeEach
    void setup() {
        repo = new InMemoryReportRepository();
        service = new ReportService(repo);
    }

    @Test
    void storeReports_ok() {
        List<ReportDto> list = service.storeReports("store-1");
        assertFalse(list.isEmpty());
    }

    @Test
    void regionReports_ok() {
        List<ReportDto> list = service.regionReports("region-1");
        assertFalse(list.isEmpty());
    }
}
