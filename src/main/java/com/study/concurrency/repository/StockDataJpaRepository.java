package com.study.concurrency.repository;

import com.study.concurrency.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDataJpaRepository extends JpaRepository<Stock, Long> {
}
