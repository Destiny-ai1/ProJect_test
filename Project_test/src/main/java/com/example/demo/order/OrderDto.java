package com.example.demo.order;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OrderDto {

    // 주문 생성에 사용되는 DTO 클래스
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        @NotNull // 주문 번호는 null일 수 없음
        private Long orderNo;
        @NotNull // 총 가격은 null일 수 없음
        private int totalPrice;
        @NotEmpty // 주문 상태는 비어있을 수 없음
        private String orderStatus;
        private int usedPoint; // 사용된 포인트 (옵션)
        private int actPayment; // 실제 결제 금액 (옵션)
        @NotEmpty // 회원의 사용자 이름은 비어있을 수 없음
        private String memberUsername;
        @NotNull // 우편 번호는 null일 수 없음
        private Long postNo;
        private List<OrderDetailDto> orderDetails; // 주문 상세 항목 리스트

        // DTO를 엔티티로 변환하는 메소드
        public Order toEntity() {
            return Order.builder()
                    .orderNo(orderNo)
                    .orderDate(LocalDate.now()) // 주문 날짜는 현재 날짜로 설정
                    .totalPrice(totalPrice)
                    .orderStatus(orderStatus)
                    .usedPoint(usedPoint)
                    .actPayment(actPayment)
                    .memberUsername(memberUsername)
                    .postNo(postNo)
                    .build();
        }
    }
    
    // 주문 목록을 제공하기 위한 DTO 클래스
    @Data
    @AllArgsConstructor
    public static class OrderList {
        private Long orderNo; // 주문 번호
        private String orderDate; // 주문 날짜
        private String orderStatus; // 주문 상태
        private int totalPrice; // 총 가격
        private List<OrderDetailDto> orderDetails; // 주문 상세 항목 리스트
    }

    // 주문 조회에 사용되는 DTO 클래스
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Read {
        private Long orderNo; // 주문 번호
        private LocalDate orderDate; // 주문 날짜
        private int totalPrice; // 총 가격
        private String orderStatus; // 주문 상태
        private int usedPoint; // 사용된 포인트
        private int actPayment; // 실제 결제 금액
        private String memberUsername; // 회원의 사용자 이름
        private Long postNo; // 우편 번호
        private List<OrderDetailDto> orderDetails; // 주문 상세 항목 리스트
    }

    // 주문 수정에 사용되는 DTO 클래스
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        @NotNull // 주문 번호는 null일 수 없음
        private Long orderNo;
        @NotEmpty // 주문 상태는 비어있을 수 없음
        private String orderStatus;
        private List<OrderDetailDto> orderDetails; // 주문 상세 항목 리스트 (옵션)

        // DTO를 엔티티로 변환하는 메소드
        public Order toEntity() {
            return Order.builder()
                    .orderNo(orderNo)
                    .orderStatus(orderStatus)
                    .build();
        }
    }
}