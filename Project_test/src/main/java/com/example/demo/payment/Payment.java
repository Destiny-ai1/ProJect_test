package com.example.demo.payment;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Component
public class Payment { // 결제 엔티티 클래스
    private Long orderNo; // 주문 번호 (orders 테이블과 연관 관계)
    private String payMethod; // 결제 수단
    private int pointAdd; // 포인트 적립 금액
    private Long cardNo; // 카드 번호 (card 테이블과 연관 관계)
}