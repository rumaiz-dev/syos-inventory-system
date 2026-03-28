package com.syos.repository;

import com.syos.entity.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {
    List<StockBatch> findByExpiryDateBefore(LocalDate date);
}