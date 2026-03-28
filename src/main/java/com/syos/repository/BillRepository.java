package com.syos.repository;

import com.syos.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByBillDate(LocalDate billDate);
}