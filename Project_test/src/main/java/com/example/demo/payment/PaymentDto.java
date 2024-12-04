package com.example.demo.payment;

import jakarta.validation.constraints.*;
import lombok.*;

public class PaymentDto {

    // 카카오페이 결제 생성 요청에 사용하는 DTO 클래스
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        @NotNull(message = "주문 번호는 필수입니다.")
        private Long orderNo; // 주문 번호
        private String username; // 사용자 이름
        private String itemName; // 상품 이름
        private int totalAmount; // 총 결제 금액

        // DTO를 엔티티로 변환하는 메소드
        public Payment toEntity() {
            Payment payment = new Payment();
            payment.setOrderNo(orderNo);
            payment.setUsername(username);
            payment.setItemName(itemName);
            payment.setTotalAmount(totalAmount);
            return payment;
        }
    }

    // 결제 상태 조회에 사용하는 DTO 클래스
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Read {
        private Long orderNo; // 주문 번호
        private String username; // 사용자 이름
        private String itemName; // 상품 이름
        private int totalAmount; // 총 결제 금액
        private String tid; // TID
        private String paymentStatus; // 결제 상태 (READY, APPROVED 등)
    }
}
