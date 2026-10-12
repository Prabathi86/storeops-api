package com.storeops.reports.dto;

import com.storeops.reports.ReportStatus;
import com.storeops.reports.ReportType;

public class ReportDto {
    public String reportId;
    public ReportType reportType;
    public ReportStatus reportStatus;
    public String targetId;
    public String description;
}
