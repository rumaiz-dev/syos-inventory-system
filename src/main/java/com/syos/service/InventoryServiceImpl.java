package com.syos.service;

import com.syos.entity.ShelfStock;
import com.syos.entity.StockBatch;
import com.syos.repository.ShelfStockRepository;
import com.syos.repository.StockBatchRepository;
import com.syos.infrastructure.singleton.InventoryManager;
import com.syos.application.strategy.ExpiryAwareFifoStrategy;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final StockBatchRepository stockBatchRepository;
    private final ShelfStockRepository shelfStockRepository;
    private final InventoryManager inventoryManager;

    public InventoryServiceImpl(StockBatchRepository stockBatchRepository, 
                                 ShelfStockRepository shelfStockRepository) {
        this.stockBatchRepository = stockBatchRepository;
        this.shelfStockRepository = shelfStockRepository;
        this.inventoryManager = InventoryManager.getInstance(new ExpiryAwareFifoStrategy());
    }

    @Override
    public List<StockBatch> findAllBatches() {
        return stockBatchRepository.findAll();
    }

    @Override
    public List<ShelfStock> findAllShelfStocks() {
        return shelfStockRepository.findAll();
    }

    @Override
    public StockBatch receiveStock(String productCode, int quantity, LocalDate expiryDate) {
        LocalDate purchaseDate = LocalDate.now();
        inventoryManager.receiveStock(productCode, purchaseDate, expiryDate, quantity);
        
        // Get the created batch from repository (most recent)
        List<StockBatch> batches = stockBatchRepository.findAll();
        return batches.isEmpty() ? null : batches.get(batches.size() - 1);
    }

    @Override
    public ShelfStock moveStock(Long batchId, int quantity) {
        Optional<StockBatch> batchOpt = stockBatchRepository.findById(batchId);
        if (batchOpt.isEmpty()) {
            return null;
        }
        
        StockBatch batch = batchOpt.get();
        inventoryManager.moveToShelf(batch.getProductCode(), quantity);
        
        // Get the created shelf stock (most recent)
        List<ShelfStock> stocks = shelfStockRepository.findAll();
        return stocks.isEmpty() ? null : stocks.get(stocks.size() - 1);
    }

    @Override
    public List<StockBatch> findExpiringBatches(LocalDate date) {
        return stockBatchRepository.findByExpiryDateBefore(date);
    }

    @Override
    public void discardBatch(Long batchId) {
        Optional<StockBatch> batchOpt = stockBatchRepository.findById(batchId);
        if (batchOpt.isPresent()) {
            StockBatch batch = batchOpt.get();
            inventoryManager.discardBatchQuantity(batchId.intValue(), batch.getQuantity());
        }
    }
}