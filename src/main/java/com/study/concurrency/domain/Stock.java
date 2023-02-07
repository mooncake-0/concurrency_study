package com.study.concurrency.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*
 제품의 재고를 관리해주는 Entity
 */
@Entity
@Getter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    private int quantity;

    /*
     연관관계
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    public Stock(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public void decreaseQuantity(int quantity) {
        if(this.quantity - quantity < 0){
            throw new RuntimeException("재고는 0 개 이하일 수 없습니다");
        }
        this.quantity -= quantity;
    }

}
