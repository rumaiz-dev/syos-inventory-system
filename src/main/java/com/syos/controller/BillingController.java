package com.syos.controller;

import com.syos.entity.Bill;
import com.syos.entity.BillItem;
import com.syos.service.BillService;
import com.syos.application.service.BillingService;
import com.syos.application.service.BillingService.BillRequest;
import com.syos.application.service.BillingService.BillItemRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/bills")
public class BillingController {
    private final BillService billService;
    private final BillingService billingService;
    
    public BillingController(BillService billService) {
        this.billService = billService;
        this.billingService = new BillingService();
    }
    
    @GetMapping
    public ResponseEntity<?> getAllBills() {
        try {
            List<Bill> bills = billService.findAll();
            List<Map<String, Object>> billSummaries = bills.stream()
                .map(bill -> {
                    Map<String, Object> summary = new HashMap<>();
                    summary.put("id", bill.getId());
                    summary.put("billNumber", bill.getBillNumber());
                    summary.put("billDate", bill.getBillDate());
                    summary.put("totalAmount", bill.getTotalAmount());
                    summary.put("cashTendered", bill.getCashTendered());
                    summary.put("changeReturned", bill.getChangeReturned());
                    summary.put("itemCount", bill.getItems() != null ? bill.getItems().size() : 0);
                    return summary;
                })
                .toList();
            return ResponseEntity.ok(billSummaries);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve bills: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getBillById(@PathVariable Long id) {
        try {
            return billService.findById(id)
                .map(bill -> {
                    Map<String, Object> billDetail = new HashMap<>();
                    billDetail.put("id", bill.getId());
                    billDetail.put("billNumber", bill.getBillNumber());
                    billDetail.put("billDate", bill.getBillDate());
                    billDetail.put("totalAmount", bill.getTotalAmount());
                    billDetail.put("cashTendered", bill.getCashTendered());
                    billDetail.put("changeReturned", bill.getChangeReturned());
                    billDetail.put("transactionType", bill.getTransactionType());
                    
                    List<Map<String, Object>> itemDetails = new ArrayList<>();
                    if (bill.getItems() != null) {
                        for (BillItem item : bill.getItems()) {
                            Map<String, Object> itemMap = new HashMap<>();
                            itemMap.put("id", item.getId());
                            itemMap.put("productCode", item.getProductCode());
                            itemMap.put("productName", item.getProductName());
                            itemMap.put("quantity", item.getQuantity());
                            itemMap.put("unitPrice", item.getUnitPrice());
                            itemMap.put("totalPrice", item.getTotalPrice());
                            itemMap.put("discountAmount", item.getDiscountAmount());
                            itemDetails.add(itemMap);
                        }
                    }
                    billDetail.put("items", itemDetails);
                    
                    return ResponseEntity.ok(billDetail);
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve bill: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createBill(@RequestBody Map<String, Object> requestData) {
        try {
            BillRequest billRequest = new BillRequest();
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) requestData.get("items");
            if (itemsData == null || itemsData.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Items are required"));
            }
            
            List<BillItemRequest> items = new ArrayList<>();
            for (Map<String, Object> itemData : itemsData) {
                BillItemRequest item = new BillItemRequest();
                item.setProductCode((String) itemData.get("productCode"));
                Object quantityObj = itemData.get("quantity");
                if (quantityObj instanceof Number) {
                    item.setQuantity(((Number) quantityObj).intValue());
                } else {
                    item.setQuantity(Integer.parseInt(quantityObj.toString()));
                }
                items.add(item);
            }
            billRequest.setItems(items);
            
            Object cashTenderedObj = requestData.get("cashTendered");
            if (cashTenderedObj instanceof Number) {
                billRequest.setCashTendered(((Number) cashTenderedObj).doubleValue());
            } else {
                billRequest.setCashTendered(Double.parseDouble(cashTenderedObj.toString()));
            }
            
            Bill bill = billingService.createBill(billRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", bill.getId());
            response.put("billNumber", bill.getBillNumber());
            response.put("totalAmount", bill.getTotalAmount());
            response.put("cashTendered", bill.getCashTendered());
            response.put("changeReturned", bill.getChangeReturned());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create bill: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/items")
    public ResponseEntity<?> getBillItems(@PathVariable Long id) {
        try {
            return billService.findById(id)
                .map(bill -> {
                    List<Map<String, Object>> itemDetails = new ArrayList<>();
                    if (bill.getItems() != null) {
                        for (BillItem item : bill.getItems()) {
                            Map<String, Object> itemMap = new HashMap<>();
                            itemMap.put("id", item.getId());
                            itemMap.put("productCode", item.getProductCode());
                            itemMap.put("productName", item.getProductName());
                            itemMap.put("quantity", item.getQuantity());
                            itemMap.put("unitPrice", item.getUnitPrice());
                            itemMap.put("totalPrice", item.getTotalPrice());
                            itemMap.put("discountAmount", item.getDiscountAmount());
                            itemDetails.add(itemMap);
                        }
                    }
                    return ResponseEntity.ok(itemDetails);
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve bill items: " + e.getMessage()));
        }
    }
}