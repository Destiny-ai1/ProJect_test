package com.example.demo.cart;

import jakarta.validation.constraints.*;
import lombok.*;

public class CartDto { // 장바구니 관련 데이터 전달 객체
    private CartDto() {}

    @Data
    // 장바구니에 상품을 추가하기 위한 DTO
    public static class Create { 
        @NotNull
        private Long itemNo; // 상품 번호
        @NotEmpty
        private String username; // 사용자 이름 (회원 ID)
        @NotNull
        private Long cartEa; // 장바구니에 담길 상품 개수
        @NotNull
        private Integer cartPrice; // 상품의 개별 가격
        @NotNull
        private Integer cartTotalPrice; // 상품의 총 가격
        @NotNull
        private String itemSize;
        // DTO를 Cart 엔티티로 변환하는 메소드
        public Cart toEntity() {
            return new Cart(itemNo, username, cartEa, cartPrice, cartTotalPrice, itemSize);
        }
    }

    @Data
    public static class Read {  // 장바구니 조회를 위한 DTO
        private Long itemNo;        // 상품 번호
        private String username;    // 사용자 이름 (회원 ID)
        private Long cartEa;         // 장바구니에 담긴 상품 개수
        private Integer itemPrice;      // 상품의 개별 가격
        private Integer cartTotalPrice; // 상품의 총 가격 (개별 가격 * 개수)
        private String itemIrum;    // 상품 이름 (item 패키지에서 가져옴)
        private String itemImage; // 상품 이미지 URL (image 패키지에서 가져옴)
        private String itemSize;    // 상품 사이즈
    }

    @Data
    public static class CartUpdateRequest {
        private Long itemNo; // 상품 번호는 Long 타입으로 변경
        private String username; // 사용자 이름(회원 ID)
        // private String itemSize; // 상품 사이즈
        private Long cartEa; // 수량
    }
    
    @Data
    public static class Update	{
    	private Long itemNo;			// 상품번호
    	private String itemIrum;		// 상품명
    	private Integer itemPrice;		// 상품 가격
    	private Long cartEa;			// 장바구니 수량
    	private String itemImage;		// 상품 이미지
    	private Integer cartTotalPrice; // 총 금액
    }
}
