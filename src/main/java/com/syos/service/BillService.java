package com.syos.service;

import com.syos.entity.Bill;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillService {
    List<Bill> findAll();
    Optional<Bill> findById(Long id);
    Bill save(Bill bill);
    List<Bill> findByBillDate(LocalDate billDate);
}