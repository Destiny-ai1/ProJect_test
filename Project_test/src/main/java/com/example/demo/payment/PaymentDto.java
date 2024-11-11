package com.example.demo.payment;

import jakarta.validation.constraints.*;
import lombok.*;

public class PaymentDto { // 결제 관련 데이터 전달 객체
    private PaymentDto() {}

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create { // 결제 정보를 생성하기 위한 DTO
        @NotNull
        private Long orderNo; // 주문 번호
        @NotEmpty
        private String payMethod; // 결제 수단
        @NotNull
        private int pointAdd; // 포인트 적립 금액
        @NotNull
        private Long cardNo; // 카드 번호

        public Payment toEntity() { // DTO를 Payment 엔티티로 변환하는 메소드
            return new Payment(orderNo, payMethod, pointAdd, cardNo);
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Read { // 결제 조회를 위한 DTO
        private Long orderNo; // 주문 번호
        private String payMethod; // 결제 수단
        private int pointAdd; // 포인트 적립 금액
        private Long cardNo; // 카드 번호
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update { // 결제 정보를 수정하기 위한 DTO
        @NotNull
        private Long orderNo; // 주문 번호
        @NotEmpty
        private String payMethod; // 수정할 결제 수단
        @NotNull
        private int pointAdd; // 수정할 포인트 적립 금액

        public Payment toEntity() { // DTO를 Payment 엔티티로 변환하는 메소드
            return Payment.builder().orderNo(orderNo).payMethod(payMethod).pointAdd(pointAdd).build();
        }
    }
}
