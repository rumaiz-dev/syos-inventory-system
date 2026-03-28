package com.syos.controller;

import com.syos.entity.Product;
import com.syos.entity.StockBatch;
import com.syos.entity.ShelfStock;
import com.syos.entity.Discount;
import com.syos.service.InventoryService;
import com.syos.service.ProductService;
import com.syos.infrastructure.singleton.InventoryManager;
import com.syos.application.strategy.ExpiryAwareFifoStrategy;
import com.syos.domain.enums.DiscountType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private final ProductService productService;
    private final InventoryManager inventoryManager;
    
    public InventoryController(InventoryService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
        this.inventoryManager = InventoryManager.getInstance(new ExpiryAwareFifoStrategy());
    }
    
    @GetMapping("/stocks")
    public ResponseEntity<?> getAllStocks() {
        try {
            // Get all shelf stocks
            List<ShelfStock> shelfStocks = inventoryService.findAllShelfStocks();
            List<Map<String, Object>> stockList = new java.util.ArrayList<>();
            
            for (ShelfStock stock : shelfStocks) {
                Map<String, Object> stockMap = new HashMap<>();
                stockMap.put("id", stock.getId());
                stockMap.put("shelfNumber", stock.getShelfNumber());
                stockMap.put("productCode", stock.getProductCode());
                stockMap.put("quantity", stock.getQuantity());
                stockMap.put("batchId", stock.getBatchId());
                stockMap.put("expiryDate", stock.getExpiryDate());
                stockList.add(stockMap);
            }
            
            return ResponseEntity.ok(stockList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get stocks: " + e.getMessage()));
        }
    }
    
    @GetMapping("/batches")
    public ResponseEntity<?> getAllBatches() {
        try {
            List<StockBatch> batches = inventoryService.findAllBatches();
            List<Map<String, Object>> batchList = new java.util.ArrayList<>();
            
            for (StockBatch batch : batches) {
                Map<String, Object> batchMap = new HashMap<>();
                batchMap.put("id", batch.getId());
                batchMap.put("batchNumber", batch.getBatchNumber());
                batchMap.put("productCode", batch.getProductCode());
                batchMap.put("quantity", batch.getQuantity());
                batchMap.put("expiryDate", batch.getExpiryDate());
                batchMap.put("purchaseDate", batch.getPurchaseDate());
                batchList.add(batchMap);
            }
            
            return ResponseEntity.ok(batchList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get batches: " + e.getMessage()));
        }
    }
    
    @PostMapping("/receive")
    public ResponseEntity<?> receiveStock(@RequestBody Map<String, Object> requestData) {
        try {
            String productCode = (String) requestData.get("productCode");
            Object quantityObj = requestData.get("quantity");
            String expiryDateStr = (String) requestData.get("expiryDate");
            
            if (productCode == null || productCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Product code is required"));
            }
            
            int quantity;
            if (quantityObj instanceof Number) {
                quantity = ((Number) quantityObj).intValue();
            } else {
                quantity = Integer.parseInt(quantityObj.toString());
            }
            
            LocalDate purchaseDate = LocalDate.now();
            LocalDate expiryDate = null;
            
            if (expiryDateStr != null && !expiryDateStr.trim().isEmpty()) {
                expiryDate = LocalDate.parse(expiryDateStr);
            }
            
            inventoryService.receiveStock(productCode, purchaseDate, expiryDate, quantity);
            
            return ResponseEntity.ok(Map.of("message", "Stock received successfully"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid quantity format"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to receive stock: " + e.getMessage()));
        }
    }
    
    @PostMapping("/move")
    public ResponseEntity<?> moveStock(@RequestBody Map<String, Object> requestData) {
        try {
            Object batchIdObj = requestData.get("batchId");
            Object quantityObj = requestData.get("quantity");
            
            if (batchIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Batch ID is required"));
            }
            
            int batchId;
            if (batchIdObj instanceof Number) {
                batchId = ((Number) batchIdObj).intValue();
            } else {
                batchId = Integer.parseInt(batchIdObj.toString());
            }
            
            int quantity;
            if (quantityObj instanceof Number) {
                quantity = ((Number) quantityObj).intValue();
            } else {
                quantity = Integer.parseInt(quantityObj.toString());
            }
            
            StockBatch batch = inventoryService.findAllBatches().stream()
                .filter(b -> b.getId() == batchId)
                .findFirst()
                .orElse(null);
            if (batch == null) {
                return ResponseEntity.notFound().build();
            }
            
            inventoryService.moveStock((long) batchId, quantity);
            
            return ResponseEntity.ok(Map.of("message", "Stock moved to shelf successfully"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid number format"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to move stock: " + e.getMessage()));
        }
    }
    
    @GetMapping("/expiring")
    public ResponseEntity<?> getExpiringStocks(@RequestParam(defaultValue = "7") int days) {
        try {
            LocalDate targetDate = LocalDate.now().plusDays(days);
            List<StockBatch> expiringBatches = inventoryService.findExpiringBatches(targetDate);
            
            List<Map<String, Object>> expiringList = new java.util.ArrayList<>();
            for (StockBatch batch : expiringBatches) {
                Map<String, Object> batchMap = new HashMap<>();
                batchMap.put("id", batch.getId());
                batchMap.put("batchNumber", batch.getBatchNumber());
                batchMap.put("productCode", batch.getProductCode());
                batchMap.put("quantity", batch.getQuantity());
                batchMap.put("expiryDate", batch.getExpiryDate());
                batchMap.put("purchaseDate", batch.getPurchaseDate());
                expiringList.add(batchMap);
            }
            
            return ResponseEntity.ok(expiringList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get expiring stocks: " + e.getMessage()));
        }
    }
    
    @PostMapping("/discard")
    public ResponseEntity<?> discardBatch(@RequestBody Map<String, Object> requestData) {
        try {
            Object batchIdObj = requestData.get("batchId");
            
            if (batchIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Batch ID is required"));
            }
            
            long batchId;
            if (batchIdObj instanceof Number) {
                batchId = ((Number) batchIdObj).longValue();
            } else {
                batchId = Long.parseLong(batchIdObj.toString());
            }
            
            StockBatch batch = stockBatchRepository.findById(batchId).orElse(null);
            if (batch == null) {
                return ResponseEntity.notFound().build();
            }
            
            inventoryService.discardBatch((int) batchId);
            
            return ResponseEntity.ok(Map.of("message", "Batch discarded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to discard batch: " + e.getMessage()));
        }
    }
    
    @PostMapping("/discounts")
    public ResponseEntity<?> createDiscount(@RequestBody Map<String, Object> requestData) {
        try {
            String name = (String) requestData.get("name");
            String typeStr = (String) requestData.get("type");
            Object valueObj = requestData.get("value");
            String startDateStr = (String) requestData.get("startDate");
            String endDateStr = (String) requestData.get("endDate");
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Discount name is required"));
            }
            if (valueObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Discount value is required"));
            }
            if (startDateStr == null || endDateStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Start date and end date are required"));
            }
            
            DiscountType discountType = DiscountType.valueOf(typeStr.toUpperCase());
            
            double discountValue;
            if (valueObj instanceof Number) {
                discountValue = ((Number) valueObj).doubleValue();
            } else {
                discountValue = Double.parseDouble(valueObj.toString());
            }
            
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            
            if (endDate.isBefore(startDate)) {
                return ResponseEntity.badRequest().body(Map.of("error", "End date cannot be before start date"));
            }
            
            // Using the domain model Discount and repository
            com.syos.domain.model.Discount discount = new com.syos.domain.model.Discount(
                name, discountType, discountValue, startDate, endDate
            );
            
            return ResponseEntity.ok(Map.of("message", "Discount created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create discount: " + e.getMessage()));
        }
    }
}