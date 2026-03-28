package com.syos.controller;

import com.syos.service.ReportService;
import com.syos.infrastructure.repository.ReportRepository;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    @GetMapping("/sales")
    public ResponseEntity<?> getSalesReport(@RequestParam(required = false) String date) {
        try {
            LocalDate reportDate = date != null && !date.isEmpty() ? LocalDate.parse(date) : LocalDate.now();
            
            Map<String, Object> report = reportService.getSalesReport(reportDate);
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate sales report: " + e.getMessage()));
        }
    }
    
    @GetMapping("/inventory")
    public ResponseEntity<?> getInventoryReport(@RequestParam(defaultValue = "0") int limit) {
        try {
            List<Map<String, Object>> report = inventoryService.getInventoryReport(limit);
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate inventory report: " + e.getMessage()));
        }
    }
    
    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactionReport(@RequestParam(required = false) String startDate,
                                                   @RequestParam(required = false) String endDate) {
        try {
            return ResponseEntity.ok(reportService.getTransactionReport(start, end));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate transaction report: " + e.getMessage()));
        }
    }
    
    @GetMapping("/daily-sales")
    public ResponseEntity<?> getDailySalesReport(@RequestParam(required = false) String date) {
        try {
            LocalDate reportDate;
            if (date != null && !date.isEmpty()) {
                reportDate = LocalDate.parse(date);
            } else {
                reportDate = LocalDate.now();
            }
            
            Map<String, Object> report = reportService.getSalesReport(reportDate);
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate daily sales report: " + e.getMessage()));
        }
    }
    
    @GetMapping("/product-stock")
    public ResponseEntity<?> getProductStockReport() {
        try {
            return ResponseEntity.ok(reportService.getInventoryReport(0));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate product stock report: " + e.getMessage()));
        }
    }
}