package com.syos.repository;

import com.syos.entity.ShelfStock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShelfStockRepository extends JpaRepository<ShelfStock, Long> {
    Optional<ShelfStock> findByShelfNumber(String shelfNumber);
}