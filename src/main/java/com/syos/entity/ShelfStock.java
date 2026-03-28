package com.syos.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "shelf_stocks")
public class ShelfStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shelf_number", nullable = false)
    private String shelfNumber;

    @Column(name = "product_code", nullable = false)
    private String productCode;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    public ShelfStock() {
    }

    public ShelfStock(String shelfNumber, String productCode, int quantity, Long batchId, LocalDate expiryDate) {
        this.shelfNumber = shelfNumber;
        this.productCode = productCode;
        this.quantity = quantity;
        this.batchId = batchId;
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShelfNumber() {
        return shelfNumber;
    }

    public void setShelfNumber(String shelfNumber) {
        this.shelfNumber = shelfNumber;
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

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}