package com.example.demo.order;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import jakarta.persistence.*;
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
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_gen")
	@SequenceGenerator(name = "order_seq_gen", sequenceName = "order_no_seq", allocationSize = 1)
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
    private String username;
    // 배송지 번호
    private Long addressNo;
}