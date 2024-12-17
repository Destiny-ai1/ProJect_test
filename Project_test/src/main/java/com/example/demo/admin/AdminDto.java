package com.example.demo.admin;

import lombok.Data;
import lombok.NoArgsConstructor;

public class AdminDto {

    @Data
    @NoArgsConstructor
    public static class User {
        private String username;
        private String name;
        private String email;
        private String phone;
        private String grade;
        private String joinDate;
        private boolean enabled;
        private String role;
        private String status;
    }
    @Data
    @NoArgsConstructor
    public static class Order {
        private Long orderNo;
        private String orderDate;
        private String customerName;
        private String customerPhone;
        private String customerAddress;
        private String orderStatus;
        private Double totalAmount;
        private Long itemNo;
        private String imgUrl; // 이미지 URL 필드 추가
    }


}
