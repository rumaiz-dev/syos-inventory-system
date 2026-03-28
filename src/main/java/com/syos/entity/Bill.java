package com.syos.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_number", unique = true, nullable = false)
    private String billNumber;

    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "cash_tendered")
    private double cashTendered;

    @Column(name = "change_returned")
    private double changeReturned;

    @Column(name = "transaction_type")
    private String transactionType;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BillItem> items = new ArrayList<>();

    public Bill() {
    }

    public Bill(String billNumber, LocalDate billDate, double totalAmount, double cashTendered, double changeReturned, String transactionType) {
        this.billNumber = billNumber;
        this.billDate = billDate;
        this.totalAmount = totalAmount;
        this.cashTendered = cashTendered;
        this.changeReturned = changeReturned;
        this.transactionType = transactionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public void setCashTendered(double cashTendered) {
        this.cashTendered = cashTendered;
    }

    public double getChangeReturned() {
        return changeReturned;
    }

    public void setChangeReturned(double changeReturned) {
        this.changeReturned = changeReturned;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
    }

    public void addItem(BillItem item) {
        items.add(item);
        item.setBill(this);
    }
}