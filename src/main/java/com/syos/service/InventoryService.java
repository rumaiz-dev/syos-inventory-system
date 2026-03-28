package com.syos.service;

import com.syos.entity.ShelfStock;
import com.syos.entity.StockBatch;
import java.util.List;

public interface InventoryService {
    List<StockBatch> findAllBatches();
    List<ShelfStock> findAllShelfStocks();
    StockBatch receiveStock(String productCode, int quantity, java.time.LocalDate expiryDate);
    ShelfStock moveStock(Long batchId, int quantity);
    List<StockBatch> findExpiringBatches(java.time.LocalDate date);
    void discardBatch(Long batchId);
}