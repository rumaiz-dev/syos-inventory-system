package com.syos.service;

import com.syos.entity.Discount;
import com.syos.repository.DiscountRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    @Override
    public Optional<Discount> findById(Long id) {
        return discountRepository.findById(id);
    }

    @Override
    public Discount save(Discount discount) {
        return discountRepository.save(discount);
    }

    @Override
    public void delete(Long id) {
        discountRepository.deleteById(id);
    }
}