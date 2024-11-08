package com.example.demo.order;

import java.time.*;
import java.util.*;

import jakarta.validation.constraints.*;
import lombok.*;

public class OrderDto {
    // 객체생성을 못하게 막는다
    private OrderDto() { }

    // 주문 생성에 사용되는 DTO 클래스
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        @NotNull // 주문 번호는 null일 수 없음
        private Long orderNo;
        @NotEmpty // 주문 날짜는 비어 있을 수 없음
        private String orderDate;
        @NotNull // 총 가격은 null일 수 없음
        private int totalPrice;
        @NotEmpty // 주문 상태는 비어 있을 수 없음
        private String orderStatus;
        private int usedPoint; // 사용된 포인트
        private int actPayment; // 실제 결제 금액
        @NotEmpty // 회원 ID는 비어 있을 수 없음
        private String memberUsername;
        @NotNull // 배송지 번호는 null일 수 없음
        private Long postNo;

        private List<OrderDetail> orderDetails; // 주문에 대한 상세 정보 리스트

        // DTO를 Order 엔티티로 변환하는 메소드
        public Order toEntity() {
            return new Order(orderNo, LocalDate.parse(orderDate), totalPrice, orderStatus, usedPoint, actPayment, memberUsername, postNo);
        }
    }

    // 주문 목록을 제공하기 위한 DTO 클래스
    @Getter
    @ToString
    @AllArgsConstructor
    public static class OrderList {
        private Long orderNo; // 주문 번호
        private String orderDate; // 주문 날짜
        private String orderStatus; // 주문 상태
        private int totalPrice; // 총 가격
		private List<OrderDetail> orderDetails;
		public void setOrderDetails(List<OrderDetail> orderDetails) {
			this.orderDetails = orderDetails;
			
		}
    }

    // 페이지네이션을 위한 DTO 클래스
    @Getter
    @ToString
    @AllArgsConstructor
    public static class Page {
        private Integer prev; // 이전 페이지 번호
        private Integer start; // 현재 페이지 시작 번호
        private Integer end; // 현재 페이지 끝 번호
        private Integer next; // 다음 페이지 번호
        private Integer pageno; // 현재 페이지 번호
        private List<OrderList> orders; // 현재 페이지의 주문 리스트
    }

    // 주문 조회에 사용되는 DTO 클래스
    @Data
    public static class Read {
        private Long orderNo; // 주문 번호
        private String orderDate; // 주문 날짜
        private int totalPrice; // 총 가격
        private String orderStatus; // 주문 상태
        private int usedPoint; // 사용된 포인트
        private int actPayment; // 실제 결제 금액
        private String memberUsername; // 회원 ID
        private Long postNo; // 배송지 번호

        private List<OrderDetail> orderDetails; // 주문에 대한 상세 정보 리스트
    }

    // 주문 수정에 사용되는 DTO 클래스
    @Data
    public static class Update {
        @NotNull
        private Long orderNo;
        @NotEmpty
        private String orderStatus;
        private List<OrderDetail> orderDetails; // 주문 상세 정보 리스트

        public Order toEntity() {
            return Order.builder().orderNo(orderNo).orderStatus(orderStatus).build();
        }

        public List<OrderDetail> getOrderDetails() {
            return orderDetails;
        }
    }
}
