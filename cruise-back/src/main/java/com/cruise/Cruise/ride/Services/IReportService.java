package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.driver.DTO.ReportDTO;
import com.cruise.Cruise.driver.DTO.ReportsDTO;

public interface IReportService {
    ReportsDTO getReports(Long id, String fromDate, String tillDate, String role);

    ReportDTO getReportByType(Long id, String fromDate, String tillDate, String type, String role);

    ReportsDTO getAllReports(String fromDate, String tillDate);

    ReportsDTO getUserReportsByEmail(String email, String fromDate, String tillDate);
}
