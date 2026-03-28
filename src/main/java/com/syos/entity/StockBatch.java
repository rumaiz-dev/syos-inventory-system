package com.syos.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stock_batches")
public class StockBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_number", unique = true, nullable = false)
    private String batchNumber;

    @Column(name = "product_code", nullable = false)
    private String productCode;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    public StockBatch() {
    }

    public StockBatch(String batchNumber, String productCode, int quantity, LocalDate expiryDate, LocalDate purchaseDate) {
        this.batchNumber = batchNumber;
        this.productCode = productCode;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.purchaseDate = purchaseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}