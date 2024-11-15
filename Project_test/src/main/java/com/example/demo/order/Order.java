package com.example.demo.order;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Component
public class Order {
    // 주문 번호
    private Long orderNo;
    // 주문 날짜
    private LocalDate orderDate;
    // 총 가격
    private int totalPrice;
    // 주문 상태
    private String orderStatus;
    // 사용된 포인트
    private int usedPoint;
    // 실제 결제 금액
    private int actPayment;
    // 회원 사용자 이름
    private String memberUsername;
    // 우편 번호
    private Long addressNo;
}