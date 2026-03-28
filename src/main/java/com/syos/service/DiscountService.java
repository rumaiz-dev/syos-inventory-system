package com.syos.service;

import com.syos.entity.Discount;
import java.util.List;
import java.util.Optional;

public interface DiscountService {
    List<Discount> findAll();
    Optional<Discount> findById(Long id);
    Discount save(Discount discount);
    void delete(Long id);
}