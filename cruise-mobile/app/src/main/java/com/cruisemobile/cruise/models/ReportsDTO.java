package com.cruisemobile.cruise.models;

import java.util.List;

public class ReportsDTO {
    private List<ReportDTO> reports;

    public ReportsDTO() {
    }

    public List<ReportDTO> getReports() {
        return reports;
    }

    public void setReports(List<ReportDTO> reports) {
        this.reports = reports;
    }
}
