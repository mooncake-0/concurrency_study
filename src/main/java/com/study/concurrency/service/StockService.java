package com.study.concurrency.service;

import com.study.concurrency.domain.Stock;
import com.study.concurrency.repository.StockDataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
//@Transactional // Q:: 지워서 사용해도 되는 이유 궁금 -> Data Jpa 라 가능한 것으로 보인다.
@RequiredArgsConstructor
public class StockService {

    private final StockDataJpaRepository stockRepository;

    public void saveStock(Stock stock) {
        stockRepository.saveAndFlush(stock);
    }

    public Stock findById(Long stockId) {
        return stockRepository.findById(stockId).orElseThrow();
    }

    /*
     Synchronize 는 문제를 완전히 해결하지 못한다.
     @Transactional 때문
     > Transactional 은 새로운 함수를 만들어두는데, 다음과 같다
     -- createdDecreaseQuantity(){ -- 얘가 synchronize 가 안됨
         transactionBegin();

         decreaseQuantity();

         transactionFinish();
     }
     > Q :: Transactional 이 만들어 주는 함수는 세마포어 제어가 안되는
     */
    public synchronized void decreaseQuantity(Long stockId, int quantity) {

        Stock stock = stockRepository.findById(stockId).orElseThrow();
        stock.decreaseQuantity(quantity);
        stockRepository.saveAndFlush(stock); // 쿼리를 날려준다.
    }
}
