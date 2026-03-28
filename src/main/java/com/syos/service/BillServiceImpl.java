package com.syos.service;

import com.syos.entity.Bill;
import com.syos.repository.BillRepository;
import com.syos.repository.BillItemRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BillServiceImpl implements BillService {
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;

    public BillServiceImpl(BillRepository billRepository, BillItemRepository billItemRepository) {
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
    }

    @Override
    public List<Bill> findAll() {
        return billRepository.findAll();
    }

    @Override
    public Optional<Bill> findById(Long id) {
        return billRepository.findById(id);
    }

    @Override
    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }

    @Override
    public List<Bill> findByBillDate(LocalDate billDate) {
        return billRepository.findByBillDate(billDate);
    }
}