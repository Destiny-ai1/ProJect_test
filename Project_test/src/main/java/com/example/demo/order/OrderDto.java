package com.example.demo.order;

import java.time.*;
import java.util.*;

import jakarta.validation.constraints.*;
import lombok.*;

public class OrderDto {
    private OrderDto() { }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        @NotNull
        private Long orderNo;
        @NotEmpty
        private String orderDate;
        @NotNull
        private int totalPrice;
        @NotEmpty
        private String orderStatus;
        private int usedPoint;
        private int actPayment;
        @NotEmpty
        private String memberUsername;
        @NotNull
        private Long postNo;

        public Order toEntity() {
            return new Order(orderNo, LocalDate.parse(orderDate), totalPrice, orderStatus, usedPoint, actPayment, memberUsername, postNo);
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class OrderList {
        private Long orderNo;
        private String orderDate;
        private String orderStatus;
        private int totalPrice;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Page {
        private Integer prev;
        private Integer start;
        private Integer end;
        private Integer next;
        private Integer pageno;
        private List<OrderList> orders;
    }

    @Data
    public static class Read {
        private Long orderNo;
        private String orderDate;
        private int totalPrice;
        private String orderStatus;
        private int usedPoint;
        private int actPayment;
        private String memberUsername;
        private Long postNo;
    }

    @Data
    public static class Update {
        @NotNull
        private Long orderNo;
        @NotEmpty
        private String orderStatus;

        public Order toEntity() {
            return Order.builder().orderNo(orderNo).orderStatus(orderStatus).build();
        }
    }
}
