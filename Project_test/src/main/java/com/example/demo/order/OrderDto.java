package com.example.demo.order;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OrderDto {

	// 주문 생성에 사용되는 DTO 클래스
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        @NotNull 									// 주문 번호는 null일 수 없음
        private Long orderNo;
        private Date orderDate;
        @NotNull 									// 총 가격은 null일 수 없음
        private Long totalPrice;
        @NotEmpty 									// 주문 상태는 비어있을 수 없음
        private String orderStatus;
        private Long usedPoint; 						// 사용된 포인트 (옵션)
        private Long actPayment; 					// 실제 결제 금액 (옵션)
        @NotEmpty 				
        private String username;					// 회원의 사용자 이름은 비어있을 수 없음
        private Long addressNo;						// 우편 번호는 null일 수 없음
        private List<OrderDetailDto> orderDetails; 	// 주문 상세 항목 리스트

        // DTO를 엔티티로 변환하는 메소드
        public Order toEntity() {
            return Order.builder()
                    .orderNo(orderNo)
                    .orderDate(LocalDate.now()) 					// 주문 날짜는 현재 날짜로 설정
                    .totalPrice(totalPrice)
                    .orderStatus(orderStatus)
                    .usedPoint(usedPoint)
                    .actPayment(actPayment)
                    .username(username)
                    .addressNo(addressNo)
                    .build();
        }
    }
    
    // 결제 준비를 위한 DTO
    @Data
    public static class PaymentPreparation {
        private Long orderNo;      // 주문 번호
        private Long totalAmount;  // 총 결제 금액
        private Long usedPoint;   // 사용된 포인트
        private String shippingAddress; // 배송지
    }
    
    // 주문 목록을 제공하기 위한 DTO 클래스
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderList {
        private Long orderNo; // 주문 번호
        private LocalDate orderDate; // 주문 날짜
        private String orderStatus; // 주문 상태
        private Long totalPrice; // 총 가격
        private int usedPoint; // 사용 포인트
        private Long actPayment; // 실제 결제 금액
        private List<OrderDetailDto> orderDetails; // 주문 상세 항목 리스트
        private String memberUsername;   // 로그인한 사용자 ID 추가
        private String itemName;
        private String image;
    }

    // 결제 정보를 포함한 주문 목록을 제공하기 위한 DTO 클래스
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderListWithPayment {
        private Long orderNo; // 주문 번호
        private LocalDate orderDate; // 주문 날짜
        private String orderStatus; // 주문 상태
        private int totalPrice; // 총 가격
        private String payMethod; // 결제 수단
        private int pointAdd; // 포인트 적립 금액
        private Long cardNo; // 카드 번호
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
        private String username; // 회원의 사용자 이름
        private Long addressNo; // 배송지 번호
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