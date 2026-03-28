package com.syos.repository;

import com.syos.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByNameContaining(String name);
}