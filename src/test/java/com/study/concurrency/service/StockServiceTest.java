package com.study.concurrency.service;

import com.study.concurrency.domain.Product;
import com.study.concurrency.domain.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class StockServiceTest {

    @Autowired
    private StockService stockService;

    private Stock stock;

    @BeforeEach
    void be() {
        Product product = new Product();
        product.setProductName("선글라스");

        stock = new Stock(product, 100);
        stockService.saveStock(stock);
    }

    @Test
    @DisplayName("재고 수량을 줄인다")
    void 재고_수량_감소_test() {

        stockService.decreaseQuantity(stock.getId(), 1);

        // 100 - 1 = 99

        Stock refreshedStock = stockService.findById(stock.getId());
        assertEquals(99, refreshedStock.getQuantity());
    }

    @Test
    @DisplayName("동시에 100개의 요청을 한다")
    void 동시_100개_요청() throws InterruptedException {

        int threadCount = 100;

        /*
         ExecutorService - 비동기로(계속 날림) 실행하는 작업을 단순화하여 사용할 수 있게끔 도와주는 Java API
         CountDownLatch - 다른 Thread 에서 수행중인 작업이 완료될 때까지 대기할 수 있도록 지원
         */
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseQuantity(stock.getId(), 1);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        // 실행되는 100 개의 스레드가 완료될 때까지 기다린다.
        countDownLatch.await();

        // 각 요청이 트랜젝션 내부에 동시에 진입한 후, 처리 결과가 expected 와 다를 경우, 해당 액션을 rollback 한다
        // 그러므로 취소되는 요청이 많아서, 100개 다 줄지 않는다.
        Stock refreshedStock = stockService.findById(stock.getId());

        // 일반 경우
//        assertNotEquals(0, refreshedStock.getQuantity());

        // Stock Service 에 decreaseQuantity() 에 synchronized 붙일 경우
        assertEquals(0, refreshedStock.getQuantity());

    }

}
