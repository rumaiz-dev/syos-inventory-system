package com.syos.service;

import com.syos.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product product);
    void delete(Long id);
    Optional<Product> findByProductCode(String productCode);
}