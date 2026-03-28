package com.syos.repository;

import com.syos.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillItemRepository extends JpaRepository<BillItem, Long> {
}