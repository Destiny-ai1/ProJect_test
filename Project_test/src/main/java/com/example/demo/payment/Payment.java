package com.example.demo.payment;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY는 DB에서 자동 증가되도록 설정합니다.
    private Long orderNo; // 주문 번호 (기본 키)

    private String username; // 사용자 이름 (member 테이블에서 참조)
    private String itemName; // 상품 이름 (결제 시점의 상품 이름 저장)
    private int totalAmount; // 총 결제 금액
    private String tid; // TID 값 추가
    private String paymentStatus; // 결제 상태 추가 (예: "READY", "APPROVED", "CANCELLED" 등)
}
