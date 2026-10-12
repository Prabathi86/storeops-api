package com.storeops.reports;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class InMemoryReportRepository implements ReportRepository {
    @Override
    public List<Report> findStoreReports(String storeId) {
        Report r = new Report();
        r.setId("store-report-1");
        r.setType(ReportType.STORE_SUMMARY);
        r.setStatus(ReportStatus.READY);
        r.setTargetId(storeId);
        r.setDescription("Store summary for " + storeId);
        return List.of(r);
    }

    @Override
    public List<Report> findRegionReports(String regionId) {
        Report r = new Report();
        r.setId("region-report-1");
        r.setType(ReportType.REGIONAL_ROLLUP);
        r.setStatus(ReportStatus.READY);
        r.setTargetId(regionId);
        r.setDescription("Regional rollup for " + regionId);
        return List.of(r);
    }
}
