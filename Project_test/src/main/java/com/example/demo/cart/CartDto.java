package com.example.demo.cart;

import jakarta.validation.constraints.*;
import lombok.*;

public class CartDto { // 장바구니 관련 데이터 전달 객체
    private CartDto() {}

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create { // 장바구니에 상품을 추가하기 위한 DTO
        @NotNull
        private Long itemNo; // 상품 번호
        @NotEmpty
        private String username; // 사용자 이름 (회원 ID)
        @NotNull
        private int cartEa; // 장바구니에 담길 상품 개수
        @NotNull
        private int cartPrice; // 상품의 개별 가격
        @NotNull
        private int cartTotalPrice; // 상품의 총 가격

        public Cart toEntity() { // DTO를 Cart 엔티티로 변환하는 메소드
            return new Cart(itemNo, username, cartEa, cartPrice, cartTotalPrice);
        }
    }

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Read { // 장바구니 조회를 위한 DTO
        private Long itemNo; // 상품 번호
        private String username; // 사용자 이름 (회원 ID)
        private int cartEa; // 장바구니에 담긴 상품 개수
        private int cartPrice; // 상품의 개별 가격
        private int cartTotalPrice; // 상품의 총 가격
        private String itemIrum; // 상품 이름 (item 패키지에서 가져옴)
        private String imageName; // 상품 이미지 URL (image 패키지에서 가져옴)
        // 추가: setItemName 메서드
        public void setItemIrum(String itemIrum) {
            this.itemIrum = itemIrum;
        }

        // 추가: setItemImageUrl 메서드
        public void setImageName(String imageName) {
            this.imageName = imageName;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update { // 장바구니에 담긴 상품 정보를 수정하기 위한 DTO
        @NotNull
        private Long itemNo; // 상품 번호
        @NotEmpty
        private String username; // 사용자 이름 (회원 ID)
        @NotNull
        private int cartEa; // 수정할 상품 개수

        public Cart toEntity() { // DTO를 Cart 엔티티로 변환하는 메소드
            return Cart.builder().itemNo(itemNo).username(username).cartEa(cartEa).build();
        }
    }
}