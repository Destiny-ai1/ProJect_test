package com.example.demo.item;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 상품 엔티티
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
@Entity
@Table(name = "items")
public class Item {
	@Id
    public Long itemNo; // 상품 고유 번호
    public String itemIrum; // 상품명
    
    @Setter
    @Lob
    public String itemInfo; // 상품 정보
    
    public Integer itemPrice; // 상품 가격
    public Integer itemJango; // 재고 수량
    public Integer itemSellQty; // 판매된 수량
    public String itemSize; // 사이즈
    public Integer reviewEa; // 리뷰 갯수
    public Integer cno; // 카테고리 ID
    
    // Item 엔티티에 상태 변경 메소드 추가
    public void updateSellQtyAndReview(Integer sellQty, Integer reviewCount) {
        this.itemSellQty = sellQty != null ? sellQty : 0;  // null이면 0으로 설정
        this.reviewEa = reviewCount != null ? reviewCount : 0;  // null이면 0으로 설정
    }
}
