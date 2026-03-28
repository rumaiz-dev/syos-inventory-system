package com.syos.service;

import com.syos.entity.Bill;
import com.syos.infrastructure.repository.ReportRepository;
import com.syos.repository.BillRepository;
import com.syos.repository.ShelfStockRepository;
import com.syos.repository.StockBatchRepository;
import com.syos.application.dto.ProductStockReportItemDTO;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    private final BillRepository billRepository;
    private final StockBatchRepository stockBatchRepository;
    private final ShelfStockRepository shelfStockRepository;
    private final ReportRepository reportRepository;

    public ReportServiceImpl(BillRepository billRepository, 
                            StockBatchRepository stockBatchRepository,
                            ShelfStockRepository shelfStockRepository) {
        this.billRepository = billRepository;
        this.stockBatchRepository = stockBatchRepository;
        this.shelfStockRepository = shelfStockRepository;
        this.reportRepository = new ReportRepository(null, shelfStockRepository, stockBatchRepository);
    }

    @Override
    public Map<String, Object> getSalesReport(LocalDate date) {
        List<Bill> bills = billRepository.findByBillDate(date);
        
        double totalSales = bills.stream()
            .mapToDouble(Bill::getTotalAmount)
            .sum();
        
        int totalTransactions = bills.size();
        
        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("totalSales", totalSales);
        report.put("totalTransactions", totalTransactions);
        report.put("bills", bills.stream().map(bill -> {
            Map<String, Object> billMap = new HashMap<>();
            billMap.put("billNumber", bill.getBillNumber());
            billMap.put("totalAmount", bill.getTotalAmount());
            billMap.put("cashTendered", bill.getCashTendered());
            billMap.put("changeReturned", bill.getChangeReturned());
            return billMap;
        }).collect(Collectors.toList()));
        
        return report;
    }

    @Override
    public List<Map<String, Object>> getInventoryReport(int limit) {
        List<ProductStockReportItemDTO> reportItems = reportRepository.getProductStockReportData(limit);
        
        return reportItems.stream()
            .map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productCode", item.getProductCode());
                itemMap.put("productName", item.getProductName());
                itemMap.put("shelfStock", item.getShelfStock());
                itemMap.put("warehouseStock", item.getWarehouseStock());
                itemMap.put("totalStock", item.getTotalStock());
                itemMap.put("expiryDate", item.getExpiryDate());
                return itemMap;
            })
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getTransactionReport(LocalDate startDate, LocalDate endDate) {
        List<Bill> allBills = billRepository.findAll();
        
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        
        List<Map<String, Object>> transactions = new java.util.ArrayList<>();
        double totalAmount = 0;
        
        for (Bill bill : allBills) {
            if (!bill.getBillDate().isBefore(start) && !bill.getBillDate().isAfter(end)) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("billNumber", bill.getBillNumber());
                transaction.put("billDate", bill.getBillDate());
                transaction.put("totalAmount", bill.getTotalAmount());
                transaction.put("cashTendered", bill.getCashTendered());
                transaction.put("changeReturned", bill.getChangeReturned());
                transaction.put("transactionType", bill.getTransactionType());
                transaction.put("itemCount", bill.getItems() != null ? bill.getItems().size() : 0);
                transactions.add(transaction);
                totalAmount += bill.getTotalAmount();
            }
        }
        
        Map<String, Object> report = new HashMap<>();
        report.put("startDate", start);
        report.put("endDate", end);
        report.put("totalTransactions", transactions.size());
        report.put("totalAmount", totalAmount);
        report.put("transactions", transactions);
        
        return report;
    }
}