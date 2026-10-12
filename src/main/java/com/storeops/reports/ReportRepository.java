package com.storeops.reports;

import java.util.List;

public interface ReportRepository {
    List<Report> findStoreReports(String storeId);
    List<Report> findRegionReports(String regionId);
}
