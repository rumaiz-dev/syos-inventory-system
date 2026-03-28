package com.syos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {
    Map<String, Object> getSalesReport(LocalDate date);
    List<Map<String, Object>> getInventoryReport(int limit);
    Map<String, Object> getTransactionReport(LocalDate startDate, LocalDate endDate);
}