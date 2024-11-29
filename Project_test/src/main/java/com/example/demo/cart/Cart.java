package com.example.demo.cart;

import org.springframework.stereotype.Component;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Component
public class Cart { // 장바구니 엔티티 클래스
    private Long itemNo; // 상품 번호
    private String username; // 사용자 이름 (회원 ID)
    private int cartEa; // 장바구니에 담긴 상품 개수
    private int cartPrice; // 장바구니에 담긴 상품의 개별 가격
    private int cartTotalPrice; // 장바구니에 담긴 상품의 총 가격 (개별 가격 * 개수)
	
}